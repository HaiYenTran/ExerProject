package com.ericsson.becrux.base.common.vise.reservation;

import javax.annotation.Nonnull;

/**
 * Created by emiwaso on 2016-12-13.
 */
public final class ChannelReservation {

    private String channelName;

    private ReservationIdentifier identifier;

    // Default constructor not allowed
    private ChannelReservation() {

    }

    public ChannelReservation(@Nonnull String channelName, @Nonnull ReservationIdentifier identifier) {

        if(channelName.isEmpty())
            throw new IllegalArgumentException("Channel name cannot be empty");

        this.channelName = channelName;
        this.identifier = identifier;
    }

    public String getName() { return channelName; }

    public ReservationIdentifier getIdentifier() { return identifier; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChannelReservation that = (ChannelReservation) o;

        if (channelName != null ? !channelName.equals(that.channelName) : that.channelName != null) return false;
        return identifier != null ? identifier.equals(that.identifier) : that.identifier == null;
    }

    @Override
    public int hashCode() {
        int result = channelName != null ? channelName.hashCode() : 0;
        result = 31 * result + (identifier != null ? identifier.hashCode() : 0);
        return result;
    }
}
