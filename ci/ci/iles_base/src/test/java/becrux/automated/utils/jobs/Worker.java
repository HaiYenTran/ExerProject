//package becrux.automated.utils.jobs;
//
//import org.jvnet.hudson.test.JenkinsRule;
//
//import com.ericsson.becrux.common.loop.LoopBuildStep;
//
//public class Worker extends JobJenkins{
//
//	private static final String nameOfJob="WORKER";
//
//	public Worker(JenkinsRule jenkins) {
//		super(jenkins, nameOfJob);
//	}
//
//	@Override
//	public void create(){
//		try {
//			job = jenkins.createFreeStyleProject(nameOfJob);
//			job.getBuildersList().add(new LoopBuildStep());
//			job.save();
//			jenkins.waitUntilNoActivity();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//}
