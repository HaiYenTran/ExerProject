package com.ericsson.becrux.iles.common.mockers;

import com.ericsson.becrux.base.common.configuration.JenkinsGlobalConfig;
import com.ericsson.becrux.base.common.data.ImsBaseline;
import com.ericsson.becrux.base.common.vise.VisePool;
import com.ericsson.becrux.base.common.dao.ComponentDao;
import com.ericsson.becrux.base.common.vise.ViseChannel;
import com.ericsson.becrux.base.common.configuration.ViseChannelGlobalConfig;
import com.ericsson.becrux.base.common.dao.ViseChannelDao;
import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.iles.configuration.IlesDirectory;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import com.ericsson.becrux.iles.data.IlesComponentFactory;
import com.ericsson.becrux.iles.data.IlesImsBaseline;
import org.powermock.api.mockito.PowerMockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

/******************************************************************************
 * Mocking of jenkins class {@link JenkinsGlobalConfig},
 * {@link IlesGlobalConfig} and {@link ViseChannelGlobalConfig}.
 *****************************************************************************/
public class GlobalConfigurationMocker {
    private ViseChannelGlobalConfig viseChannelGlobalConfigMock;
    private ViseChannelDao viseDaoMock;
    private IlesImsBaseline processingBaselineMock;
    private JenkinsGlobalConfig jenkinsGlobalConfigMock;

    private IlesGlobalConfig ilesGlobalConfigMock;
    private Path ilesDaoPathMock;
    private ComponentDao componentDaoMock;
    private IlesDirectory ilesDirectoryMock;
    private File phoenixDirMock;
    private File provisioningDirMock;


    private GlobalConfigurationMocker() {
        phoenixDirMock = PowerMockito.mock(File.class);
        provisioningDirMock = PowerMockito.mock(File.class);
    }

    /**********************************************************************
     * Create a wrapper object for mocking of all global configurations.
     *
     * @return GlobalConfigurationMocker object
     *********************************************************************/
    public static GlobalConfigurationMocker createMock() {
        return new GlobalConfigurationMocker();
    }

    /**********************************************************************
     * Initiates a mock for {@link IlesGlobalConfig} only.
     *
     * @return this GlobalConfigurationMocker object
     *********************************************************************/
    public GlobalConfigurationMocker createIlesGlobalConfigMock(File jarFile) throws Exception {
        mockIlesGlobalConfig(jarFile);
        return this;
    }

    /**********************************************************************
     * Initiates a mock for {@link JenkinsGlobalConfig} only.
     *
     * @return this GlobalConfigurationMocker object
     *********************************************************************/
    public GlobalConfigurationMocker createJenkinsGlobalConfigMock() {
        mockJenkinsGlobalConfig();
        return this;
    }

    /**********************************************************************
     * Initiates a mock for {@link IlesGlobalConfig} only.
     *
     * @return this GlobalConfigurationMocker object
     *********************************************************************/
    public GlobalConfigurationMocker createViseChannelGlobalConfigMock() {
        mockViseChannelGlobalConfig();
        return this;
    }

    /**********************************************************************
     * Initiates a mock for all global configurations.
     *
     * @return this GlobalConfigurationMocker object
     *********************************************************************/
    public GlobalConfigurationMocker mockAll(File jarFile) throws Exception {
        mockIlesGlobalConfig(jarFile);
        mockViseChannelGlobalConfig();
        mockJenkinsGlobalConfig();
        return this;
    }

    /**********************************************************************
     * Mock loadViseChannel() to return the given {@link ViseChannel}
     *
     * @param viseChannel the VISE channel to return
     *********************************************************************/
    public void mockLoadViseChannel(ViseChannel viseChannel) throws IOException {
        when(viseDaoMock.loadViseChannel(any())).thenReturn(viseChannel);
    }

    /**********************************************************************
     * Mock loadVisePool() to return the given {@link VisePool}
     *
     * @param visePool the VISE channel to return
     *********************************************************************/
    public void mockLoadVisePool(VisePool visePool, ViseChannel... viseChannels) throws IOException {
        when(viseDaoMock.loadVisePool(anyString())).thenReturn(visePool);
        List<String> channels = new ArrayList<>();
        for (ViseChannel viseChannel : viseChannels) {
            channels.add(viseChannel.getFullName());
        }
        visePool.setViseChannels(channels);
    }

    /**********************************************************************
     * Mock loadViseChannels() to return all given {@link ViseChannel}
     *
     * @param viseChannels the VISE channels to return
     *********************************************************************/
    public void mockLoadViseChannels(ViseChannel... viseChannels) throws IOException {
        List<ViseChannel> ret = new ArrayList<>();
        for (ViseChannel viseChannel : viseChannels)
            ret.add(viseChannel);
        when(viseDaoMock.loadViseChannels()).thenReturn(ret);
    }

    /**********************************************************************
     * Mock loadComponent() to return the given {@link Component}
     *
     * @param component the VISE channels to return
     *********************************************************************/
    public void mockLoadComponent(Component component) throws IOException {
        when(componentDaoMock.loadComponent(anyString(), anyString())).thenReturn(component);
    }

    public void mockLoadNewestComponent() throws Exception {
        for (String node : IlesComponentFactory.getInstance().getRegisteredClassNames())
            when(componentDaoMock.loadNewestComponent(node)).thenReturn(IlesComponentFactory.getInstance().create(node));
    }

    public void mockloadOneNewestComponent(String node) throws Exception {
        Component component = IlesComponentFactory.getInstance().create(node);
        component.setState(Component.State.BASELINE_APPROVED);
        if(node.equalsIgnoreCase("Mtas")) {
            component.setVersion("vMTAS_1_1_LSV1");
        } else {
            component.setVersion("R1A01");
        }
        when(componentDaoMock.loadNewestComponent(node, Component.State.BASELINE_APPROVED)).thenReturn(component);

    }
    public void mockLoadNewestComponent(Component.State state) throws Exception {
        for (String node : IlesComponentFactory.getInstance().getRegisteredClassNames()) {
            Component component = IlesComponentFactory.getInstance().create(node);
            component.setState(state);

            if(component.getType().equals("mtas")) {
                component.setVersion("vMTAS_1_1_LSV1");
            } else {
                component.setVersion("R1A01");
            }
            when(componentDaoMock.loadNewestComponent(node, state)).thenReturn(component);
        }
    }

    /**********************************************************************
     * Mock loadComponent() to return all given {@link Component}
     *
     *
     *********************************************************************/
    public void mockLoadAllComponents() throws IOException {
        List<Component> ret = new ArrayList<>();
        for (String node : IlesComponentFactory.getInstance().getRegisteredClassNames()) {
            try {
                Component component = IlesComponentFactory.getInstance().create(node);
                component.setState(Component.State.BASELINE_APPROVED);
                if(node.equals("Mtas")) {
                    component.setVersion("vMTAS_1_1_LSV1");
                } else {
                    component.setVersion("R1A01");
                }
                ret.add(component);
            } catch (Exception ex) {

            }
        }
        when(componentDaoMock.loadAllComponents(anyString(), any(Component.State.class))).thenReturn(ret);
    }

    private void mockViseChannelGlobalConfig() {
        viseDaoMock = PowerMockito.mock(ViseChannelDao.class);
        viseChannelGlobalConfigMock = PowerMockito.mock(ViseChannelGlobalConfig.class);

        PowerMockito.mockStatic(ViseChannelGlobalConfig.class);
        when(ViseChannelGlobalConfig.getInstance()).thenReturn(viseChannelGlobalConfigMock);
        when(ViseChannelGlobalConfig.getInstance().getDao()).thenReturn(viseDaoMock);
    }

    private void mockIlesGlobalConfig(File jarFile) throws Exception {
        componentDaoMock = PowerMockito.mock(ComponentDao.class);
        ilesDaoPathMock = PowerMockito.mock(Path.class);
        ilesDirectoryMock = PowerMockito.mock(IlesDirectory.class);
        ilesGlobalConfigMock = PowerMockito.mock(IlesGlobalConfig.class);
        processingBaselineMock = PowerMockito.mock(IlesImsBaseline.class);

        PowerMockito.mockStatic(IlesGlobalConfig.class);
        when(IlesGlobalConfig.getInstance()).thenReturn(ilesGlobalConfigMock);
        when(ilesGlobalConfigMock.getComponentDao()).thenReturn(componentDaoMock);
        when(ilesGlobalConfigMock.getProcessingBaseline()).thenReturn(processingBaselineMock);
        when(ilesDirectoryMock.getPhoenixDir()).thenReturn(phoenixDirMock);
        when(ilesDirectoryMock.getProvisioningDir()).thenReturn(provisioningDirMock);
        when(ilesDirectoryMock.getProvisioningScript()).thenReturn(provisioningDirMock);
        when(ilesDirectoryMock.getTestExecJar()).thenReturn(jarFile);

        when(IlesGlobalConfig.getInstance().getIlesDirectory()).thenReturn(ilesDirectoryMock);
        when(phoenixDirMock.getPath()).thenReturn("./");
        when(phoenixDirMock.getAbsolutePath()).thenReturn("./");

        PowerMockito.whenNew(ImsBaseline.class).withAnyArguments().thenReturn(processingBaselineMock);
    }

    private void mockJenkinsGlobalConfig() {
        jenkinsGlobalConfigMock = PowerMockito.mock(JenkinsGlobalConfig.class);
        PowerMockito.mockStatic(JenkinsGlobalConfig.class);
        when(JenkinsGlobalConfig.getInstance()).thenReturn(jenkinsGlobalConfigMock);
        when(jenkinsGlobalConfigMock.getLeoUrl()).thenReturn("https://www.leo-url.com");
    }

    /**********************************************************************
     * Accessors
     *********************************************************************/

}
