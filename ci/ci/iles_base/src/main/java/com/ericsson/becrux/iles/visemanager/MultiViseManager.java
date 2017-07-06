package com.ericsson.becrux.iles.visemanager;

import com.ericsson.becrux.base.common.configuration.ViseChannelGlobalConfig;
import com.ericsson.becrux.base.common.dao.ViseChannelDao;
import com.ericsson.becrux.base.common.vise.AbstractViseManager;
import com.ericsson.becrux.base.common.vise.ViseChannel;
import com.ericsson.becrux.base.common.vise.ViseManager;
import com.ericsson.becrux.base.common.vise.VisePool;
import com.ericsson.becrux.base.common.vise.reservation.ChannelReservation;
import com.ericsson.becrux.base.common.vise.reservation.ReservationIdentifier;
import com.ericsson.becrux.iles.utils.JenkinsHelper;
import hudson.model.AbstractBuild;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Created by emiwaso on 2016-11-29.
 */
public final class MultiViseManager extends AbstractViseManager implements ViseManager {

    private ViseChannelDao dao;
    private JenkinsHelper jenkinsHelper = new JenkinsHelper();

    public MultiViseManager() {
        this.dao = ViseChannelGlobalConfig.getInstance().getDao();
    }

    /** {@inheritDoc} */
    @Override
    public ViseChannel reserveViseChannelFromPool(@Nonnull String poolName, @Nonnull ReservationIdentifier identifier) throws IOException {

        if(poolName.isEmpty())
            throw new IllegalArgumentException("Pool name is empty");

        ViseChannel reservedChannel = null;

        synchronized (_lock) {

            VisePool pool = dao.loadVisePool(poolName); //Load specified vise pool from database
            if(pool == null)
                throw new IllegalArgumentException("Invalid pool name.");

            for(String channelName : pool.getViseChannels()) {
                if (reservedChannel == null) {
                    ViseChannel channel = dao.loadViseChannel(channelName); //Load specified vise value from the database
                    reservedChannel = reserveViseChannel(channel, identifier);
                }
            }
        }

        return reservedChannel;
    }

    /** {@inheritDoc} */
    @Override
    public ViseChannel reserveViseChannel(@Nonnull ViseChannel channel, @Nonnull ReservationIdentifier identifier) throws IOException {
        validateIdentifier(identifier);

        synchronized (_lock) {
            dao.saveViseChannel(channel); // if the channel already exit, it will update the channel

            ChannelReservation reservation = dao.loadChannelReservation(channel.getFullName()); //Load reservation object for specific value
            if(reservation == null) { //Channel is not reserved, reserve nad return it
                dao.saveChannelReservation(new ChannelReservation(channel.getFullName(), identifier)); //Save registration status

                return channel; //Return value
            } else { //Channel is reserved, check job status
                String jobName = reservation.getIdentifier().getJobName();
                AbstractBuild<?, ?> build = null;

                try {
                    build = jenkinsHelper.findJob(jobName).getBuild(reservation.getIdentifier().getJobNumber());
                } catch(NullPointerException ex) {} //Job doesn't exist, continue

                if(build == null || !build.isBuilding()) { //Build doesn't exist or isn't running
                    dao.saveChannelReservation(new ChannelReservation(reservation.getName(), identifier)); //Save registration status
                    return channel;
                }
            }
        }
        return null;
    }

    private boolean validateIdentifier(@Nonnull ReservationIdentifier identifier) {
        if(identifier.getJobName().isEmpty() || identifier.getJobNumber().isEmpty())
            throw new IllegalArgumentException("Reservation identifier is invalid");

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void freeViseChannel(@Nonnull String channelName) throws IOException {

        synchronized (_lock) {

            ChannelReservation reservation = dao.loadChannelReservation(channelName);
            if(reservation != null) { //Only try to free value if it's not taken, multiple calls to freeViseChannel are therefore allowed

                String jobName = reservation.getIdentifier().getJobName();
                AbstractBuild<?, ?> build = null;
                try {
                    build = jenkinsHelper.findJob(jobName).getBuild(reservation.getIdentifier().getJobNumber());
                } catch(NullPointerException ex) {} //Job doesn't exist, continue

                if(build != null && build.isBuilding()) //Only try to free if taken by something, but don't throw error if it is free
                    dao.removeChannelReservation(channelName);
            }
        }
    }
}
