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
//import com.ericsson.becrux.common.eventhandler.EventQueue;
//
//import becrux.automated.utils.JenkinsJobsSetting;
//import becrux.automated.utils.Scenario;
//import becrux.automated.utils.TemplateForScenario;
//
//public class Scenario2 extends Scenario implements TemplateForScenario{
//
//	private JenkinsJobsSetting jobs;
//	private Node node1 = new Mtas(Version.create("1.2"));
//	private Node node2 = new Mtas(Version.create("2.0"));
//
//	public Scenario2(JenkinsRule rule, JenkinsJobsSetting jobs, EiffelEventSender sender) {
//		super(rule, jobs, sender);
//		this.jobs=jobs;
//	}
//
//	@Override
//	public void baselineLoop() {
//
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
//		waiting();//waiting on secondNode ITR
//		waiting();//waiting on secondNode BTF
//	}
//
//	@Override
//	public void assertionDAO() {
//		try {
//			EventQueue queue = value.loadEventQueue();
//			assertEquals(0, queue.getEventList().size());
//			Component node1AfterNBP = value.loadComponent(node1.getType(), node1.getVersion().getVersion());
//			Component node2AfterNBP = value.loadComponent(node2.getType(), node2.getVersion().getVersion());
//			assertEquals(Component.State.BASELINE_CANDIDATE, node2AfterNBP.getState());
//			assertEquals(null, node1AfterNBP);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void assertionJobs() {
//		// verify count run in JOBs
//		assertEquals(1, getBuildCountWorker());
//		assertEquals(4, getBuildCountPhoenix());
//		assertEquals(1, getBuildCountTestExec());
//
//		// verify status build in JOBs
//		assertTrue(verifyControllerBuilds());
//		assertTrue(verifyWorkerBuilds());
//		assertTrue(verifyPhoenixBuilds());
//		assertTrue(verifyTestexecBuilds());
//	}
//}