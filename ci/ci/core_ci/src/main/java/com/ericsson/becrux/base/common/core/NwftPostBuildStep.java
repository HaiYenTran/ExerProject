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

    /**
     *
     * @param build the current
     * @param additionDescription
     * @throws IOException
     */
    protected void addBuildDescription(AbstractBuild<?, ?> build, String additionDescription) throws IOException{
        StringBuilder builder = new StringBuilder();
        String currentDescription = build.getDescription();
        currentDescription = currentDescription == null? "" : currentDescription; // for avoid NullPointerException

        builder.append(currentDescription);

        /*
            Check if the current build description has <br> or not, if not add <br> for moving to next line
            Tag '<br>' only support on IE browser for Chrome, it should be \n or \r but right now our client only use IE
         */
        String lastPart = currentDescription;
        if (currentDescription.length() > 4) { // for avoiding java.lang.StringIndexOutOfBoundsException
            lastPart = currentDescription.substring(currentDescription.length() - 4, currentDescription.length());
        }

        if (!lastPart.isEmpty() && !("<br>").equals(lastPart)) {
            builder.append("<br>");
        }

        builder.append(additionDescription);

        build.setDescription(builder.toString());
    }
}
