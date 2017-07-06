package com.ericsson.becrux.base.common;

import java.util.Map;

public interface TestGlobalConfig {
    String getExecutionServer();

    String getGatewayServer();

    String getTestLocation();

    String getTestName();

    String getCfgLocation();

    String getToolsLocation();

    String getCfgFile();

    String getTTCN3ExportDir();

    String getTTCN3ExportPath();

    String getTTCN3LicenseFile();

    String getTTCNLabPwd();

    String getIsVerbose();

    String getIntResultsLocal();

    String getTtcnTimeout();

    Map<String, String> getProperties();
}
