//package becrux.automated.utils.jobs;
//
//import org.jvnet.hudson.test.JenkinsRule;
//
//public class Phoenix extends JobJenkins{
//
//	private static final String nameOfJob="PHOENIX";
//	private JenkinsRule jenkins;
//
//	public Phoenix(JenkinsRule jenkins) {
//		super(jenkins, nameOfJob);
//		this.jenkins = jenkins;
//	}
//
//	@Override
//	public void create() {
//		try {
//			job = jenkins.createFreeStyleProject(nameOfJob);
//			job.setConcurrentBuild(true);
//			job.save();
//			jenkins.waitUntilNoActivity();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//}
