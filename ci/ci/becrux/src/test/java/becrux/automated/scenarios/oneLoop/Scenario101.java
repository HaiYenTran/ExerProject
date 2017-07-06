//package becrux.automated.scenarios.oneLoop;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//import java.io.IOException;
//
//import org.jvnet.hudson.test.JenkinsRule;
//
//import com.ericsson.becrux.common.data.BaselineVoting;
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
//public class Scenario101 extends Scenario implements TemplateForScenario{
//
//	private JenkinsJobsSetting jobs;
//	private Node node1 = new Mtas(Version.create("2.0"));
//
//	public Scenario101(JenkinsRule rule, JenkinsJobsSetting jobs, EiffelEventSender sender) {
//		super(rule, jobs, sender);
//		this.jobs=jobs;
//	}
//
//	@Override
//	public void baselineLoop() {
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
//			jobs.voting(false);
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
//		BaselineVoting bv;
//		try {
//			bv = value.readLatestBaselineVoting();
//			assertEquals(false, bv.getVote());
//			//TODO add assertion for ApprovedBaseline
//
//			Component node1AfterNBP = value.loadComponent(node1.getType(), node1.getVersion().getVersion());
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
