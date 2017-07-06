package com.ericsson.becrux.base.common.mockers;

import com.ericsson.becrux.base.common.core.NwftDownstreamJob;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.Shell;
import jenkins.model.Jenkins;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

/******************************************************************************
 * Mocking of jenkins class {@link Jenkins}.
 *****************************************************************************/
public class JenkinsMocker {
    private Jenkins jenkinsMock;
    private Shell jenkinsShellMock;
    private RunMocker runMock;
    private AbstractBuildMocker abstractBuildMock;
    private NwftDownstreamJob downstreamJobMock;

    private JenkinsMocker(Descriptor descriptor) throws Exception {
        this.jenkinsMock = PowerMockito.mock(Jenkins.class);
        this.runMock = RunMocker.createMock();
        this.jenkinsShellMock = PowerMockito.mock(Shell.class);
        this.abstractBuildMock = AbstractBuildMocker.createMock();
        mockJenkinsInstance(descriptor);

        PowerMockito.whenNew(Shell.class).withArguments(anyString()).thenReturn(this.jenkinsShellMock);
        when(this.jenkinsShellMock.perform(any(AbstractBuild.class), any(Launcher.class), any(BuildListener.class))).thenReturn(true);
    }

    private void mockJenkinsInstance(Descriptor descriptor) throws Exception {
        PowerMockito.mockStatic(Jenkins.class);
        PowerMockito.when(Jenkins.getInstance()).thenReturn(jenkinsMock);
        PowerMockito.when(Jenkins.getInstance().getDescriptorOrDie(any())).thenReturn(descriptor);
        FreeStyleProject freeStyleProjectMock = PowerMockito.mock(FreeStyleProject.class);
        PowerMockito.when(Jenkins.getInstance().getItem(anyString())).thenReturn(freeStyleProjectMock);
        PowerMockito.when(Jenkins.getInstance().getRootUrl()).thenReturn("https://localhost:8888");
    }

    /**********************************************************************
     * Create a wrapper object for mocking jenkins.
     *
     * @param descriptor The descriptor related to the jenkins instance
     *                   to mock.
     * @return JenkinsMocker object
     *********************************************************************/
    public static JenkinsMocker createMock(Descriptor descriptor) throws Exception {
        return new JenkinsMocker(descriptor);
    }

    /**********************************************************************
     * Mock a jenkins downstream job.
     *********************************************************************/
    public void mockNwftDownstreamJob() throws Exception {
        downstreamJobMock = PowerMockito.mock(NwftDownstreamJob.class);
        PowerMockito.whenNew(NwftDownstreamJob.class).withAnyArguments().thenReturn(downstreamJobMock);
        PowerMockito.when(downstreamJobMock.getBuild()).thenReturn(abstractBuildMock.getMock());
    }

    private List<String> jobNames = new ArrayList<>();
    public void mockJenkinsJobs(String jobName) {
        jobNames.add(jobName);
        PowerMockito.when(Jenkins.getInstance().getJobNames()).thenReturn(jobNames);
    }

    /**********************************************************************
     * Accessors, in case extra mocking is needed.
     *********************************************************************/
    public Jenkins getJenkinsMock() {
        return jenkinsMock;
    }

    public AbstractBuild getAbstractBuildMock() {
        return abstractBuildMock.getMock();
    }

    public Shell getJenkinsShellMock() {
        return jenkinsShellMock;
    }

    public Run getRunMock() {
        return runMock.getMock();
    }
}
