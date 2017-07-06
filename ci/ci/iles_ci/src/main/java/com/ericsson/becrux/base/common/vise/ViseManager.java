package com.ericsson.becrux.base.common.vise;

import com.ericsson.becrux.base.common.vise.reservation.ReservationIdentifier;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Manages the vise channels used in the NWFT CI loops.
 *
 * @author emacmyc
 */
public interface ViseManager {
    /**
     * Returns a reserved vise value that was available at the moment in a specific pool.
     *
     * @param poolName from which VisePool channel should be reserved
     * @param identifier unique job identifier that reserves the vise value
     * @throws IOException in case of database access problems
     * @return vise value that is reserved to specified identifier
     */
    ViseChannel reserveViseChannelFromPool(String poolName, ReservationIdentifier identifier) throws IOException;

    /**
     * Reserve a specific vise channel.
     * @param channel the vise to reserve
     * @param identifier unique job identifier that reserves the vise value
     * @return Vise value that is reserved to specified identifier
     * @throws IOException in case of database access problems
     */
    ViseChannel reserveViseChannel(@Nonnull ViseChannel channel, @Nonnull ReservationIdentifier identifier) throws IOException;

    /**
     * Frees a vise value by name
     *
     * @param channelName name of vise value which is supposed to be freed
     * @throws IOException in case of database access problems
     */
    void freeViseChannel(String channelName) throws IOException;
}
