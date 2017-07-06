package com.ericsson.becrux.iles.visemanager;

import com.ericsson.becrux.base.common.configuration.ViseChannelGlobalConfig;
import com.ericsson.becrux.base.common.core.NwftBuildStep;
import com.ericsson.becrux.base.common.dao.ViseChannelDao;
import com.ericsson.becrux.base.common.utils.BecruxBuildBadgeAction;
import com.ericsson.becrux.base.common.vise.parameters.ReservedViseChannelParameterValue;
import com.ericsson.becrux.base.common.vise.reservation.ChannelReservation;
import com.ericsson.becrux.base.common.vise.reservation.ReservationIdentifier;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.List;

/**
 * Created by thien.d.vu on 12/27/2016.
 */
public class MultiViseManagerBuildStep_UpdateIdentifier extends NwftBuildStep {
    protected static final Object _lock = new Object();

    @DataBoundConstructor
    public MultiViseManagerBuildStep_UpdateIdentifier() {}

    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {

        ViseChannelDao dao = ViseChannelGlobalConfig.getInstance().getDao();

        ReservationIdentifier identifier = new ReservationIdentifier(build);
        ReservedViseChannelParameterValue channel;

        List<ReservedViseChannelParameterValue> channels = getAllNwftParametersOfType(build, ReservedViseChannelParameterValue.class);

        // TODO: why we have this synchronized ?
        synchronized (_lock) {
            if (channels.size() < 1) {
                listener.getLogger().println("No VISE value in build parameters.");
                return false;
            } else if (channels.size() > 1) {
                listener.getLogger().println("More than one VISE value provided.");
                return false;
            } else  {
                channel = channels.get(0);

                try {
                    // Add badge build
                    build.addAction(
                            new BecruxBuildBadgeAction(channel.getViseChannel().getFullName()));
                    
                    dao.saveChannelReservation(new ChannelReservation(channel.getViseChannel().getFullName(), identifier));
                    return true;
                } catch (IOException e) {
                    listener.getLogger().println("[FAILED] saveChannelReservation.");
                    e.printStackTrace();
                    return false;
                }
            }
        }
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "CI: MultiViseManager update identifier to current job";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }
}
