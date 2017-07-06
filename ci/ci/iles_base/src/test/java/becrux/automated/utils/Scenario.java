//package becrux.automated.utils;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.nio.file.Files;
//import java.util.Calendar;
//import java.util.Date;
//
//import com.ericsson.becrux.iles.value.IlesDao;
//import org.apache.commons.io.FileUtils;
//import org.joda.time.DateTime;
//import org.jvnet.hudson.test.JenkinsRule;
//
//import com.ericsson.becrux.common.configuration.IlesDirectory;
//import com.ericsson.becrux.common.value.CommonDao;
//import com.ericsson.becrux.common.data.BaselineVoting;
//import com.ericsson.becrux.common.eiffel.EiffelEventSender;
//import com.ericsson.becrux.common.loop.PhaseStatus;
//import com.ericsson.becrux.common.loop.jobs.ControllerJob;
//import com.ericsson.becrux.common.utils.JenkinsGlobalConfig;
//
//import hudson.triggers.TimerTrigger;
//
//public abstract class Scenario implements TemplateForScenario{
//
//	private JenkinsRule jenkins;
//	private JenkinsJobsSetting jobs;
//	private int countBuildWorker=0;
//	private int countBuildPhoenix=0;
//	private int countBuildTestExec=0;
//	private int countBuildController=0;
//	private PhaseStatus statusScenario = PhaseStatus.INCONCLUSIVE;
//	private String error="";
//
//	public static String nameFileOfArtifact = "artifact.tar";
//
//	protected EiffelEventSender sender;
//	protected CommonDao value;
//	protected IlesDao ilesDao;
//
//	public Scenario(JenkinsRule rule, JenkinsJobsSetting jobs, EiffelEventSender sender){
//		this.jenkins=rule;
//		this.jobs=jobs;
//		this.sender=sender;
//	}
//
//	public void execution(){
//		String title = "SCENARIO-"+this.getClass().getSimpleName();
//		printTestHead(title);
//		try {
//			cleanBeforeScenario();
//			resetCountBuild();
//			baselineLoop();
//			assertionDAO();
//			assertionJobs();
//			statusScenario = PhaseStatus.OK;
//		} catch (AssertionError e) {
//		    statusScenario = PhaseStatus.FAILURE;
//		    StringWriter sw = new StringWriter();
//		    e.printStackTrace(new PrintWriter(sw));
//		    error=sw.toString();
//		    e.printStackTrace();
//		}catch (Exception e) {
//		    statusScenario = PhaseStatus.ERROR;
//		    StringWriter sw = new StringWriter();
//		    e.printStackTrace(new PrintWriter(sw));
//		    error=sw.toString();
//		    e.printStackTrace();
//		}finally{
//			cleanAfterScenario();
//			System.out.println("[INFO] " + title + ": "+statusScenario);
//		}
//	}
//
//	@Override
//	public void baselineLoop(){};
//	@Override
//	public void assertionDAO(){};
//	@Override
//	public void assertionJobs(){};
//
//
//	/*
//	 * Method for running this scenario
//	 */
//	private void wait(int msecond){
//		try {
//			Thread.sleep(msecond);//waitForEvent
//			jenkins.waitUntilNoActivity();
//		} catch (Exception e) {
//		}
//	}
//
//	public void waiting(){
//		System.out.println("==Waiting[5s] to ensure that Eiffel has triggered Controller [status=start]");
//		wait(5000);
//		System.out.println("==Waiting[5s] to ensure that Eiffel has trigerred Controller [status=stop]");
//	};
//
//	private void cleanBeforeScenario() throws Exception {
//		jobs.CONTROLLER.build();
//		wait(0);
//		regenerateDB();
//		wait(0);
//		System.out.println("Cleaned Queue on RabbitMq");
//	}
//
//	private void regenerateDB() throws Exception {
//		JenkinsGlobalConfig cfg = new JenkinsGlobalConfig();
//		IlesDirectory dir = cfg.getIlesDirectory();
//		File db = dir.getDbDir();
//		FileUtils.deleteDirectory(db);
//		db.mkdir();
//		cfg.setIlesDirectory(new IlesDirectory(dir.getBaseDir().getAbsolutePath(), true));
//		value = dir.getValue();
//		ilesDao = dir.getIlesDao();
//	}
//
//	private void cleanAfterScenario(){
//		File artifacts = new File(nameFileOfArtifact);
//		if (artifacts.exists())
//			try {
//				Files.delete(artifacts.toPath());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		jobs.PHOENIX.setStatusBuild(true);
//		jobs.TESTEXEC.setStatusBuild(true);
//		jobs.PHOENIX.setToNeverEnd(false);
//		jobs.TESTEXEC.setToNeverEnd(false);
//		jobs.TESTEXEC.setResultTest(false);
//	}
//
//	private static void printTestHead(String title) {
//		String line = new String(new char[title.length()+4]).replace("\0", "#");
//		System.out.println("\n" + line + "\n# " + title + " #\n" + line + "\n");
//	}
//
//
//	/*
//	 * Method operated on count of builds JOB
//	 */
//	public int getBuildCountController(){return jobs.CONTROLLER.getCountBuild()-countBuildController;}
//	public int getBuildCountPhoenix(){return jobs.PHOENIX.getCountBuild()-countBuildPhoenix;}
//	public int getBuildCountTestExec(){return jobs.TESTEXEC.getCountBuild()-countBuildTestExec;}
//	public int getBuildCountWorker(){return jobs.WORKER.getCountBuild()-countBuildWorker;}
//
//	private void resetCountBuild(){
//		countBuildController=jobs.CONTROLLER.getCountBuild();
//		countBuildPhoenix=jobs.PHOENIX.getCountBuild();
//		countBuildTestExec=jobs.TESTEXEC.getCountBuild();
//		countBuildWorker=jobs.WORKER.getCountBuild();
//	}
//
//
//	/*
//	 * Method operated on information about this scenario
//	 */
//	public String getInformationAboutScenario(){
//		String msg = this.getClass().getSimpleName()+" finished with status: "+statusScenario.name();
//		if(statusScenario!=PhaseStatus.OK){
//			msg+="\n"+error;
//			printInformationAboutBuilds();
//		}
//		return msg;
//	}
//	public void printInformationAboutBuilds(){
//		System.out.println(jobs.CONTROLLER.isOKBuild(true, countBuildController));
//		System.out.println(jobs.WORKER.isOKBuild(true, countBuildWorker));
//		System.out.println(jobs.TESTEXEC.isOKBuild(true,countBuildTestExec));
//		System.out.println(jobs.PHOENIX.isOKBuild(true, countBuildPhoenix));
//	}
//	public PhaseStatus getStatus(){return statusScenario;}
//
//
//	/*
//	 * Method return status of builds JOB
//	 */
//	public boolean verifyControllerBuilds(){return jobs.CONTROLLER.isOKBuild(countBuildController+1);}
//	public boolean verifyWorkerBuilds(){return jobs.WORKER.isOKBuild(countBuildWorker+1);}
//	public boolean verifyPhoenixBuilds(){return jobs.PHOENIX.isOKBuild(countBuildPhoenix+1);}
//	public boolean verifyTestexecBuilds(){return jobs.TESTEXEC.isOKBuild(countBuildTestExec+1);}
//
//
//	/*
//	 * Method operate trigger in Controller
//	 */
//	public void setDeadlineVotingAndTrigerring(){
//		try {
//			ControllerJob contr = new ControllerJob();
//			TimerTrigger trigger = contr.getProject().getTrigger(TimerTrigger.class);
//			System.out.println("Time:" + trigger.getSpec());
//			ControllerJob.removeTimerTrigger();
//			DateTime dateTime = new DateTime();
//			if(dateTime.getSecondOfMinute()>55)dateTime = dateTime.plusMinutes(2);
//			else dateTime = dateTime.plusMinutes(1);
//			BaselineVoting voting = ilesDao.loadLatestVoting();
//			voting.setDeadline(dateTime.toDate());
//			ilesDao.saveLatestVoting(voting);
//			trigger = new TimerTrigger(convertDateToTrigger(dateTime.toDate()));
//			trigger.start(contr.getProject(), true);
//			contr.getProject().addTrigger(trigger);
//			System.out.println("[New] Time:" + trigger.getSpec());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private String convertDateToTrigger(Date deadline){
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(deadline);
//		return new StringBuilder()
//				.append(cal.get(Calendar.MINUTE))
//				.append(" ")
//				.append(cal.get(Calendar.HOUR_OF_DAY))
//				.append(" ")
//				.append(cal.get(Calendar.DAY_OF_MONTH))
//				.append(" ")
//				.append(cal.get(Calendar.MONTH) + 1)
//				.append(" *")
//				.toString();
//	}
//
//}
