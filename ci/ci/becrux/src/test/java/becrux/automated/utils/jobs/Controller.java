//package becrux.automated.utils.jobs;
//
//import java.util.LinkedList;
//import java.util.List;
//
//import com.ericsson.becrux.common.eiffel.EiffelEventReceiverBuildStep;
//import com.ericsson.becrux.common.eiffel.EiffelEventSenderBuildStep;
//import com.ericsson.becrux.iles.eventhandler.strategies.AllEventStrategyRegistrationBuildStep;
//import org.jvnet.hudson.test.JenkinsRule;
//
//import com.ericsson.becrux.common.eventhandler.EventHandlerBuildStep;
//import com.ericsson.duraci.rule.extension.JarFilePaths;
//import com.ericsson.duraci.rule.snippet.AbstractSnippet;
//import com.ericsson.duraci.rule.snippet.TextSnippetForRuleTrigger;
//import com.ericsson.duraci.rule.triggering.RuleTrigger;
//
//public class Controller extends JobJenkins{
//
//	private JenkinsRule jenkins;
//	private static final String nameOfJob="CONTROLLER";
//
//	public Controller(JenkinsRule jenkins) {
//		super(jenkins, nameOfJob);
//		this.jenkins=jenkins;
//	}
//
//	@Override
//	public void create(){
//		List<AbstractSnippet> rules = new LinkedList<AbstractSnippet>();
//		rules.add(new TextSnippetForRuleTrigger(""+
//				"declare EiffelMessage\n"+
//				"\t@role( event )\n"+
//				"\t@expires( 1m )\n"+
//				"end\n\n"+
//				"declare EiffelEvent\n"+
//				"\t@role( event )\n"+
//				"\t@expires( 1m )\n"+
//				"end\n\n"+
//				"rule \"Trigger\"\n"+
//				"\twhen\n"+
//				"\t\tm : EiffelMessage(eventType == \"EiffelGenericEvent\")\n"+
//				"\tthen\n"+
//				"\t\tqueue.wipe();\n"+
//				"\t\tqueue.unique(m);\n"+
//				"end"
//				));
//
//		RuleTrigger eiffelTrigger = new RuleTrigger(
//				"experimental.generic.*.eiffel021.seki.femTEST002Auto",
//				rules,
//				false,
//				"1000",
//				false,
//				new JarFilePaths(""));
//
//		try {
//			job = jenkins.createFreeStyleProject(nameOfJob);
//			job.getBuildersList().add(new EiffelEventReceiverBuildStep(null, "experimental.generic.*.eiffel021.seki.fem004", "5000"));
//			job.getBuildersList().add(new AllEventStrategyRegistrationBuildStep("WORKER", "test Pool", "240"));
//			job.getBuildersList().add(new EventHandlerBuildStep("5", "500"));
//			job.getBuildersList().add(new EiffelEventSenderBuildStep(null));
//			job.addTrigger(eiffelTrigger);
//			job.save();
//			jenkins.waitUntilNoActivity();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//}
