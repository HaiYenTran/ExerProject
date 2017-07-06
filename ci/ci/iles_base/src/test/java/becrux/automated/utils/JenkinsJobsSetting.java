//package becrux.automated.utils;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import com.ericsson.becrux.iles.testexec.TestExecutionBuildStep;
//import org.jvnet.hudson.test.JenkinsRule;
//
//import com.ericsson.becrux.iles.data.Int;
//import com.ericsson.becrux.iles.data.Mtas;
//import com.ericsson.becrux.common.data.Node;
//import com.ericsson.becrux.common.data.Version;
//import com.ericsson.becrux.common.eiffel.EiffelEventSender;
//import com.ericsson.becrux.iles.eiffel.events.ITREvent;
//import com.ericsson.becrux.iles.eiffel.events.NTAEvent;
//import com.ericsson.becrux.iles.eiffel.events.OPBEvent;
//import com.ericsson.duraci.eiffelmessage.sending.exceptions.EiffelMessageSenderException;
//
//import becrux.automated.utils.jobs.Controller;
//import becrux.automated.utils.jobs.Phoenix;
//import becrux.automated.utils.jobs.Provisioning;
//import becrux.automated.utils.jobs.TestExec;
//import becrux.automated.utils.jobs.Worker;
//
//public class JenkinsJobsSetting implements AutoCloseable {
//
//	private JenkinsRule j;
//	private JenkinsSetting setting;
//
//	public Controller CONTROLLER;
//	public Phoenix PHOENIX;
//	public TestExec TESTEXEC;
//	public Worker WORKER;
//	public Provisioning PROVISIONING;
//
//	public JenkinsJobsSetting(JenkinsRule jenkinsRule) {
//		this.j = jenkinsRule;
//		this.setting = new JenkinsSetting(j);
//	}
//
//	public void initializeConfiguration() throws Exception {
//		setting.setJenkinsConfiguration(); //configure global  and create DB
//		createJobs();
//	}
//
//	private void createJobs() throws Exception{
//		//Create Jobs Object
//		CONTROLLER = new Controller(j);
//		WORKER = new Worker(j);
//		PHOENIX = new Phoenix(j);
//		TESTEXEC = new TestExec(j);
//		PROVISIONING = new Provisioning(j);
//
//		//Create Object;
//		CONTROLLER.create();
//		WORKER.create();
//		PHOENIX.create();
//		TESTEXEC.create();
//		PROVISIONING.create();
//
//
//		j.waitUntilNoActivity();
//	}
//
//	public TestExecutionBuildStep.DescriptorImpl getDescriptorTestExec() {
//		return setting.getDescriptorTestExec();
//	}
//
//	public void voting(boolean status) throws Exception{
//		try(EiffelEventSender sender = new EiffelEventSender()) {
//			OPBEvent opbEvent = new OPBEvent();
//			opbEvent.setVote(status);
//			opbEvent.setComment("comment");
//			opbEvent.setSignum("ESOMEBODY");
//
//			Node node = new Mtas(Version.create("2.0"));
//			opbEvent.setProducts(Arrays.asList(node.getType()));
//			opbEvent.setBaselines(Arrays.asList(node.getVersion()));
//			sender.sendEvent(opbEvent, "ILESGuardian");
//			System.out.println("ILES Guardian voting :"+status);
//			j.waitUntilNoActivity();
//		}
//	}
//
//	public void startingTest(Node node, boolean multiNode) throws Exception{
//		startingTest(node, multiNode, true);
//	}
//
//	public void startingTest(Node node, boolean multiNode, boolean emptyParams) throws Exception{
//		try(EiffelEventSender sender = new EiffelEventSender()) {
//			ITREvent intEvent = new ITREvent();
//			intEvent.setProduct(node.getType());
//			intEvent.setBaseline(node.getVersion());
//			File artifacts = new File(Scenario.nameFileOfArtifact);
//			try {
//				artifacts.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			String urlToartifacts="file://"+artifacts.getAbsolutePath();
//			intEvent.setArtifact(urlToartifacts);
//			List<String> listParams = new ArrayList<>();
//			// Temporary solution for adding configuration files
//			listParams.add(urlToartifacts);
//			if(emptyParams)intEvent.setParameters(listParams);
//			intEvent.setMultiNodeBaselineCandidate(multiNode);
//			sender.sendEvent(intEvent);
//		}
//	}
//
//	public void sendNTAEvent(Int i, boolean existFile) throws EiffelMessageSenderException{
//		try(EiffelEventSender sender = new EiffelEventSender()) {
//			NTAEvent nta = new NTAEvent();
//			nta.setBuildId("1");
//			nta.setProduct(i.getType());
//			nta.setBaseline(i.getVersion());
//			File artifacts = new File(Scenario.nameFileOfArtifact);
//			if(existFile){
//				try {
//					artifacts.createNewFile();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			String urlToartifacts="file://"+artifacts.getAbsolutePath();
//			nta.setArtifact(urlToartifacts);
//			List<String> listParams = new ArrayList<>();
//			// Temporary solution for adding configuration files
//			listParams.add(urlToartifacts);
//			nta.setParameters(listParams);
//			sender.sendEvent(nta);
//		}
//	}
//
//	@Override
//	public void close() throws Exception {
//
//		Exception ex = new Exception("Failed to close Jenkins jobs setting object properly.");
//
//		if (this.setting != null)
//			try {
//				this.setting.close();
//			} catch (Exception e) {
//				ex.addSuppressed(e);
//			}
//
//		if (CONTROLLER != null)
//			try {
//				CONTROLLER.delete();
//			} catch (Exception e) {
//				ex.addSuppressed(e);
//			}
//
//		if (PHOENIX != null)
//			try {
//				PHOENIX.delete();
//			} catch (Exception e) {
//				ex.addSuppressed(e);
//			}
//
//		if (WORKER != null)
//			try {
//				WORKER.delete();
//			} catch (Exception e) {
//				ex.addSuppressed(e);
//			}
//
//		if (TESTEXEC != null)
//			try {
//				TESTEXEC.delete();
//			} catch (Exception e) {
//				ex.addSuppressed(e);
//			}
//
//		if (PROVISIONING != null)
//			try {
//				PROVISIONING.delete();
//			} catch (Exception e) {
//				ex.addSuppressed(e);
//			}
//
//		if (ex.getSuppressed().length > 0)
//			throw ex;
//
//	}
//}
//
