//package becrux.automated.utils;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.ericsson.becrux.iles.testexec.TestExecutionBuildStep;
//import org.apache.commons.io.FileUtils;
//import org.jvnet.hudson.test.JenkinsRule;
//
//import com.ericsson.becrux.common.configuration.GlobalParameterPlugin;
//import com.ericsson.becrux.common.configuration.IlesDirectory;
//import com.ericsson.becrux.common.configuration.ViseChannelField;
//import com.ericsson.becrux.common.eiffel.configuration.SecondaryBinding;
//import com.ericsson.becrux.iles.utils.IlesGuardian;
//import com.ericsson.duraci.configuration.EiffelJenkinsGlobalConfiguration;
//import com.ericsson.duraci.configuration.EiffelJenkinsGlobalConfiguration.EiffelJenkinsGlobalConfigurationDescriptor;
//
//import net.sf.json.JSONObject;
//
//public class JenkinsSetting implements AutoCloseable {
//
//	private String MessageBusHostname = "";
//	private String MessageBusExchangeName = "";
//	private boolean CreateIfNotExist = false;
//	private String MessageBusUsername = "";
//	private String MessageBusUserPassword = "";
//	private String ComponentName = "";
//	private String DomainId = "";
//	private String QueueLength = "";
//	private String LeoHost = "";
//
//	private String EmailAddress = "noreply@ericsson.com";
//	private String Signum = "signum";
//	private String bindingkey;
//	private String description;
//
//	private JenkinsRule j;
//
//
//	public JenkinsSetting(JenkinsRule jenkinsRule) {
//		this.j = jenkinsRule;
//	}
//
//	public void setJenkinsConfiguration() throws Exception {
//
//		j.waitUntilNoActivity();
//
//		//setting global value in Jenkins (ILES-global ; Eiffel-global)
//		setData();
//
//		j.jenkins.setNumExecutors(10);
//
//		//setting ILES global parameters
//		getDescriptor().setLeoUrl(LeoHost);
//		getDescriptor().setBaseDir(IlesDirectory.createTempIlesDirectory().getBaseDir().getAbsolutePath());
//
//		List<IlesGuardian> emailAddresses = new ArrayList<>();
//		emailAddresses.add(new IlesGuardian(EmailAddress,Signum));
//		getDescriptor().setIlesAddresses(emailAddresses);
//
//		List<SecondaryBinding> secondaryBindings = new ArrayList<>();
//		secondaryBindings.add(new SecondaryBinding(bindingkey, description));
//		getDescriptor().setSecondaryBindings(secondaryBindings);
//
//		List<ViseChannelField> listVise = new ArrayList<>();
//		listVise.add(new ViseChannelField("201"));
//		getDescriptor().setViseChannels(listVise);
//		//setting Eiffel global parameters
//		JSONObject ob = new JSONObject();
//		ob.put("messageBusHostName", MessageBusHostname);
//		ob.put("queueLength", QueueLength);
//		ob.put("messageBusExchangeName", MessageBusExchangeName);
//		ob.put("messageBusUserName", MessageBusUsername);
//		ob.put("messageBusPassword", MessageBusUserPassword);
//		ob.put("messageBusComponentName", ComponentName);
//		ob.put("domainId", DomainId);
//		ob.put("messageBusExchangeCreation", CreateIfNotExist);
//		getDescriptorEiffel().configure(null, ob);
//
//	}
//
//	private void setData()
//	{
//		MessageBusHostname = "amqps://mb001-eiffel021.rnd.ki.sw.ericsson.se";
//		MessageBusExchangeName = "eiffel021.seki.testexchangedemo.broadcast.durable";
//		CreateIfNotExist = true;
//		MessageBusUsername = "guest";
//		MessageBusUserPassword = "guest";
//		ComponentName = "femTEST002Auto-eiffel021";
//		DomainId = "eiffel021.seki.femTEST002Auto";
//		QueueLength = "100";
//		LeoHost = "http://esekiws5503.rnd.ki.sw.ericsson.se:8084";
//		bindingkey = "experimental.generic.iles.eiffel021.seki.fem004";
//		description = "MTAS";
//	}
//
//	private GlobalParameterPlugin.DescriptorImpl getDescriptor() {
//		return (GlobalParameterPlugin.DescriptorImpl) j.getInstance()
//				.getDescriptor(GlobalParameterPlugin.class);
//	}
//
//	public TestExecutionBuildStep.DescriptorImpl getDescriptorTestExec() {
//		return (TestExecutionBuildStep.DescriptorImpl) j.getInstance()
//				.getDescriptor(TestExecutionBuildStep.class);
//	}
//
//	private EiffelJenkinsGlobalConfigurationDescriptor getDescriptorEiffel() {
//		return (EiffelJenkinsGlobalConfiguration.EiffelJenkinsGlobalConfigurationDescriptor) j.getInstance()
//				.getDescriptor(EiffelJenkinsGlobalConfiguration.class);
//	}
//
//	@Override
//	public void close() throws Exception {
//		String s = getDescriptor().getBaseDir();
//		if (s != null && !s.isEmpty()) {
//			File baseDir = new File(s);
//			if (baseDir.exists())
//				FileUtils.deleteDirectory(baseDir);
//		}
//	}
//
//}
