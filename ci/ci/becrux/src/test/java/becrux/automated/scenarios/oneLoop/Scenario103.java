//package becrux.automated.scenarios.oneLoop;
//
//import org.jvnet.hudson.test.JenkinsRule;
//
//import com.ericsson.becrux.iles.data.Int;
//import com.ericsson.becrux.common.data.Version;
//import com.ericsson.becrux.common.eiffel.EiffelEventSender;
//
//import becrux.automated.utils.JenkinsJobsSetting;
//import becrux.automated.utils.Scenario;
//import becrux.automated.utils.TemplateForScenario;
//
//public class Scenario103 extends Scenario implements TemplateForScenario{
//
//	private JenkinsJobsSetting jobs;
//	private Int int1 = new Int(Version.create("2.0"));
//
//	public Scenario103(JenkinsRule rule, JenkinsJobsSetting jobs, EiffelEventSender sender) {
//		super(rule, jobs, sender);
//		this.jobs=jobs;
//	}
//
//	@Override
//	public void baselineLoop() {
//		try {
//			jobs.sendNTAEvent(int1, false);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		waiting();//wait for NTA
//		waiting();//wait for NTF
//	}
//
//	@Override
//	public void assertionDAO() {
//		//TODO verify URL to INT
//	}
//
//	@Override
//	public void assertionJobs() {
//
//	}
//}
