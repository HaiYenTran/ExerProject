package com.ericsson.becrux.iles.common.mockers;

import com.ericsson.becrux.base.common.vise.ViseChannel;

import java.io.File;
import java.io.IOException;

/**
 * Created by znikvik on 5/19/17.
 */
public class MockerInitializer {
    //protected GlobalConfigurationMocker globalConfigMocker;

    //private File jarFile;

    public static void initializeGlobalConfigurationMocker() throws Exception {
        File jarFile = new File("testexec.jar");
        GlobalConfigurationMocker globalConfigMocker = GlobalConfigurationMocker.createMock();
        globalConfigMocker.mockAll(jarFile);

        ViseChannel viseChannel = new ViseChannel("VISE0300");
        viseChannel.setIpAddress("192.168.0.1");
        globalConfigMocker.mockLoadViseChannel(viseChannel);
    }
}
