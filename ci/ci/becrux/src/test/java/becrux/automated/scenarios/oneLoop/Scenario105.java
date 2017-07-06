//package becrux.automated.scenarios.oneLoop;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//import java.io.IOException;
//
//import org.jvnet.hudson.test.JenkinsRule;
//
//import com.ericsson.becrux.common.data.Component;
//import com.ericsson.becrux.iles.data.Mtas;
//import com.ericsson.becrux.common.data.Node;
//import com.ericsson.becrux.common.data.Version;
//import com.ericsson.becrux.common.eiffel.EiffelEventSender;
//
//import becrux.automated.utils.JenkinsJobsSetting;
//import becrux.automated.utils.Scenario;
//import becrux.automated.utils.TemplateForScenario;
//
//public class Scenario105 extends Scenario implements TemplateForScenario{
//
//	private JenkinsJobsSetting jobs;
//	private Node node1 = new Mtas(Version.create("2.0"));
//
//	public Scenario105(JenkinsRule rule, JenkinsJobsSetting jobs, EiffelEventSender sender) {
//		super(rule, jobs, sender);
//		this.jobs=jobs;
//	}
//
//	@Override
//	public void baselineLoop() {
//		try {
//			jobs.startingTest(node1, true,false);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		waiting();//wait for ITR
//		waiting();//wait for BTF
//	}
//
//	@Override
//	public void assertionDAO() {
//		try {
//			Component comp = value.loadComponent(node1.getType(), ((Mtas)node1).getVersion().getVersion());
//			assertEquals(null, comp);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void assertionJobs() {
//		// verify count run in JOBs
//		assertEquals(0, getBuildCountWorker());
//		assertEquals(0, getBuildCountPhoenix());
//		assertEquals(0, getBuildCountTestExec());
//
//		// verify status build in JOBs
//		assertTrue(verifyControllerBuilds());
//		assertTrue(verifyWorkerBuilds());
//		assertTrue(verifyPhoenixBuilds());
//		assertTrue(verifyTestexecBuilds());
//	}
//}
