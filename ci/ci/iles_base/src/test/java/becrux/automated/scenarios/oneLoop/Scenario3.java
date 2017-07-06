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
//import com.ericsson.becrux.common.eventhandler.EventQueue;
//
//import becrux.automated.utils.JenkinsJobsSetting;
//import becrux.automated.utils.Scenario;
//import becrux.automated.utils.TemplateForScenario;
//
//public class Scenario3 extends Scenario implements TemplateForScenario{
//
//	private JenkinsJobsSetting jobs;
//	private Node node1 = new Mtas(Version.create("2.0"));
//	private Node node2 = new Cscf(Version.create("2.0"));
//
//	public Scenario3(JenkinsRule rule, JenkinsJobsSetting jobs, EiffelEventSender sender) {
//		super(rule, jobs, sender);
//		this.jobs=jobs;
//	}
//
//	@Override
//	public void baselineLoop() {
//
//		try {
//			jobs.startingTest(node1, true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		waiting();//wait for ITR
//		waiting();//wait for BTF
//
//		try {
//			jobs.startingTest(node2, true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		waiting();//wait for ITR2
//
//		EventQueue queue;
//		try {
//			queue = value.loadEventQueue();
//			assertEquals(1, queue.getEventList().size());
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//
//		try {
//			jobs.voting(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		waiting();//wait for OPB
//
//		int index=0;
//		try {
//			index = value.readLatestBaselineVoting().getBtf().getProducts().indexOf(node1.getType());
//			Version v = value.readLatestBaselineVoting().getBtf().getBaselines().get(index);
//			assertEquals(Version.create("2.0"), v);
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//
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
//			EventQueue queue = value.loadEventQueue();
//			assertEquals(0, queue.getEventList().size());
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
//		assertTrue(verifyWorkerBuilds());
//		assertTrue(verifyPhoenixBuilds());
//		assertTrue(verifyTestexecBuilds());
//	}
//}
