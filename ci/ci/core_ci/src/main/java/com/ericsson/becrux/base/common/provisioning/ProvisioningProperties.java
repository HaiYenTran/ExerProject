package com.ericsson.becrux.base.common.provisioning;

import com.ericsson.becrux.base.common.vise.ViseChannel;

/**
 * Contain all properties that need to do the Provisioning.
 * @author DungB
 */
public class ProvisioningProperties {

    private String scriptPath;
    private ViseChannel viseChannel;
    boolean netconf;
    private String sslFilePath;
    private String additionalArgs;

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public ViseChannel getViseChannel() {
        return viseChannel;
    }

    public void setViseChannel(ViseChannel viseChannel) {
        this.viseChannel = viseChannel;
    }

    public boolean isNetconf() {
        return netconf;
    }

    public void setNetconf(boolean netconf) {
        this.netconf = netconf;
    }

    public String getSslFilePath() {
        return sslFilePath;
    }

    public void setSslFilePath(String sslFilePath) {
        this.sslFilePath = sslFilePath;
    }

    public String getAdditionalArgs() { return additionalArgs; }

    public void setAdditionalArgs(String additionalArgs) { this.additionalArgs = additionalArgs; }
}
