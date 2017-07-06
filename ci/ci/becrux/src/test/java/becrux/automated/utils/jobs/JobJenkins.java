//package becrux.automated.utils.jobs;
//
//import java.io.IOException;
//import java.util.concurrent.ExecutionException;
//
//import org.jvnet.hudson.test.JenkinsRule;
//
//import hudson.model.FreeStyleBuild;
//import hudson.model.FreeStyleProject;
//import hudson.model.Result;
//import hudson.tasks.Shell;
//
//public abstract class JobJenkins{
//
//	public FreeStyleProject job=null;
//	public JenkinsRule jenkins;
//	private String nameOfJobs;
//
//	public JobJenkins(JenkinsRule jenkins, String nameOfJob){
//		this.jenkins = jenkins;
//		this.nameOfJobs = nameOfJob;
//	}
//
//	public void create() throws Exception {
//		job = jenkins.createFreeStyleProject(nameOfJobs);
//		job.save();
//		jenkins.waitUntilNoActivity();
//	}
//
//	public boolean build(){
//		try {
//			job.scheduleBuild2(0).waitForStart();
//			return true;
//		} catch (InterruptedException | ExecutionException e) {
//			return false;
//		}
//	}
//
//	public int getCountBuild(){
//		return (job.getNextBuildNumber() - 1);
//	}
//
//	@SuppressWarnings("deprecation")
//	public boolean isOKBuild(boolean enableLog, int startBuildnumber){
//		boolean status=true;
//		if(startBuildnumber<1)startBuildnumber=1;
//		if(startBuildnumber>getCountBuild())startBuildnumber=getCountBuild();
//		if(startBuildnumber>0){
//			for (int c = startBuildnumber; c <= getCountBuild(); c++) {
//				FreeStyleBuild build = job.getBuild(String.valueOf(c));
//				Result result = build.getResult();
//				System.out.println("Status "+job.getDisplayName()+"#"+c+" :"+result);
//				if(result!=Result.SUCCESS && result!=Result.UNSTABLE){
//					status=false;
//					try {
//						System.out.println("Log  from failed Build:\n"+build.getLog());
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//				else if(enableLog){
//					try {
//						System.out.println("Log  from failed Build:\n"+build.getLog());
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//
//		return status;
//	}
//
//	public boolean isOKBuild(int buildNumberStart){
//		return isOKBuild(false,buildNumberStart);
//	}
//
//	public void delete() throws IOException, InterruptedException{
//		job.delete();
//	}
//
//	public void changeState(boolean status) {
//		try {
//			if (status) {
//				job.enable();
//			} else {
//				job.disable();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void setStatusBuild(boolean statusBuild){
//		try {
//			if (statusBuild) {
//				job.getBuildersList().removeAll(Shell.class);
//			} else
//				job.getBuildersList().add(new Shell("false"));
//
//			job.save();
//			jenkins.waitUntilNoActivity();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void setToNeverEnd(boolean on)
//	{
//		try {
//			if (on) {
//				job.getBuildersList().add(new Shell("while true ; do sleep 10 ; done"));
//			} else {
//				job.getBuildersList().removeAll(Shell.class);
//			}
//
//			job.save();
//			jenkins.waitUntilNoActivity();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}
