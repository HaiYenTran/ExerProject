package com.ericsson.becrux.base.common.core;

import com.ericsson.becrux.base.common.utils.BuildParametersExtractor;
import hudson.model.AbstractBuild;
import hudson.model.ParameterValue;
import hudson.tasks.BuildStep;
import hudson.tasks.Builder;

import java.util.Collection;
import java.util.List;

/**
 * Created by emacmyc on 2016-11-30.
 */
public class NwftBuildStep extends Builder implements BuildStep {

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
}
