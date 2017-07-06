package com.ericsson.becrux.base.common.dao.filedb;

import com.ericsson.becrux.base.common.vise.ViseChannel;
import com.ericsson.becrux.base.common.vise.VisePool;
import com.ericsson.becrux.base.common.dao.ViseChannelDao;
import com.ericsson.becrux.base.common.utils.TempFilesCreator;
import com.ericsson.becrux.base.common.vise.reservation.ChannelReservation;
import com.ericsson.becrux.base.common.vise.reservation.ReservationIdentifier;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by dung.t.bui on 3/30/2017.
 */
public class JsonViseChannelDaoTest {

    private File path;
    private ViseChannelDao dao;

    @Before
    public void init() throws Exception{
        path = TempFilesCreator.createTempDirectory("testDirectory");
        dao = new JsonViseChannelDao(path.getAbsolutePath());
    }

    @Test
    public void testVisePoolsSerialization() throws IOException {
        try {
            List<VisePool> pools = new ArrayList<>();
            VisePool pool1 = new VisePool("Pool1", new ViseChannel(104).getFullName());
            VisePool pool2 = new VisePool("Pool2");
            VisePool pool3 = new VisePool("Pool3");
            pool3.getViseChannels().add(new ViseChannel(105).getFullName());
            pool3.getViseChannels().add(new ViseChannel(106).getFullName());
            pools.add(pool1);
            pools.add(pool2);
            pools.add(pool3);
            dao.saveVisePools(pools);
            dao.checkSynchronizeLock();
            assertTrue(dao.validate());
            List<VisePool> pools2 = dao.loadVisePools();

            for (VisePool pool : pools) assertTrue(pools2.contains(pool));
            assertEquals(pools.size(), pools2.size());
            for(int i = 0; i < pools.size(); ++i) {
                VisePool originalPool = pools.get(i);
                VisePool serializedPool = pools2.get(pools2.indexOf(originalPool));
                assertEquals(originalPool, serializedPool);
                assertEquals(originalPool.getName(), serializedPool.getName());
                assertEquals(originalPool.getViseChannels(), serializedPool.getViseChannels());
                for(int j = 0; j < originalPool.getViseChannels().size(); ++j) {
                    String originalChannel = originalPool.getViseChannels().get(j);
                    String serializedChannel = serializedPool.getViseChannels().get(j);
                    assertEquals(originalChannel, serializedChannel);
                }
            }
        }
        catch (IOException e) {
            fail("Could not save component with reason: " + e.getMessage());
        }
        catch (Exception e) {
            fail("DAO validation failed with reason: " + e.getMessage());
        }
        finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test
    public void testSingleVisePoolSerialization() throws IOException {
        try {
            VisePool originalEmptyPool = new VisePool("Name1");
            dao.saveVisePool(originalEmptyPool);
            VisePool serializedEmptyPool = dao.loadVisePool("Name1");
            assertEquals(originalEmptyPool, serializedEmptyPool);
            assertEquals(originalEmptyPool.getName(), serializedEmptyPool.getName());
            assertEquals(originalEmptyPool.getViseChannels().size(), serializedEmptyPool.getViseChannels().size());
            assertEquals(originalEmptyPool.getViseChannels(), serializedEmptyPool.getViseChannels());
            VisePool originalNonEmptyPool = new VisePool("Name2", new ViseChannel(105).getFullName());
            dao.saveVisePool(originalNonEmptyPool);
            VisePool serializedNonEmptyPool = dao.loadVisePool("Name2");
            assertEquals(originalNonEmptyPool, serializedNonEmptyPool);
            assertEquals(originalNonEmptyPool.getName(), serializedNonEmptyPool.getName());
            assertEquals(originalNonEmptyPool.getViseChannels().size(), serializedNonEmptyPool.getViseChannels().size());
            assertEquals(originalNonEmptyPool.getViseChannels(), serializedNonEmptyPool.getViseChannels());
            dao.checkSynchronizeLock();
            assertTrue(dao.validate());
        }
        catch (IOException e) {
            fail("Could not save component with reason: " + e.getMessage());
        }
        catch (Exception e) {
            fail("DAO validation failed with reason: " + e.getMessage());
        }
        finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test
    public void testLengthTruncateInPoolSerialization() throws IOException {
        try {
            VisePool pool = new VisePool("Name1");
            pool.getViseChannels().add(new ViseChannel(105).getFullName());
            pool.getViseChannels().add(new ViseChannel(106).getFullName());
            dao.saveVisePool(pool);
            VisePool pool2 = dao.loadVisePool("Name1");
            assertEquals(pool, pool2);
            assertEquals(pool.getViseChannels().size(), 2);
            assertEquals(pool2.getViseChannels().size(), 2);
            pool.getViseChannels().remove(1);
            dao.saveVisePool(pool);
            assertNotEquals(pool, pool2);
            pool2 = dao.loadVisePool("Name1");
            assertEquals(pool, pool2);
            assertEquals(pool.getViseChannels().size(), 1);
            assertEquals(pool2.getViseChannels().size(), 1);
            dao.checkSynchronizeLock();
            assertTrue(dao.validate());
        }
        catch (IOException e) {
            fail("Could not save component with reason: " + e.getMessage());
        }
        catch (Exception e) {
            fail("DAO validation failed with reason: " + e.getMessage());
        }
        finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test
    public void testRemovalOfVisePoolsDuringListOverride() throws IOException {
        try {
            List<VisePool> pools = new ArrayList<>();
            pools.add(new VisePool("Name1"));
            pools.add(new VisePool("Name2"));
            dao.saveVisePools(pools);
            dao.checkSynchronizeLock();
            assertTrue(dao.validate());
            List<VisePool> pools2 = dao.loadVisePools();
            for (VisePool pool : pools) assertTrue(pools2.contains(pool));
            assertEquals(pools.size(), 2);
            assertEquals(pools2.size(), 2);
            pools.remove(1);
            dao.saveVisePools(pools);
            assertNotEquals(pools, pools2);
            pools2 = dao.loadVisePools();
            assertEquals(pools, pools2);
            assertEquals(pools.size(), 1);
            assertEquals(pools2.size(), 1);
            assertTrue(dao.validate());
        }
        catch (IOException e) {
            fail("Could not save component with reason: " + e.getMessage());
        }
        catch (Exception e) {
            fail("DAO validation failed with reason: " + e.getMessage());
        }
        finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test
    public void testRemoveVisePoolEmptyName() throws IOException {
        try {
            dao.removeVisePool("");
            fail("Exception not thrown when calling removeVisePool with empty name.");
        }
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Name cannot be empty"));
        }
    }

    @Test
    public void testRemovalOfSingleVisePool() throws IOException {
        try {
            VisePool pool1 = new VisePool("Name1");
            VisePool pool2 = new VisePool("Name2");
            dao.saveVisePool(pool1);
            dao.saveVisePool(pool2);
            dao.checkSynchronizeLock();
            assertTrue(dao.validate());
            List<VisePool> pools = dao.loadVisePools();
            assertEquals(pools.size(), 2);
            dao.removeVisePool(pool1);
            pools = dao.loadVisePools();
            assertEquals(pools.size(), 1);
            dao.removeVisePool("Name2");
            pools = dao.loadVisePools();
            assertEquals(pools.size(), 0);
            assertTrue(dao.validate());
        }
        catch (IOException e) {
            fail("Could not save component with reason: " + e.getMessage());
        }
        catch (Exception e) {
            fail("DAO validation failed with reason: " + e.getMessage());
        }
        finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test
    public void testRemovalOfAllVisePools() throws IOException {
        try {
            VisePool pool1 = new VisePool("Name1");
            VisePool pool2 = new VisePool("Name2");
            dao.saveVisePool(pool1);
            dao.saveVisePool(pool2);
            dao.checkSynchronizeLock();
            assertTrue(dao.validate());
            List<VisePool> pools = dao.loadVisePools();
            assertEquals(pools.size(), 2);
            dao.removeAllVisePools();
            pools = dao.loadVisePools();
            assertEquals(pools.size(), 0);
            assertTrue(dao.validate());
        }
        catch (IOException e) {
            fail("Could not save component with reason: " + e.getMessage());
        }
        catch (Exception e) {
            fail("DAO validation failed with reason: " + e.getMessage());
        }
        finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test
    public void testLoadingVisePoolEmptyName() throws IOException {
        try {
            dao.loadVisePool("");
            fail("Exception not thrown when calling loadVisePool with empty name.");
        }
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Name cannot be empty"));
        }
    }

    @Test
    public void testLoadingSingleNonExistantVisePool() throws IOException {
        try {
            VisePool pool = dao.loadVisePool("abcd");
            assertNull(pool);
        } finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test
    public void testLoadingOfMultipleNonExistantVisePools() throws IOException {
        try {
            List<VisePool> pools = dao.loadVisePools();
            assertNotNull(pools);
            assertEquals(0, pools.size());
        } finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    //Vise channels tests
    @Test
    public void testViseChannelsSerialization() throws IOException {
        try {
            List<ViseChannel> channels = new ArrayList<>();
            ViseChannel channel1 = new ViseChannel(101);
            ViseChannel channel2 = new ViseChannel(102);
            ViseChannel channel3 = new ViseChannel(103);
            channels.add(channel1);
            channels.add(channel2);
            channels.add(channel3);
            dao.saveViseChannels(channels);
            dao.checkSynchronizeLock();
            assertTrue(dao.validate());
            List<ViseChannel> channels2 = dao.loadViseChannels();
            for (ViseChannel ch : channels) assertTrue(channels2.contains(ch));
            assertEquals(channels.size(), channels2.size());
            for(int i = 0; i < channels.size(); ++i) {
                ViseChannel originalChannel = channels.get(i);
                ViseChannel serializedChannel = channels2.get(channels2.indexOf(originalChannel));
                assertEquals(originalChannel, serializedChannel);
                assertEquals(originalChannel.getFullName(), serializedChannel.getFullName());
                assertEquals(originalChannel.getShortName(), serializedChannel.getShortName());
                assertEquals(originalChannel.getNumber(), serializedChannel.getNumber());
            }
        }
        catch (IOException e) {
            fail("Could not save component with reason: " + e.getMessage());
        }
        catch (Exception e) {
            fail("DAO validation failed with reason: " + e.getMessage());
        }
        finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test
    public void testSingleViseChannelSerialization() throws IOException {
        try {
            ViseChannel originalEmptyChannel = new ViseChannel(101);
            dao.saveViseChannel(originalEmptyChannel);
            dao.checkSynchronizeLock();
            assertTrue(dao.validate());
            ViseChannel serializedEmptyChannel = dao.loadViseChannel(101);
            assertEquals(originalEmptyChannel, serializedEmptyChannel);
            assertEquals(originalEmptyChannel.getFullName(), serializedEmptyChannel.getFullName());
            assertEquals(originalEmptyChannel.getShortName(), serializedEmptyChannel.getShortName());
            assertEquals(originalEmptyChannel.getNumber(), serializedEmptyChannel.getNumber());
        }
        catch (IOException e) {
            fail("Could not save component with reason: " + e.getMessage());
        }
        catch (Exception e) {
            fail("DAO validation failed with reason: " + e.getMessage());
        }
        finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test
    public void testRemovalOfViseChannelsDuringListOverride() throws IOException {
        try {
            List<ViseChannel> channels = new ArrayList<>();
            channels.add(new ViseChannel(101));
            channels.add(new ViseChannel(102));
            dao.saveViseChannels(channels);
            List<ViseChannel> channels2 = dao.loadViseChannels();
            for (ViseChannel ch : channels) assertTrue(channels2.contains(ch));
            assertEquals(channels.size(), 2);
            assertEquals(channels2.size(), 2);
            channels.remove(1);
            dao.saveViseChannels(channels);
            dao.checkSynchronizeLock();
            assertTrue(dao.validate());
            assertNotEquals(channels, channels2);
            channels2 = dao.loadViseChannels();
            assertEquals(channels, channels2);
            assertEquals(channels.size(), 1);
            assertEquals(channels2.size(), 1);
        }
        catch (IOException e) {
            fail("Could not save component with reason: " + e.getMessage());
        }
        catch (Exception e) {
            fail("DAO validation failed with reason: " + e.getMessage());
        }
        finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test
    public void testRemoveViseChannelEmptyName() throws IOException {
        try {
            dao.removeViseChannel("");
            fail("Exception not thrown when calling removeViseChannel with empty name.");
        }
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Name cannot be empty"));
        }
    }

    @Test
    public void testRemovalOfSingleViseChannel() throws IOException {
        try {
            ViseChannel channel1 = new ViseChannel(101);
            ViseChannel channel2 = new ViseChannel(102);
            dao.saveViseChannel(channel1);
            dao.saveViseChannel(channel2);
            dao.checkSynchronizeLock();
            assertTrue(dao.validate());
            List<ViseChannel> channels = dao.loadViseChannels();
            assertEquals(channels.size(), 2);
            dao.removeViseChannel(channel1);
            channels = dao.loadViseChannels();
            assertEquals(channels.size(), 1);
            dao.removeViseChannel(102);
            channels = dao.loadViseChannels();
            assertEquals(channels.size(), 0);
            assertTrue(dao.validate());
        }
        catch (IOException e) {
            fail("Could not save component with reason: " + e.getMessage());
        }
        catch (Exception e) {
            fail("DAO validation failed with reason: " + e.getMessage());
        }
        finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test
    public void testRemovalOfAllViseChannels() throws IOException {
        try {
            ViseChannel channel1 = new ViseChannel(101);
            ViseChannel channel2 = new ViseChannel(102);
            dao.saveViseChannel(channel1);
            dao.saveViseChannel(channel2);
            dao.checkSynchronizeLock();
            assertTrue(dao.validate());
            List<ViseChannel> channels = dao.loadViseChannels();
            assertEquals(channels.size(), 2);
            dao.removeAllViseChannels();
            assertTrue(dao.validate());
            channels = dao.loadViseChannels();
            assertEquals(channels.size(), 0);
        }
        catch (IOException e) {
            fail("Could not save component with reason: " + e.getMessage());
        }
        catch (Exception e) {
            fail("DAO validation failed with reason: " + e.getMessage());
        }
        finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test
    public void testLoadViseChannelEmptyName() throws IOException {
        try {
            dao.loadViseChannel("");
            fail("Exception not thrown when calling loadViseChannel with empty name.");
        }
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Name cannot be empty"));
        }
    }

    @Test
    public void testLoadingSingleNonExistantViseChannel() throws IOException {
        try {
            ViseChannel channel = dao.loadViseChannel(101);
            assertNull(channel);
        } finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test
    public void testLoadingMultipleNonExistantViseChannels() throws IOException {
        try {
            List<ViseChannel> channels = dao.loadViseChannels();
            assertNotNull(channels);
            assertEquals(0, channels.size());
        } finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test
    public void testSaveChannelReservationNegativeTests() throws IOException {
        try {
            ChannelReservation reservation = new ChannelReservation("0001", new ReservationIdentifier());
            dao.saveChannelReservation(reservation);
            fail("Exception not thrown when calling saveChannelReservation with empty identifier.");
        }
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            assertTrue(e.getMessage().contains("Channel reservation identifier has null job name or job number"));
        }
    }

    @Test
    public void testLoadChannelReservationNegativeTests() throws IOException {
        try {;
            dao.loadChannelReservation("");
            fail("Exception not thrown when calling loadChannelReservation with empty name.");
        }
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Channel name cannot be empty"));
        }
    }

    @Test
    public void testRemoveChannelReservationNegativeTests() throws IOException {
        try {
            dao.removeChannelReservation("");
            fail("Exception not thrown when calling loadChannelReservation with empty name.");
        }
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Channel name cannot be empty"));
        }
    }

    @Test
    public void testChannelReservationSerialization() throws IOException {
        try {
            ViseChannel channel = new ViseChannel(103);
            ChannelReservation reservation = new ChannelReservation(channel.getFullName(), new ReservationIdentifier("abcd", "123"));
            dao.saveChannelReservation(reservation);
            dao.checkSynchronizeLock();
            assertTrue(dao.validate());

            ChannelReservation reservation2 = dao.loadChannelReservation(channel.getFullName());
            assertEquals(reservation, reservation2);
            assertEquals(reservation.getName(), reservation2.getName());
            assertEquals(reservation.getIdentifier(), reservation2.getIdentifier());
        }
        catch (IOException e) {
            fail("Could not save component with reason: " + e.getMessage());
        }
        catch (Exception e) {
            fail("DAO validation failed with reason: " + e.getMessage());
        }
        finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test
    public void testLoadingNonExistantChannelReservation() throws IOException {
        try {
            ViseChannel channel = new ViseChannel(103);

            ChannelReservation reservation = dao.loadChannelReservation(channel.getFullName());
            assertNull(reservation);
        } finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test
    public void testRemovingChannelReservation() throws IOException {
        try {
            ViseChannel channel = new ViseChannel(103);

            ChannelReservation reservation = new ChannelReservation(channel.getFullName(), new ReservationIdentifier("abcd", "1234"));
            dao.saveChannelReservation(reservation);
            dao.checkSynchronizeLock();
            assertTrue(dao.validate());

            ChannelReservation reservation2 = dao.loadChannelReservation(channel.getFullName());
            assertNotNull(reservation2);
            assertEquals(reservation, reservation2);

            dao.removeChannelReservation(channel.getFullName());

            ChannelReservation reservation3 = dao.loadChannelReservation(channel.getFullName());
            assertNull(reservation3);
            assertTrue(dao.validate());
        }
        catch (IOException e) {
            fail("Could not save component with reason: " + e.getMessage());
        }
        catch (Exception e) {
            fail("DAO validation failed with reason: " + e.getMessage());
        }
        finally {
            if(path != null)
                FileUtils.deleteDirectory(path);
        }
    }

    @Test
    public void testSaveViseMultipleTimes() throws IOException {
        ViseChannel channel = new ViseChannel(999);
        channel.setIpAddress("1.1.1.0");
        dao.saveViseChannel(channel);
        int numberOfViseChannel = dao.loadViseChannels().size();
        channel.setIpAddress("2.2.2.0");
        dao.saveViseChannel(channel);
        assertTrue(dao.loadViseChannels().size() == numberOfViseChannel);
    }

    @After
    public void cleanUp() throws IOException {
        FileUtils.deleteDirectory(path);
    }
}
