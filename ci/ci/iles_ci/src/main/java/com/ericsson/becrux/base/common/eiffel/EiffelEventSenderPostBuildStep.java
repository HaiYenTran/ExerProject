package com.ericsson.becrux.base.common.eiffel;

import com.ericsson.becrux.base.common.core.NwftPostBuildStep;
import com.ericsson.becrux.base.common.eiffel.parameters.EiffelEventParameterValue_ToSend;
import com.ericsson.duraci.eiffelmessage.sending.exceptions.EiffelMessageSenderException;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by emiwaso on 2016-12-19.
 */
public class EiffelEventSenderPostBuildStep extends NwftPostBuildStep {

    private String tag;
    private boolean customTag;

    @DataBoundConstructor
    public EiffelEventSenderPostBuildStep(String tag, boolean customTag) {
        this.tag = tag;
        this.customTag = customTag;
    }

    public String getTag() { return tag; }

    public boolean getCustomTag() { return customTag; }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        try{
            IEventSender sender = initSender();
            // Get all events need to be sent
            List<EiffelEventParameterValue_ToSend> events =
                    getAllNwftParametersOfType(build, EiffelEventParameterValue_ToSend.class)
                            .stream().filter(param -> !param.isUsed()).collect(Collectors.toList());
            for(EiffelEventParameterValue_ToSend e : events) {
                sender.sendEvent(e.getEvent(), tag);
                // After sending each value, set value used = true
                e.setUsed(true);
            }

        } catch(EiffelMessageSenderException ex) {
            listener.getLogger().println(ex);
            return false;
        }
        return true;
    }

    protected IEventSender initSender() {
        return new EiffelEventSender();
    }

}
