package com.ericsson.becrux.base.common.vise;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by emiwaso on 2016-11-17.
 */
public class VisePool {

    String name;
    List<String> channelNames = new ArrayList<>();

    public VisePool(@Nonnull String name) {
        setName(name);
    }

    public VisePool(@Nonnull String name, @Nonnull String channel) {
        setName(name);
        this.channelNames.add(channel);
    }

    public VisePool(@Nonnull String name, @Nonnull List<String> channels) {
        setName(name);
        this.channelNames.addAll(channels);
    }

    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        if (name.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");
        this.name = name;
    }

    public List<String> getViseChannels() {
        return channelNames;
    }

    public void setViseChannels(@Nonnull List<String> channels) {
        this.channelNames = channels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VisePool visePool = (VisePool) o;

        if (name != null ? !name.equals(visePool.name) : visePool.name != null) return false;
        return channelNames != null ? channelNames.equals(visePool.channelNames) : visePool.channelNames == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (channelNames != null ? channelNames.hashCode() : 0);
        return result;
    }
}
