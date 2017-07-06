package com.ericsson.becrux.base.common.core;

import hudson.console.HyperlinkNote;
import hudson.model.*;
import hudson.model.queue.QueueTaskFuture;
import jenkins.model.Jenkins;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Created by ematwie on 2016-12-08.
 */
public class NwftDownstreamJob {

    private static final String protectionVarName = "MERGE_PROTECTION";
    private static final String protectionVarDescription = "This parameter protects the build to be merged with another one";

    private final Project<?, ?> project; //Jenkins job
    private final Queue queue; //Jenkins jobs queue
    private final Collection<Action> actions; //parameters actions for downstream build
    private final Cause cause; //cause of the build
    private final int delay; //time when job is in queue
    private final long timeout; //maximal time when job is in queue
    private final long timeoutWithDelay; //delay + timeout
    private final String description; //job custom description

    private AbstractBuild<?, ?> build; //downstream job
    private QueueTaskFuture<?> task; //future object waiting for build execution
    private Future<?> future; //future object waiting until build starts
    
    private JobPhase phase = JobPhase.NONE; //to get know which methods were executed
    
    private enum JobPhase {
        NONE(0),
        SCHEDULED(1),
        STARTED(2),
        FINISHED(3);

        private int ordinal;

        JobPhase(int ordinal) {
            this.ordinal = ordinal;
        }

    }

    /**
     * There is a bug in Jenkins, that we cannot run the builds with the same parameters at the same time,
     * because they can be merged. We add a fake string parameter value with unique identifier string to the build
     * to protect it from the merge with another one.
     *
     * @param actions the collections of actions provided in constructor
     * @return collections of actions with the unique parameter
     */
    private Collection<Action> protectFromMerge(Collection<Action> actions) {

        Collection<Action> collection = new ArrayList<>();

        collection.add(new ParametersAction(
                new StringParameterValue(protectionVarName, UUID.randomUUID().toString(), protectionVarDescription)));

        if (actions != null)
            collection.addAll(actions);

        return collection;
    }

    /**
     * Create NWFT downstream job.
     * 
     * @param jobName name of job existent in Jenkins
     * @param description job custom description
     * @param actions collection of action which will be transferred to downstream build
     * @param cause cause object to keep track of why the downstream build is started
     * @param delay the time when the job is in Jenkins queue
     * @param timeout the maximal time (after <tt>delay</tt>) when the job is in Jenkins queue 
     */
    public NwftDownstreamJob(@Nonnull String jobName, String description, Collection<Action> actions,
                             @Nonnull Cause cause, int delay, long timeout) {
        
    	Jenkins jenkins = Jenkins.getInstance();
        if (jenkins == null)
            throw new RuntimeException("Jenkins instance has not been started or was already shut down.");

        this.project = (Project<?,?>)jenkins.getItem(jobName);
        if (this.project == null)
            throw new NoSuchElementException("No such job name in Jenkins: " + jobName);

        this.description = (description == null) ? jobName : description;
        this.queue = jenkins.getQueue();
        this.actions = protectFromMerge(actions);
        this.cause = cause;
        this.delay = delay;
        this.timeout = timeout;
        
        if (delay < 0)
        	throw new IllegalArgumentException("Delay cannot be less than 0.");
        if (timeout < 0)
        	throw new IllegalArgumentException("Timeout cannot be less than 0.");
        if (timeout > Long.MAX_VALUE - delay)
        	throw new ArithmeticException("Overflow: Sum of timeout and delay is greater than max value.");
        
        this.timeoutWithDelay = delay + timeout;
        
    }

    /**
     * Create NWFT downstream job.
     *
     * @param jobName name of job existent in Jenkins
     * @param actions collection of action which will be transferred to downstream build
     * @param cause cause object to keep track of why the downstream build is started
     * @param delay the time when the job is in Jenkins queue
     * @param timeout the maximal time (after <tt>delay</tt>) when the job is in Jenkins queue
     */
    public NwftDownstreamJob(@Nonnull String jobName, Collection<Action> actions,
                             @Nonnull Cause cause, int delay, long timeout) {
        this(jobName, null, actions, cause, delay, timeout);
    }

    /**
     * schedule a build of downstream job
     * 
     * @return false if job has already been scheduled
     */
    public boolean schedule() {
        if (phase == JobPhase.NONE) {
            
        	if (actions == null)
                task = project.scheduleBuild2(delay, cause);
            else
                task = project.scheduleBuild2(delay, cause, actions);
            
        	future = task.getStartCondition();
        	phase = JobPhase.SCHEDULED;
            return true;
        }
        return false;
    }

    /**
     * wait for build starting (it will be scheduled if it has not done yet)
     * 
     * @return build object
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public AbstractBuild<?, ?> waitForStart() throws CancellationException, ExecutionException, InterruptedException, TimeoutException {
        
    	if (phase.ordinal > JobPhase.SCHEDULED.ordinal)
            return build;

        schedule();
        
        try {
            if (timeout > 0) {
                build = (AbstractBuild<?,?>) future.get(timeoutWithDelay, TimeUnit.SECONDS);
            } else {
                build = (AbstractBuild<?,?>) future.get();
            }
            phase = JobPhase.STARTED;
            return build;        	
        } catch (CancellationException | InterruptedException | ExecutionException | TimeoutException e) {
            try {
                stop();
            } catch (Exception e1) {
               e.addSuppressed(e1);
            }
            throw e;
        }
    }

    /**
     * wait for build starting (it will be started if it has not done yet)
     * 
     * @return
     * @throws InterruptedException
     * @throws TimeoutException
     * @throws ExecutionException
     */
    public AbstractBuild<?, ?> waitForFinish() throws CancellationException, InterruptedException, TimeoutException, ExecutionException {
        
        if (phase.ordinal > JobPhase.STARTED.ordinal)
            return build;

        waitForStart();

        try {
            task.get();
            phase = JobPhase.FINISHED;
            return build;
        } catch (CancellationException | InterruptedException | ExecutionException e) {
            try {
                stop();
            } catch (Exception e1) {
                e.addSuppressed(e1);
            }
            throw e;
        }
    }

    /**
     * stop the downstream job if it is in Jenkins queue or is building now
     * @return false if build cannot be stopped (because is not scheduled yet or is already finished)
     * @throws IOException
     * @throws ServletException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public boolean stop() throws IOException, ServletException, CancellationException, InterruptedException, ExecutionException {

    	boolean done = false;
    	
    	if (phase != JobPhase.NONE && phase != JobPhase.FINISHED) {
    		
    		for (Queue.Item i : queue.getItems())
    			if (i.getFuture() == task)
    				done = queue.cancel(i);
    		
    		if (build == null && !task.isDone()) {
    			build = (AbstractBuild<?, ?>) future.get();
    		}
    		
    		if (build != null) {    			
    			build.doStop();
    			while (build.isBuilding())
    				Thread.sleep(1000);
    			done = true;
    		}
    		
    		phase = JobPhase.FINISHED;
    	}
    	return done;
    }

    public boolean isInQueue() {
    	return !future.isDone();
    }
    
    public boolean isOngoing() {
        return future.isDone() && !task.isDone();
    }

    public boolean isFinished() {
        return task.isDone();
    }

    public Result getResult() {
        if (phase == JobPhase.FINISHED) {
            if (build == null)
                return Result.NOT_BUILT;
            else
                return build.getResult();
        } else {
            return null;
        }
    }

    public String getBuildLink(String text) {
        String s;
        try {
            if (text == null || text.isEmpty())
                text = build.getFullDisplayName();
            s = HyperlinkNote.encodeTo("/"+build.getUrl(), text);
        } catch (Exception e) {
            return text;
        }
        return s;
    }
    public String getFullBuildLink () {
        return Jenkins.getInstance().getRootUrl() + build.getUrl() + "consoleFull";
    }

    public AbstractBuild<?, ?> getBuild() {
        return build;
    }

    public Cause getCause() {
        return cause;
    }

    public Project<?, ?> getProject() {
        return project;
    }

    public String getDescription() {
        return description;
    }


}
