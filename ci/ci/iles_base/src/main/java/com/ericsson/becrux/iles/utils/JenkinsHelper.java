package com.ericsson.becrux.iles.utils;

import hudson.model.Item;
import hudson.model.Project;
import jenkins.model.Jenkins;

/**
 * Contain all methods to support integration with Jenkins.
 *
 * @author dung.t.bui
 */
public class JenkinsHelper {

    public JenkinsHelper() {}

    /**
     * Find Jenkins Job with name
     * @param jobName the name of the job
     * @return {@link Project}
     */
    public Project<?, ?> findJob(String jobName) {
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins == null)
            throw new NullPointerException("Critical problem: Jenkins instance not started, or already shut down!");

        Item item = jenkins.getItem(jobName);
        if (item == null)
            throw new NullPointerException(jobName + " job type does not exist in Jenkins workspace.");

        return (Project<?, ?>) item;
    }

}
