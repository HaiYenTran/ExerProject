package com.ericsson.becrux.iles.phoenix;

import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.configuration.FormValidator;
import com.ericsson.becrux.base.common.data.Version;
import com.ericsson.becrux.base.common.data.VersionType;
import com.ericsson.becrux.base.common.utils.BecruxBuildBadgeAction;
import com.ericsson.becrux.base.common.loop.ComponentParameterValue;
import com.ericsson.becrux.base.common.vise.ViseChannel;
import com.ericsson.becrux.base.common.vise.parameters.ReservedViseChannelParameterValue;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tasks.Shell;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Phoenix Installation Build Step .
 */
public class PhoenixInstallationBuildStep extends NwftBuildStep {
    private static final String PREPARE_REMOTE_SSH = "prepare_remote_ssh";
    private static final String OVA_NAME = "ova_name";
    private static final String ATLAS_IP = "atlas_ip";
    private static final String ATLAS_USER = "atlas_user";
    private static final String ATLAS_PW = "atlas_pw";
    private static final String NODE_IP = "node_ip";
    private static final String NODE_USER = "node_user";
    private static final String NODE_PASSWORD = "node_password";
    private static final String PDB_SERVER_HOST = "pdb_server_host";
    private static final String PDB_SERVER_PORT = "pdb_server_port";
    private static final String PDB_USER = "pdb_user";
    private static final String PDB_PASSWORD = "pdb_password";
    private static final String NR_OF_SAMPLES = "nr_of_samples";
    private static final String PDB_TOOLS_PKG = "pdb_tools_pkg";
    private static final String VNF_PATH = "vnf_path";
    private static final String PHOENIX_EXECUTION_SCRIPT = "phoenixExecutionScript";
    private static final String CI_ENGINE_PARAM_FILE_NAME = "ci_engine.param";
    private static final String INT_NODE_NAME = "Int";

    private boolean useCustomParameters;
    private String prepare_remote_ssh;
    private String ova_name;
    private String atlas_ip;
    private String atlas_user;
    private String atlas_pw;
    private String node_ip;
    private String node_user;
    private String node_password;
    private String pdb_tools_pkg;
    private String pdb_server_host;
    private String pdb_server_port;
    private String pdb_user;
    private String pdb_password;
    private String nr_of_samples;
    private String vnf_path;
    private String phoenixExecutionScript;
    private HashMap<String, String> properties = new HashMap<>();

    @DataBoundConstructor
    public PhoenixInstallationBuildStep(boolean useCustomParameters, String prepare_remote_ssh, String ova_name, String atlas_ip,
                                        String atlas_user, String atlas_pw, String node_ip, String node_user, String node_password,
                                        String pdb_tools_pkg, String pdb_server_host, String pdb_server_port, String pdb_user,
                                        String pdb_password, String nr_of_samples, String vnf_path, String phoenixExecutionScript) {
        setUseCustomParameters(useCustomParameters);
        setPrepare_remote_ssh(prepare_remote_ssh);
        setOva_name(ova_name);
        setAtlas_ip(atlas_ip);
        setAtlas_user(atlas_user);
        setAtlas_pw(atlas_pw);
        setNode_ip(node_ip);
        setNode_user(node_user);
        setNode_password(node_password);
        setPdb_tools_pkg(pdb_tools_pkg);
        setPdb_server_host(pdb_server_host);
        setPdb_server_port(pdb_server_port);
        setPdb_user(pdb_user);
        setPdb_password(pdb_password);
        setNr_of_samples(nr_of_samples);
        setVnf_path(vnf_path);
        setPhoenixExecutionScript(phoenixExecutionScript);
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        try {

            List<Component> components = this.getAllNwftParametersOfType(build, ComponentParameterValue.class)
                    .stream().map(param -> param.getComponent()).collect(Collectors.toList());
            if (components == null || components.size() != 1)
                throw new Exception(); // TODO: fix later
            Component node = components.get(0);

            if (node == null) {
                throw new Exception("Node was not initialized!");
            }

            List<ViseChannel> channels = this.getAllNwftParametersOfType(build, ReservedViseChannelParameterValue.class)
                    .stream().map(param -> param.getViseChannel()).collect(Collectors.toList());
            if (channels == null || channels.size() != 1)
                throw new Exception(); // TODO: fix later
            ViseChannel channel = channels.get(0);

            build.addAction(
                    new BecruxBuildBadgeAction(
                            channel.getFullName() + ": " + node.getType().toString() + " " + node.getVersion().getVersion()));

            IlesGlobalConfig cfg = IlesGlobalConfig.getInstance();
            File phoenixSourceDir = new File(cfg.getIlesDirectory().getPhoenixDir().getPath());

            FilePath workspaceDir = build.getWorkspace();

            FilePath fp = new FilePath(phoenixSourceDir);
            fp.copyRecursiveTo(workspaceDir);

            FilePath phoenixExePath = new FilePath(new FilePath(workspaceDir, "bin"), "phx");

            String cmd = phoenixExePath.getRemote() + " " + getCommandParameters(node,channel );
            listener.getLogger().println("Executing Phoenix command : " + cmd);
            return new Shell(cmd).perform(build, launcher, listener);
/*
            IlesGlobalConfig cfg = new JenkinsGlobalConfig();
            Node node = (Node) cfg.getIlesDirectory().getValue().loadComponent(INT_NODE_NAME, getNodeVersion());



            BuildParametersExtractor extractor = new BuildParametersExtractor(build);
            Map<String, String> phoenixParams = new HashMap<>();

            phoenixParams.putAll(node.getDeployParameters());

            if (isUseCustomParameters()) {
                phoenixParams.putAll(getProperties());
            }

            phoenixParams.put("_PHOENIX_LOCATION", cfg.getIlesDirectory().getPhoenixLocation().getPath());

            extractor.addNwftParameters(new PhoenixParamenterValue(phoenixParams));

            // add all param to DT_PHOENIX\etc\ci_engine.param ??!!!!!!
            // TODO: review the path
            PhoenixConfigurator configurator = new PhoenixConfigurator();
            configurator.setParamFilesDirectory(new FilePath(cfg.getIlesDirectory().getPhoenixLocation()));
            Iterator iter = getProperties().entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
                configurator.addParameter(CI_ENGINE_PARAM_FILE_NAME, entry.getKey(), entry.getValue());
            }

            // Execute the shell script, BuildParameters can be access via Shell script ${param_name}
            return new Shell(getPhoenixExecutionScript()).perform(build, launcher, listener);
*/
        } catch (Exception e) {
            e.printStackTrace();
            listener.getLogger().println(e);
            return false;
        }
    }

    private boolean isCustomVersionByUser(Version version) {
        return version.getVersionType() == VersionType.CUSTOM;
    }

    private String getCommandParameters(Component node, ViseChannel viseChannel){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(node.getType().toLowerCase());
        stringBuilder.append("_");
        stringBuilder.append(viseChannel.getWholeNumber());
        stringBuilder.append(" ");

        if (isCustomVersionByUser(node.getVersion())) {
            stringBuilder.append(node.getVersion().getVersion());
            stringBuilder.append(" ");
            stringBuilder.append(node.getPdb() != null ? node.getPdb() : "");
        }
        else {
            stringBuilder.append("sw=");
            stringBuilder.append(node.getType().toUpperCase()); // phoenix need type to be uppercase !
            stringBuilder.append("_");
            stringBuilder.append(node.getVersion().getVersion().replace('/', '_'));
        }

        return stringBuilder.toString();
    }

    public boolean isUseCustomParameters() {
        return useCustomParameters;
    }

    public void setUseCustomParameters(boolean useCustomParameters) {
        this.useCustomParameters = useCustomParameters;
    }

    public String getPrepare_remote_ssh() {
        return prepare_remote_ssh;
    }

    public void setPrepare_remote_ssh(String prepare_remote_ssh) {
        properties.put(PREPARE_REMOTE_SSH, prepare_remote_ssh);
        this.prepare_remote_ssh = prepare_remote_ssh;
    }

    public String getOva_name() {
        return ova_name;
    }

    public void setOva_name(String ova_name) {
        properties.put(OVA_NAME, ova_name);
        this.ova_name = ova_name;
    }

    public String getAtlas_ip() {
        return atlas_ip;
    }

    public void setAtlas_ip(String atlas_ip) {
        properties.put(ATLAS_IP, atlas_ip);
        this.atlas_ip = atlas_ip;
    }

    public String getAtlas_user() {
        return atlas_user;
    }

    public void setAtlas_user(String atlas_user) {
        properties.put(ATLAS_USER, atlas_user);
        this.atlas_user = atlas_user;
    }

    public String getAtlas_pw() {
        return atlas_pw;
    }

    public void setAtlas_pw(String atlas_pw) {
        properties.put(ATLAS_PW, atlas_pw);
        this.atlas_pw = atlas_pw;
    }

    public String getNode_ip() {
        return node_ip;
    }

    public void setNode_ip(String node_ip) {
        properties.put(NODE_IP, node_ip);
        this.node_ip = node_ip;
    }

    public String getNode_user() {
        return node_user;
    }

    public void setNode_user(String node_user) {
        properties.put(NODE_USER, node_user);
        this.node_user = node_user;
    }

    public String getNode_password() {
        return node_password;
    }

    public void setNode_password(String node_password) {
        properties.put(NODE_PASSWORD, node_password);
        this.node_password = node_password;
    }

    public String getPdb_tools_pkg() {
        return pdb_tools_pkg;
    }

    public void setPdb_tools_pkg(String pdb_tools_pkg) {
        properties.put(PDB_TOOLS_PKG, pdb_tools_pkg);
        this.pdb_tools_pkg = pdb_tools_pkg;
    }

    public String getPdb_server_host() {
        return pdb_server_host;
    }

    public void setPdb_server_host(String pdb_server_host) {
        properties.put(PDB_SERVER_HOST, pdb_server_host);
        this.pdb_server_host = pdb_server_host;
    }

    public String getPdb_server_port() {
        return pdb_server_port;
    }

    public void setPdb_server_port(String pdb_server_port) {
        properties.put(PDB_SERVER_PORT, pdb_server_port);
        this.pdb_server_port = pdb_server_port;
    }

    public String getPdb_user() {
        return pdb_user;
    }

    public void setPdb_user(String pdb_user) {
        properties.put(PDB_USER, pdb_user);
        this.pdb_user = pdb_user;
    }

    public String getPdb_password() {
        return pdb_password;
    }

    public void setPdb_password(String pdb_password) {
        properties.put(PDB_PASSWORD, pdb_password);
        this.pdb_password = pdb_password;
    }

    public String getNr_of_samples() {
        return nr_of_samples;
    }

    public void setNr_of_samples(String nr_of_samples) {
        properties.put(NR_OF_SAMPLES, nr_of_samples);
        this.nr_of_samples = nr_of_samples;
    }

    public String getVnf_path() {
        return vnf_path;
    }

    public HashMap<String, String> getProperties() {
        return properties;
    }

    public void setVnf_path(String vnf_path) {
        properties.put(VNF_PATH, vnf_path);
        this.vnf_path = vnf_path;
    }

    public String getPhoenixExecutionScript() {
        return phoenixExecutionScript;
    }

    public void setPhoenixExecutionScript(String phoenixExecutionScript) {
        properties.put(PHOENIX_EXECUTION_SCRIPT, phoenixExecutionScript);
        this.phoenixExecutionScript = phoenixExecutionScript;
    }


    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link PhoenixInstallationBuildStep}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an
    // extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        public FormValidation doCheckNr_of_samples(@QueryParameter String value) throws IOException, ServletException {
            return FormValidator.isCorrectNumber(value);
        }

        public FormValidation doCheckPdb_tools_pkg(@QueryParameter String value) throws IOException, ServletException {
            return FormValidator.isFile(value);
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project
            // types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "ILES: Use Phoenix to install nodes";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);
            save();
            return super.configure(req, formData);
        }

    }
}
