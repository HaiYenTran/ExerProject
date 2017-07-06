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
//import com.ericsson.becrux.iles.data.Cscf;
//import com.ericsson.becrux.iles.data.Mtas;
//import com.ericsson.becrux.common.data.Node;
//import com.ericsson.becrux.common.data.Version;
//import com.ericsson.becrux.common.eiffel.EiffelEventSender;
//
//import becrux.automated.utils.JenkinsJobsSetting;
//import becrux.automated.utils.Scenario;
//import becrux.automated.utils.TemplateForScenario;
//
//public class Scenario12 extends Scenario implements TemplateForScenario{
//
//	private JenkinsJobsSetting jobs;
//	private Node node1 = new Mtas(Version.create("2.0"));
//	private Node node2 = new Cscf(Version.create("2.0"));
//
//	public Scenario12(JenkinsRule rule, JenkinsJobsSetting jobs, EiffelEventSender sender) {
//		super(rule, jobs, sender);
//		this.jobs=jobs;
//	}
//
//	@Override
//	public void baselineLoop() {
//		jobs.CONTROLLER.changeState(false);
//		try {
//			jobs.startingTest(node1, true);
//			jobs.startingTest(node2, true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		jobs.CONTROLLER.changeState(true);
//		jobs.CONTROLLER.build();
//
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
//		waiting();//wait for BTF2
//
//		try {
//			jobs.voting(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		waiting();//wait for OPB2
//		waiting();//wait for NBP2
//	}
//
//	@Override
//	public void assertionDAO() {
//		try {
//			//TODO add assertion for ApprovedBaseline
//			Component node1AfterNBP = value.loadComponent(node1.getType(), node1.getVersion().getVersion());
//			Component node2AfterNBP = value.loadComponent(node2.getType(), node2.getVersion().getVersion());
//			assertEquals(Component.State.BASELINE_APPROVED, node1AfterNBP.getState());
//			assertEquals(Component.State.BASELINE_APPROVED, node2AfterNBP.getState());
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
//		assertTrue(verifyTestexecBuilds());
//	}
//}
