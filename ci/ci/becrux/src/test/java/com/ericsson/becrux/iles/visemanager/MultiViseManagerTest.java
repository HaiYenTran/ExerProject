package com.ericsson.becrux.iles.visemanager;

import com.ericsson.becrux.base.common.configuration.ViseChannelGlobalConfig;
import com.ericsson.becrux.base.common.dao.ViseChannelDao;
import com.ericsson.becrux.base.common.vise.ViseChannel;
import com.ericsson.becrux.base.common.vise.VisePool;
import com.ericsson.becrux.base.common.vise.reservation.ChannelReservation;
import com.ericsson.becrux.base.common.vise.reservation.ReservationIdentifier;
import com.ericsson.becrux.iles.configuration.IlesDirectory;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import com.ericsson.becrux.iles.utils.JenkinsHelper;
import com.google.common.io.Files;
import hudson.model.AbstractBuild;
import hudson.model.Project;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * Test {@link MultiViseManager}
 */
public class MultiViseManagerTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    private File baseDir;
    private MultiViseManager viseManager;
    private String exitedVisePool;
    private String emptyVisePool;
    private int exitedViseNumber;
    @Mock private AbstractBuild mockedBuild;
    @Mock private JenkinsHelper helper;
    @Mock private Project project;

    /**
     * Setting data for testing.
     * @throws Exception
     */
    @Before
    public void init() throws Exception{
        MockitoAnnotations.initMocks(this);
        baseDir = Files.createTempDir();
        IlesGlobalConfig.getInstance().getDescriptor().setComponentDaoPath(baseDir.getPath() + "ComponentDao");
        IlesGlobalConfig.getInstance().getDescriptor().setEventDaoPath(baseDir.getPath() + "EventDao");
        IlesGlobalConfig.getInstance().getDescriptor().setIlesDaoPath(baseDir.getPath() + "IlesDao");
        IlesGlobalConfig.getInstance().getDescriptor().setIlesDirPath(baseDir.getPath() + "Extension");
        ViseChannelGlobalConfig.getInstance().setDaoPath(new File(baseDir, "ViseDao").getPath());
        IlesGlobalConfig.getInstance().synchonizeDAO();
        viseManager = new MultiViseManager();
        createViseChannels();

        // work around solution for create ReservationIdentifier
        String jobName = "AnyJobName";
        String jobId = "1";
        Map<String, String> buildVars = new HashMap<>();
        buildVars.put("JOB_NAME", jobName);
        when(mockedBuild.getEnvVars()).thenReturn(buildVars);
        when(mockedBuild.getId()).thenReturn(jobId);

        // work around solution for get build
        ReflectionTestUtils.setField(viseManager, "jenkinsHelper", helper);
        when(helper.findJob(jobName)).thenReturn(project);
        when(project.getBuild(jobId)).thenReturn(mockedBuild);
    }

    /**
     * Create Vise channels and pools in DAO.
     * @throws Exception
     */
    private void createViseChannels() throws Exception {
        ViseChannelDao viseDao = ViseChannelGlobalConfig.getInstance().getDao();

        // create Vise channels
        exitedViseNumber = 299;
        ViseChannel vise1 = new ViseChannel(exitedViseNumber);
        viseDao.saveViseChannel(vise1);

        // create Vise pools
        exitedVisePool = "pool1";
        VisePool pool1 = new VisePool(exitedVisePool);
        pool1.getViseChannels().add(vise1.getFullName());
        viseDao.saveVisePool(pool1);
        emptyVisePool = "emptyPool";
        VisePool pool2 = new VisePool(emptyVisePool);
        viseDao.saveVisePool(pool2);
    }

    /**
     * Test {@link MultiViseManager#reserveViseChannelFromPool(String, ReservationIdentifier)}
     * Cases:
     * - Reserve a free Vise
     * - Reserve when no Vise are free
     * @throws Exception if anything fail
     */
    @Test
    public void reserveExitedViseChannel() throws Exception {
        ReservationIdentifier identifier = new ReservationIdentifier(mockedBuild);

        // case: has free channel (1 pool with 1 vise)
        ViseChannel channel = viseManager.reserveViseChannelFromPool(exitedVisePool, identifier);
        assertTrue(channel != null && channel.getNumber() == exitedViseNumber);

        // case: all channel was reserved
        when(mockedBuild.isBuilding()).thenReturn(true);
        channel = viseManager.reserveViseChannelFromPool(exitedVisePool, identifier);
        assertTrue(channel == null);
    }

    /**
     * Test {@link MultiViseManager#reserveViseChannelFromPool(String, ReservationIdentifier)}
     * Cases:
     * - Try to reserve a vise in an empty pool
     * @throws Exception if anything fail
     */
    @Test
    public void reserveNoneExitViseChannel() throws Exception{
        ReservationIdentifier identifier = new ReservationIdentifier(mockedBuild);
        ViseChannel channel = viseManager.reserveViseChannelFromPool(emptyVisePool, identifier);
        assertTrue(channel == null);
    }

    /**
     * Test {@link MultiViseManager#reserveViseChannelFromPool(String, ReservationIdentifier)}
     * Cases:
     * - Try to reserve a pool that not exit
     * @throws Exception if anything fail
     */
    @Test
    public void reserveNoneExitVisePool() throws Exception {
        ReservationIdentifier identifier = new ReservationIdentifier(mockedBuild);
        try {
            ViseChannel channel = viseManager.reserveViseChannelFromPool("RandomPoolName", identifier);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().equals("Invalid pool name."));
        }
    }

    /**
     * Test {@link MultiViseManager#freeViseChannel(String)}
     * Cases:
     * - free a free Vise
     * - free a using Vise
     * @throws Exception if anything fail
     */
    @Test
    public void freeExitedViseChannel() throws Exception {
        ReservationIdentifier identifier = new ReservationIdentifier(mockedBuild);

        // case: has free channel (1 pool with 1 vise)
        ViseChannel channel = viseManager.reserveViseChannelFromPool(exitedVisePool, identifier);
        assertTrue(channel != null && channel.getNumber() == exitedViseNumber);
        ChannelReservation reservation = ViseChannelGlobalConfig.getInstance().getDao().loadChannelReservation(channel.getFullName());
        assertTrue(reservation != null);

        // case: all channel was reserved
        when(mockedBuild.isBuilding()).thenReturn(true);
        viseManager.freeViseChannel(channel.getFullName());
        reservation = ViseChannelGlobalConfig.getInstance().getDao().loadChannelReservation(channel.getFullName());
        assertTrue(reservation == null);
    }

    /**
     * Test {@link MultiViseManager#freeViseChannel(String)}
     * Cases:
     * - free a Vise that not exit
     * @throws Exception if anything fail
     */
    @Test
    public void freeNoneExitViseChannel() throws Exception {
        // most likely nothing happen ..
        viseManager.freeViseChannel("NoneExitVise");
    }

    /**
     * Clean up env after test.
     * @throws Exception
     */
    @After
    public void cleanUp() throws Exception {
        FileUtils.deleteDirectory(baseDir);
    }
}
