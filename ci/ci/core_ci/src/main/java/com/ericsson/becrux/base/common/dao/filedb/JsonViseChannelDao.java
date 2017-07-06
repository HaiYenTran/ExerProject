package com.ericsson.becrux.base.common.dao.filedb;

import com.ericsson.becrux.base.common.vise.ViseChannel;
import com.ericsson.becrux.base.common.vise.VisePool;
import com.ericsson.becrux.base.common.dao.ViseChannelDao;
import com.ericsson.becrux.base.common.vise.reservation.ChannelReservation;
import com.google.gson.Gson;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The Implementation of {@link ViseChannelDao}.
 */
public class JsonViseChannelDao implements ViseChannelDao {
    public static final String VISE_POOL_FOLDER_NAME = "visepools";
    public static final String VISE_CHANNEL_FOLDER_NAME = "visechannels";
    public static final String CHANNEL_RESERVATION_FOLDER_NAME = "channelreservations";
    public static final String JSON_TYPE_FORMAT = ".json";

    private static final ConcurrentMap<Path, Object> _visePoolLocks = new ConcurrentHashMap<>(); //Locks for vise pools
    private static final ConcurrentMap<Path, Object> _viseChannelLocks = new ConcurrentHashMap<>(); //Locks for vise channels
    private static final ConcurrentMap<Path, Object> _channelReservationLocks = new ConcurrentHashMap<>(); //Locks for channel reservations

    protected Path dir; //Path of the current instance

    /**
     * Constructor.
     * @param dir
     */
    public JsonViseChannelDao(@Nonnull String dir) {
        this.dir = Paths.get(dir);

        _visePoolLocks.putIfAbsent(this.dir, new Object());
        _viseChannelLocks.putIfAbsent(this.dir, new Object());
        _channelReservationLocks.putIfAbsent(this.dir, new Object());
    }

    /** {@inheritDoc} */
    @Override
    public boolean validate() throws Exception {
        // TODO: what we need here ?

        return true;
    }

    /**
     * In case all the queue lock not initialize correctly
     */
    public void checkSynchronizeLock() {
        if (_visePoolLocks.get(this.dir) == null) {
            _visePoolLocks.putIfAbsent(this.dir, new Object());
        }
        if (_viseChannelLocks.get(this.dir) == null) {
            _viseChannelLocks.putIfAbsent(this.dir, new Object());
        }
        if (_channelReservationLocks.get(this.dir) == null) {
            _channelReservationLocks.putIfAbsent(this.dir, new Object());
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<VisePool> loadVisePools() throws IOException {
        synchronized (_visePoolLocks.get(dir)) {
            List<VisePool> result = new ArrayList<>();
            File directory = dir.resolve(VISE_POOL_FOLDER_NAME).toFile();
            if(directory.exists()) {
                for (final File file : directory.listFiles())
                    result.add(loadVisePool(FilenameUtils.getBaseName(file.getName())));
            }
            return result;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void saveVisePools(@Nonnull List<VisePool> pools) throws IOException {
        synchronized (_visePoolLocks.get(dir)) {
            removeAllVisePools();
            for (VisePool pool : pools)
                saveVisePool(pool);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllVisePools() throws IOException {
        synchronized (_visePoolLocks.get(dir)) {
            File directory = dir.resolve(VISE_POOL_FOLDER_NAME).toFile();
            if (directory.exists()) {
                for (final File file : directory.listFiles())
                    file.delete();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public VisePool loadVisePool(@Nonnull String name) throws IOException {
        if (name.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");
        synchronized (_visePoolLocks.get(dir)) {
            Path path = dir.resolve(VISE_POOL_FOLDER_NAME).resolve(name + JSON_TYPE_FORMAT);
            if(path.toFile().exists()) {
                String json = new String(Files.readAllBytes(dir.resolve(VISE_POOL_FOLDER_NAME).resolve(name + ".json")));
                return new Gson().fromJson(json, VisePool.class);
            }
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void saveVisePool(@Nonnull VisePool pool) throws IOException {
        synchronized (_visePoolLocks.get(dir)) {
            String json = new Gson().toJson(pool);
            dir.resolve(VISE_POOL_FOLDER_NAME).toFile().mkdirs();
            Files.write(dir.resolve(VISE_POOL_FOLDER_NAME).resolve(pool.getName() + JSON_TYPE_FORMAT), json.getBytes(StandardCharsets.UTF_8));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeVisePool(@Nonnull VisePool pool) throws IOException {
        removeVisePool(pool.getName());
    }

    /** {@inheritDoc} */
    @Override
    public void removeVisePool(@Nonnull String name) throws IOException {
        if (name.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");
        synchronized (_visePoolLocks.get(dir)) {
            File file = dir.resolve(VISE_POOL_FOLDER_NAME).resolve(name + JSON_TYPE_FORMAT).toFile();
            if (file.exists())
                file.delete();
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<ViseChannel> loadViseChannels() throws IOException {
        synchronized (_viseChannelLocks.get(dir)) {
            List<ViseChannel> result = new ArrayList<>();
            File directory = dir.resolve(VISE_CHANNEL_FOLDER_NAME).toFile();
            if(directory.exists()) {
                for(final File file : directory.listFiles())
                    result.add(loadViseChannel(FilenameUtils.getBaseName(file.getName())));
            }
            return result;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void saveViseChannels(@Nonnull List<ViseChannel> channels) throws IOException {
        synchronized (_viseChannelLocks.get(dir)) {
            removeAllViseChannels();
            for (ViseChannel channel : channels)
                saveViseChannel(channel);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllViseChannels() throws IOException {
        synchronized (_viseChannelLocks.get(dir)) {
            File directory = dir.resolve(VISE_CHANNEL_FOLDER_NAME).toFile();
            if (directory.exists()) {
                for (final File file : directory.listFiles())
                    file.delete();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public ViseChannel loadViseChannel(int number) throws IOException {
        synchronized (_viseChannelLocks.get(dir)) {
            ViseChannel temp = new ViseChannel(number);
            return loadViseChannel(temp.getFullName());
        }
    }

    /** {@inheritDoc} */
    @Override
    public ViseChannel loadViseChannel(@Nonnull String name) throws IOException {
        if (name.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");
        synchronized (_viseChannelLocks.get(dir)) {
            Path path = dir.resolve(VISE_CHANNEL_FOLDER_NAME).resolve(name + JSON_TYPE_FORMAT);
            if(path.toFile().exists()) {
                String json = new String(Files.readAllBytes(path));
                return new Gson().fromJson(json, ViseChannel.class);
            }
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void saveViseChannel(@Nonnull ViseChannel channel) throws IOException {
        synchronized (_viseChannelLocks.get(dir)) {
            String json = new Gson().toJson(channel);
            dir.resolve(VISE_CHANNEL_FOLDER_NAME).toFile().mkdirs();
            Files.write(dir.resolve(VISE_CHANNEL_FOLDER_NAME).resolve(channel.getFullName() + JSON_TYPE_FORMAT), json.getBytes(StandardCharsets.UTF_8));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeViseChannel(@Nonnull ViseChannel channel) throws IOException {
        removeViseChannel(channel.getFullName());
    }

    /** {@inheritDoc} */
    @Override
    public void removeViseChannel(int number) throws IOException {
        ViseChannel temp = new ViseChannel(number);
        removeViseChannel(temp.getFullName());
    }

    /** {@inheritDoc} */
    @Override
    public void removeViseChannel(@Nonnull String name) throws IOException {
        if (name.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");
        synchronized (_viseChannelLocks.get(dir)) {
            File file = dir.resolve(VISE_CHANNEL_FOLDER_NAME).resolve(name + JSON_TYPE_FORMAT).toFile();
            if (file.exists())
                file.delete();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void saveChannelReservation(@Nonnull ChannelReservation reservation) throws IOException {
        if (reservation.getIdentifier().getJobName() == null ||
                reservation.getIdentifier().getJobNumber() == null)
            throw new IllegalArgumentException("Channel reservation identifier has null job name or job number");
        synchronized (_channelReservationLocks.get(dir)) {
            Path directory = dir.resolve(CHANNEL_RESERVATION_FOLDER_NAME);
            directory.toFile().mkdirs();
            String json = new Gson().toJson(reservation);
            Files.write(directory.resolve(reservation.getName() + JSON_TYPE_FORMAT), json.getBytes());
        }
    }

    /** {@inheritDoc} */
    @Override
    public ChannelReservation loadChannelReservation(@Nonnull String name) throws IOException {
        if(name.isEmpty())
            throw new IllegalArgumentException("Channel name cannot be empty");
        synchronized (_channelReservationLocks.get(dir)) {
            Path file = dir.resolve(CHANNEL_RESERVATION_FOLDER_NAME).resolve(name + JSON_TYPE_FORMAT);
            if(file.toFile().exists()) {
                String json = new String(Files.readAllBytes(file));
                return new Gson().fromJson(json, ChannelReservation.class);
            } else {
                return null;
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeChannelReservation(@Nonnull String name) throws IOException {
        if(name.isEmpty())
            throw new IllegalArgumentException("Channel name cannot be empty");
        synchronized (_channelReservationLocks.get(dir)) {
            File file = dir.resolve(CHANNEL_RESERVATION_FOLDER_NAME).resolve(name + JSON_TYPE_FORMAT).toFile();
            if(file.exists())
                file.delete();
        }
    }

}
