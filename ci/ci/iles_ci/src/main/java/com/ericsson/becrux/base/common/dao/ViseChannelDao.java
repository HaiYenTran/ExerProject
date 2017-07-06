package com.ericsson.becrux.base.common.dao;

import com.ericsson.becrux.base.common.vise.ViseChannel;
import com.ericsson.becrux.base.common.vise.VisePool;
import com.ericsson.becrux.base.common.vise.reservation.ChannelReservation;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

/**
 * The Dao for Vise Channels
 */
public interface ViseChannelDao {

    /**
     * Validate the DAO, checking for issues.
     * @return
     * @throws Exception if anything fail
     */
    boolean validate() throws Exception;

    /**
     * Returns all vise pools from database
     *
     * @return List of Vise pools loaded from database
     * @throws IOException in case of file handling problems
     */
    List<VisePool> loadVisePools() throws IOException;

    /**
     * Saves provided list of vise pools
     *
     * @param pools - List of VisePool objects that will be saved (all other VisePools will be removed)
     * @throws IOException in case of file handling problems
     */
    void saveVisePools(@Nonnull List<VisePool> pools) throws IOException;

    /**
     * Removes all vise pools from database
     *
     * @throws IOException in case of file handling problems
     */
    void removeAllVisePools() throws IOException;

    /**
     * Returns specific vise pool by name
     *
     * @param name - name of VisePool that will be loaded
     * @return VisePool object loaded from database
     * @throws IOException in case of file handling problems
     */
    VisePool loadVisePool(@Nonnull String name) throws IOException;

    /**
     * Saves provided vise pool
     *
     * @param pool - VisePool object that will be saved
     * @throws IOException in case of file handling problems
     */
    void saveVisePool(@Nonnull VisePool pool) throws IOException;

    /**
     * Removes specific vise pool by object comparison
     *
     * @param pool - VisePool object that will be removed
     * @throws IOException in case of file handling problems
     */
    void removeVisePool(@Nonnull VisePool pool) throws IOException;

    /**
     * Removes specific vise pool by name
     *
     * @param name - name of VisePool that will be removed
     * @throws IOException in case of file handling problems
     */
    void removeVisePool(@Nonnull String name) throws IOException;

    /**
     * Returns all vise channels from the database
     *
     * @return loaded list of ViseChannel objects
     * @throws IOException in case of file handling problems
     */
    List<ViseChannel> loadViseChannels() throws IOException;

    /**
     * Saves provided list of vise channels
     *
     * @param channels - list of ViseChannels which will be saved (all other channels will be removed)
     * @throws IOException in case of file handling problems
     */
    void saveViseChannels(@Nonnull List<ViseChannel> channels) throws IOException;

    /**
     * Remove all vise channels from the database
     *
     * @throws IOException in case of file handling problems
     */
    void removeAllViseChannels() throws IOException;

    /**
     * Load specific vise value by value number
     *
     * @param number - number representation of ViseChannel which will be loaded from database
     * @return loaded ViseChannel object
     * @throws IOException in case of file handling problems
     */
    ViseChannel loadViseChannel(int number) throws IOException;

    /**
     * Load specific vise value by name
     *
     * @param name - name of ViseChannel which will be loaded from database
     * @return loaded ViseChannel object
     * @throws IOException in case of file handling problems
     */
    ViseChannel loadViseChannel(@Nonnull String name) throws IOException;

    /**
     * Save specific vise value
     *
     * @param channel - ViseChannel object which will be saved
     * @throws IOException in case of file handling problems
     */
    void saveViseChannel(@Nonnull ViseChannel channel) throws IOException;

    /**
     * Removes specific vise value by object comparison
     *
     * @param channel - value object which will be removed
     * @throws IOException in case of file handling problems
     */
    void removeViseChannel(@Nonnull ViseChannel channel) throws IOException;

    /**
     * Removes specific vise value by value number
     *
     * @param number - number representation of ViseChannel that will be removed
     * @throws IOException in case of file handling problems
     */
    void removeViseChannel(int number) throws IOException;

    /**
     * Removes specific vise value by name
     *
     * @param name - name of the ViseChannel which will be removed
     * @throws IOException in case of file handling problems
     */
    void removeViseChannel(@Nonnull String name) throws IOException;

    /**
     * Saves value reservation object to database
     *
     * @param reservation - reservation object
     * @throws IOException in case of database access problems
     */
    void saveChannelReservation(@Nonnull ChannelReservation reservation) throws IOException;

    /**
     * Loads value reservation object by value name
     *
     * @param name - value name for which to load reservation object
     * @throws IOException in case of database access problems
     */
    ChannelReservation loadChannelReservation(@Nonnull String name) throws IOException;

    /**
     * Removes value reservation object from database
     *
     * @param name - value name for which reservation is to be removed
     * @throws IOException in case of database access problems
     */
    void removeChannelReservation(@Nonnull String name) throws IOException;

    /**
     * In case all the queue lock not initialize correctly
     */
    void checkSynchronizeLock();
}
