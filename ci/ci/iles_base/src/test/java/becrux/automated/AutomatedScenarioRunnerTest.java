//
//package becrux.automated;
//
//import static org.junit.Assert.assertEquals;
//
//import java.util.LinkedList;
//import java.util.List;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.jvnet.hudson.test.JenkinsRule;
//
//import com.ericsson.becrux.common.eiffel.EiffelEventSender;
//import com.ericsson.becrux.common.loop.PhaseStatus;
//
//import becrux.automated.scenarios.oneLoop.*;
//import becrux.automated.utils.JenkinsJobsSetting;
//import becrux.automated.utils.LoggerScenario;
//import becrux.automated.utils.Scenario;
//
//public class AutomatedScenarioRunnerTest {
//
//	@Rule
//	public JenkinsRule j = new JenkinsRule();
//
//	private JenkinsJobsSetting jobs;
//	private List<Scenario> scenarios;
//	private LoggerScenario logger;
//	private EiffelEventSender sender;
//
//	@Before
//	public void configuration() throws Throwable {
//		j.timeout=0;
//		j.after();
//		j.before();
//		j.getInstance().getUpdateCenter().updateAllSites();
//		jobs = new JenkinsJobsSetting(j);
//		scenarios = new LinkedList<Scenario>();
//		logger = new LoggerScenario();
//		sender = new EiffelEventSender();
//	}
//
//	@After
//	public void cleanUp() throws Exception{
//		Exception ex = new Exception ("Failed to clean up automated scenario data.");
//		try {
//			sender.close();
//		}
//		catch(Exception e1) {
//			ex.addSuppressed(e1);
//		}
//		try {
//			jobs.close();
//		} catch (Exception e2) {
//			ex.addSuppressed(e2);
//		}
//		if (ex.getSuppressed().length > 0)
//			throw ex;
//	}
//
//	@Test
//	public void scenarios() throws Exception {
//
//		jobs.initializeConfiguration();
//
//		scenarios.add(new Scenario1(j, jobs, sender));
//		scenarios.add(new Scenario2(j, jobs, sender));
//		scenarios.add(new Scenario3(j, jobs, sender));
//		scenarios.add(new Scenario4(j, jobs, sender));
//		scenarios.add(new Scenario5(j, jobs, sender));
//		scenarios.add(new Scenario7(j, jobs, sender));
//		scenarios.add(new Scenario8(j, jobs, sender));
//		scenarios.add(new Scenario9(j, jobs, sender));
//		scenarios.add(new Scenario10(j, jobs, sender));
//		scenarios.add(new Scenario11(j, jobs, sender));
//		scenarios.add(new Scenario12(j, jobs, sender));
//		scenarios.add(new Scenario101(j, jobs, sender));
//		scenarios.add(new Scenario102(j, jobs, sender));
//		scenarios.add(new Scenario103(j, jobs, sender));
//		scenarios.add(new Scenario104(j, jobs, sender));
//		scenarios.add(new Scenario105(j, jobs, sender));
//		scenarios.add(new Scenario13(j, jobs, sender));
//
//		run();
//		verify();
//	}
//
//	private void run(){
//		for(Scenario scenario: scenarios){
//			scenario.execution();
//			try {
//				j.waitUntilNoActivity();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	private void verify(){
//		String newLine = System.lineSeparator();
//		StringBuilder logBuilder = new StringBuilder("=============RESULT OF SCENARIOS=============");
//		logBuilder.append(newLine);
//
//		for(Scenario scenario:scenarios){
//			logBuilder.append(scenario.getInformationAboutScenario());
//			logBuilder.append(newLine);
//		}
//		logBuilder.append("=============================================");
//		String log = logBuilder.toString();
//		logger.addLog(log);
//		System.out.println(log);
//		for(Scenario scenario:scenarios){
//			assertEquals(PhaseStatus.OK,scenario.getStatus());
//		}
//	}
//
//}
