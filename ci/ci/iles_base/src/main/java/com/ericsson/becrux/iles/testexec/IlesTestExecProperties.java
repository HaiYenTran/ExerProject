package com.ericsson.becrux.iles.testexec;

import com.ericsson.becrux.base.common.vise.ViseChannel;
import javax.annotation.Nonnull;
import java.io.Closeable;
import java.io.IOException;

//prepare all argument for intX command
public class IlesTestExecProperties implements Closeable{

    private String intXLocation;
    private ViseChannel viseChannel;
    private String executionServer;
    private String gatewayServer;
    private boolean uploadToInsight;
    private boolean useSandboxPdbInstance;
    private boolean realTestExec;
    private String cfgFile;

    public IlesTestExecProperties() {}
    public String getCfgFile() {
        return cfgFile;
    }

    public void setCfgFile(String cfgFile) {
        this.cfgFile = cfgFile;
    }

    public String getIntXLocation() {
        return intXLocation;
    }

    public void setIntXLocation(String intXLocation) {
        this.intXLocation = intXLocation;
    }

    public ViseChannel getViseChannel() {
        return viseChannel;
    }

    public void setViseChannel(ViseChannel viseChannel) {
        this.viseChannel = viseChannel;
    }

    public String getExecutionServer() {
        return executionServer;
    }

    public void setExecutionServer(String executionServer) {
        this.executionServer = executionServer;
    }

    public String getGatewayServer() {
        return gatewayServer;
    }

    public void setGatewayServer(String gatewayServer) {
        this.gatewayServer = gatewayServer;
    }

    public boolean isUploadToInsight() {
        return uploadToInsight;
    }

    public void setUploadToInsight(boolean uploadToInsight) {
        this.uploadToInsight = uploadToInsight;
    }

    public boolean isUseSandboxPdbInstance() {
        return useSandboxPdbInstance;
    }

    public void setUseSandboxPdbInstance(boolean useSandboxPdbInstance) {
        this.useSandboxPdbInstance = useSandboxPdbInstance;
    }

    public boolean isRealTestExec() {
        return realTestExec;
    }

    public void setRealTestExec(boolean realTestExec) {
        this.realTestExec = realTestExec;
    }

    @Override
    public void close() throws IOException {}
}
