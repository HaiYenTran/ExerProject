package com.ericsson.becrux.base.common.loop;

import com.ericsson.becrux.base.common.core.NwftDownstreamJob;
import hudson.model.Result;

import javax.annotation.Nonnull;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by ematwie on 2016-12-13.
 * TODO: Review the way to use
 */
public class JobsScheduler {

    private PrintStream log;
    private Set<JobThread> jobThreads;

    /**
     * Create the scheduler of Jenkins jobs. They will be run parallel if Jenkins configuration allow.
     *
     * @param log print stream object
     * @param jobsMap map with downstream jobs as keys and operation to do
     */
    public JobsScheduler(@Nonnull PrintStream log, @Nonnull Map<NwftDownstreamJob, Operation> jobsMap) {
        this.log = log;
        this.jobThreads = new HashSet<>();
        for (Map.Entry<NwftDownstreamJob, Operation> entry : jobsMap.entrySet())
            this.jobThreads.add(new JobThread(log, entry.getKey(), entry.getValue()));
    }

    /**
     * Create the scheduler of one Jenkins job.
     *
     * @param log print stream object
     * @param job downstream job object to run
     * @param operation operation to do
     */
    public JobsScheduler(@Nonnull PrintStream log, @Nonnull NwftDownstreamJob job, @Nonnull Operation operation) {
        this.log = log;
        this.jobThreads = new HashSet<>();
        this.jobThreads.add(new JobThread(log, job, operation));
    }

    /**
     * Run job(s) with chosen type of operation and print the results of each job
     *
     * @return the worst {@link Result build result} of downstream jobs
     */
    public Result run() {

        log.println();
        Set<Thread> threads = new HashSet<>();
        for (JobThread jobThread : jobThreads) {
            // create list of downstream jobs threads
            threads.add(new Thread(jobThread));
        }

        try {
            for (Thread t : threads) {
                t.start(); //start all threads
            }
            for (Thread t : threads) {
                t.join(); //wait for all threads
            }
        } catch (InterruptedException e){
            // kill all threads in case of interruption
            for (Thread t : threads) {
                t.interrupt();
                try {
                    t.join(); //wait until it is killed
                } catch (InterruptedException e1) {
                    e.addSuppressed(e1);
                }
            }
            log.println("Jobs scheduler interrupted:");
            e.printStackTrace(log);
        }

        return printJobsInfoAndGetResult();
    }

    /**
     * Print information about triggered builds and possible exceptions stack traces;
     * get the results of builds an choose the worst one
     * @return the worst {@link Result build result} of downstream jobs
     */
    private Result printJobsInfoAndGetResult() {
        Result result = Result.SUCCESS;

        log.println();
        log.println("Downstream jobs results:");
        log.println();

        for (JobThread jobThread : jobThreads) {

            if (jobThread.operation == Operation.WAIT_FOR_FINISH) {
                if (jobThread.job.getResult() == null) {
                    log.println(jobThread.job.getDescription() + ": can be still running, because the status in unknown!!!");
                    result = result.combine(Result.FAILURE);
                }
                else {
                    log.println(jobThread.job.getDescription() + ": " + jobThread.job.getResult());
                    result = result.combine(jobThread.job.getResult());
                }
            } else {
                if (jobThread.exception == null) {
                    if (jobThread.operation == Operation.SCHEDULE)
                        log.println(jobThread.job.getDescription() + ": scheduled properly.");
                    else
                        log.println(jobThread.job.getDescription() + ": started properly.");
                    result = result.combine(Result.SUCCESS);
                } else {
                    result = result.combine(Result.FAILURE);
                }
            }

            if (jobThread.exception != null) {
                log.println("Error Stacktrace:");
                jobThread.exception.printStackTrace(log);
            }
            log.println();
        }
        return result;
    }


    private class JobThread implements Runnable {

        private PrintStream log;
        private NwftDownstreamJob job;
        private Operation operation;
        private Exception exception;

        public JobThread(@Nonnull PrintStream log, @Nonnull NwftDownstreamJob job, @Nonnull Operation operation) {
            this.log = log;
            this.job = job;
            this.operation = operation;
        }

        @Override
        public void run() {
           if (job.schedule()) {
               log.println(job.getDescription() + ": new build scheduled.");
               if (operation != Operation.SCHEDULE) {
                   try {
                       job.waitForStart();
                       log.println(job.getDescription() + ": build started: " + job.getBuild() + ": " + job.getFullBuildLink());
                   } catch (Exception e) {
                       log.println(job.getDescription() + ": cannot start the build.");
                       exception = e;
                       return;
                   }
                   if (operation == Operation.WAIT_FOR_FINISH) {
                       try {
                           job.waitForFinish();
                       } catch (Exception e) {
                           exception = e;
                           return;
                       } finally {
                           log.println(job.getDescription() + ": build " + job.getBuild() + " finished with status " + job.getResult());
                       }
                   }
               }
           } else {
               String msg = job.getDescription() + ": cannot schedule the build.";
               log.println(msg);
               exception = new Exception(msg);
           }
        }
    }

    public enum Operation {
        SCHEDULE, WAIT_FOR_START, WAIT_FOR_FINISH
    }

}
