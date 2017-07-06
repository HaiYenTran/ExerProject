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
//public class Scenario11 extends Scenario implements TemplateForScenario{
//
//	private JenkinsJobsSetting jobs;
//	private Node node = new Mtas(Version.create("2.0"));
//
//	public Scenario11(JenkinsRule rule, JenkinsJobsSetting jobs, EiffelEventSender sender) {
//		super(rule, jobs, sender);
//		this.jobs=jobs;
//	}
//
//	@Override
//	public void baselineLoop() {
//		jobs.TESTEXEC.setStatusBuild(false);
//		try {
//			jobs.startingTest(node, true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		waiting();//wait for ITR
//		waiting();//wait for BTF
//		jobs.TESTEXEC.setStatusBuild(true);
//
//		try {
//			jobs.voting(false);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		waiting();//wait for OPB
//		waiting();//wait for NBP
//
//		try {
//			jobs.startingTest(node, true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		waiting();//wait for ITR
//		waiting();//wait for BTF
//
//		try {
//			jobs.voting(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		waiting();//wait for OPB
//		waiting();//wait for NBP
//	}
//
//	@Override
//	public void assertionDAO() {
//		try {
//			//TODO add assertion for ApprovedBaseline
//
//			Component node1AfterNBP = value.loadComponent(node.getType(), node.getVersion().getVersion());
//			assertEquals(Component.State.BASELINE_APPROVED, node1AfterNBP.getState());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void assertionJobs() {
//		// verify count run in JOBs
//		assertEquals(2, getBuildCountWorker());
//		assertEquals(8, getBuildCountPhoenix());
//		assertEquals(2, getBuildCountTestExec());
//
//		// verify status build in JOBs
//		assertTrue(verifyControllerBuilds());
//		assertTrue(verifyPhoenixBuilds());
//	}
//}
