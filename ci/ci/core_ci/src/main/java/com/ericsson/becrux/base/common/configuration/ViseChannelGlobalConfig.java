package com.ericsson.becrux.base.common.configuration;

import com.ericsson.becrux.base.common.dao.ViseChannelDao;
import com.ericsson.becrux.base.common.dao.filedb.JsonViseChannelDao;
import com.ericsson.becrux.base.common.vise.ViseChannel;
import com.ericsson.becrux.base.common.vise.ViseChannelName;
import com.ericsson.becrux.base.common.vise.ViseChannelPool;
import com.ericsson.becrux.base.common.vise.VisePool;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Global configuration for Vise Channels.
 */
public class ViseChannelGlobalConfig extends Builder {

    public ViseChannelGlobalConfig() {}

    // Singleton pattern implementation
    public static ViseChannelGlobalConfig getInstance() {
        return Holder.INSTANCE;
    }

    public ViseChannelDao getDao() {
        return getDescriptor().getViseDao();
    }

    public void setDaoPath(String path) {
        getDescriptor().setViseDaoPath(path);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        //Pool name that contains necessary VISE channels
        private List<ViseChannelPool> viseChannelPools;
        //Path of ILES Database folder to store VISE channels
        private String viseDaoPath;

        public DescriptorImpl() {
            load();
            synchronizeDatabase();
        }

        /**
         * Get VISE channel pools.
         *
         * @return
         */
        public List<ViseChannelPool> getViseChannelPools() {
            return viseChannelPools;
        }

        /**
         * Set VISE channel pools.
         *
         * @return
         */
        public void setViseChannelPools(List<ViseChannelPool> viseChannelPools) {
            this.viseChannelPools = viseChannelPools;
        }

        public String getViseDaoPath() {
            return viseDaoPath;
        }

        public void setViseDaoPath(String viseDaoPath) {
            this.viseDaoPath = viseDaoPath;
        }
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return false;
        }

        @Override
        public String getDisplayName() {
            return "CI: Vise Channel Global Configuration";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);

            synchronizeDatabase();

            save();
            return super.configure(req, formData);
        }

        public ViseChannelDao getViseDao() {
            return new JsonViseChannelDao(viseDaoPath);
        }

        /**
         * Synchronize input from config with Which was saved in database.
         * NOTE: If we go with Singleton instance contain data we may not need this method anymore.
         */
        private void synchronizeDatabase() {
            try {
                // Prepare newPools VisePool list
                List<VisePool> newPools = new ArrayList<>();
                List<ViseChannel> newChannels = new ArrayList<>();
                // Convert ViseChannelPool to VisePool
                if (viseChannelPools != null) {
                    for (ViseChannelPool pool : viseChannelPools) {
                        List<String> viseNames = new ArrayList<>();
                        for (ViseChannelName name : pool.getViseChannelName()) {
                            if (name.getName().contains("VISE0")) {
                                viseNames.add(name.getName());
                            } else {
                                viseNames.add("VISE0" + name.getName());
                            }
                            if (newChannels.contains(new ViseChannel(name.getName()))) { // check if it's same VISE channel name on different pool
                                for (ViseChannel tmp : newChannels) {
                                    if (tmp.getFullName().contains(name.getName())) {
                                        if (!tmp.getIpAddress().equals(name.getIpAddr())) {
                                            throw new FormException("[ERROR] Channel IP Address: " + name.getName(), name.getName());
                                        }
                                    }
                                }
                            } else {
                                newChannels.add(new ViseChannel(name.getName(), name.getIpAddr()));
                            }
                        }
                        VisePool visePool = new VisePool(pool.getViseChannelPoolName(), viseNames);
                        newPools.add(visePool);
                    }
                }

                //Prepare
                //newChannels is all Channel in all Pool from Global configuration
                //oldChannels is all Channel from database
                List<ViseChannel> commonChannels = new ArrayList<>();
                List<ViseChannel> carryChannelNews = new ArrayList<>();
                List<ViseChannel> carryChannelOlds = new ArrayList<>();
//            List<ViseChannel> newChannels = new ArrayList<>();
                List<ViseChannel> oldChannels = new ArrayList<>();
                try {
                    oldChannels = getViseDao().loadViseChannels();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Find commonChannels, carryChannelNews, carryChannelOlds in all pool
                // between:
                // newChannels is all Channel in all Pool from Global configuration
                // oldChannels is all Channel from database
                // commonChannels will be keep
                // carryChannelNews will be updated
                // carryChannelOlds will be removed
                if (newPools == null && oldChannels != null) {
                    carryChannelNews = newChannels;
                } else if (newPools == null && oldChannels != null) {
                    carryChannelOlds = oldChannels;
                } else {
                    for (ViseChannel newChannel : newChannels) {
                        for (ViseChannel oldChannel : oldChannels) {
                            if (newChannel.equals(oldChannel))
                                commonChannels.add(newChannel);
                        }
                    }
                    for (ViseChannel newChannel : newChannels) {
                        if (!commonChannels.contains(newChannel))
                            carryChannelNews.add(newChannel);
                    }
                    for (ViseChannel oldChannel : oldChannels) {
                        if (!commonChannels.contains(oldChannel))
                            carryChannelOlds.add(oldChannel);
                    }
                }

                //Update Pools and Vise value into database
                if (carryChannelNews != null && carryChannelOlds == null) { // Save all pools and channels
                    getViseDao().saveVisePools(newPools);
                    getViseDao().saveViseChannels(newChannels);
                } else if (carryChannelNews == null && carryChannelOlds != null) { // Delete all pools and channels
                    getViseDao().removeAllVisePools();
                    getViseDao().removeAllViseChannels();
                } else {
                    //Remove value in carryChannelOlds
                    for (ViseChannel channel : carryChannelOlds) {
                        getViseDao().removeViseChannel(channel);
                    }
                    //Update value in carryChannelNews
                    for (ViseChannel channel : carryChannelNews) {
                        getViseDao().saveViseChannel(channel);
                    }
                    //Update pool in newPools: delete all old pools in database and save new pools
                    getViseDao().saveVisePools(newPools);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Holder of Singleton instance.
     */
    private static class Holder {
        static final ViseChannelGlobalConfig INSTANCE = new ViseChannelGlobalConfig();
    }
}
