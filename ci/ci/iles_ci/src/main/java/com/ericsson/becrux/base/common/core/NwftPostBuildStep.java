package com.ericsson.becrux.base.common.core;

import com.ericsson.becrux.base.common.utils.BecruxBuildBadgeAction;
import com.ericsson.becrux.base.common.utils.BuildParametersExtractor;
import hudson.model.AbstractBuild;
import hudson.model.ParameterValue;
import hudson.tasks.Notifier;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Created by emiwaso on 2016-12-19.
 */
public abstract class NwftPostBuildStep extends Notifier {

    protected void addNwftBuildParameter(AbstractBuild<?, ?> build, NwftParameterValue param) {
        BuildParametersExtractor extractor = new BuildParametersExtractor(build);
        extractor.addNwftParameter(param);
    }

    protected void addNwftBuildParameters(AbstractBuild<?,?> build, Collection<NwftParameterValue> params) {
        BuildParametersExtractor extractor = new BuildParametersExtractor(build);
        extractor.addNwftParameters(params);
    }

    protected void addNwftBuildParameters(AbstractBuild<?,?> build, NwftParameterValue... params) {
        BuildParametersExtractor extractor = new BuildParametersExtractor(build);
        extractor.addNwftParameters(params);
    }

    protected List<ParameterValue> getAllParameters(AbstractBuild<?,?> build) {
        BuildParametersExtractor extractor = new BuildParametersExtractor(build);
        return extractor.getAllParameters();
    }

    protected List<NwftParameterValue> getAllNwftParameters(AbstractBuild<?,?> build) {
        BuildParametersExtractor extractor = new BuildParametersExtractor(build);
        return extractor.getAllNwftParameters();
    }

    protected <T extends ParameterValue> List<T> getAllParametersOfType(AbstractBuild<?,?> build, Class<T> type) {
        BuildParametersExtractor extractor = new BuildParametersExtractor(build);
        return extractor.getAllParametersOfType(type);
    }

    protected <T extends NwftParameterValue> List<T> getAllNwftParametersOfType(AbstractBuild<?,?> build, Class<T> type) {
        BuildParametersExtractor extractor = new BuildParametersExtractor(build);
        return extractor.getAllNwftParametersOfType(type);
    }

    protected void addBuildBadge(AbstractBuild<?, ?> build, String description) {
        build.addAction(new BecruxBuildBadgeAction("\n" + description));
    }

    protected void addBuildDescription(AbstractBuild<?, ?> build, String description) throws IOException{
        StringBuilder builder = new StringBuilder();
        String currentDescription = build.getDescription();

        if (currentDescription != null && !currentDescription.isEmpty()) {
            builder.append(currentDescription);
            if (!("\n").equals(currentDescription.substring(0, currentDescription.length() - 2))) {
                builder.append("\n");
            }
        }

        builder.append(description);
        build.setDescription(builder.toString());
    }
}
