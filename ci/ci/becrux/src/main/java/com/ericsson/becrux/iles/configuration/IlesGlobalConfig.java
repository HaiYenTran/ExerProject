package com.ericsson.becrux.iles.configuration;

import com.ericsson.becrux.base.common.configuration.FormValidator;
import com.ericsson.becrux.base.common.core.NodeGuardian;
import com.ericsson.becrux.base.common.dao.ComponentDao;
import com.ericsson.becrux.base.common.dao.EventDao;
import com.ericsson.becrux.base.common.dao.filedb.JsonComponentDao;
import com.ericsson.becrux.base.common.dao.filedb.JsonEventDao;
import com.ericsson.becrux.base.common.exceptions.BecruxDirectoryException;
import com.ericsson.becrux.iles.data.IlesComponentFactory;
import com.ericsson.becrux.iles.data.IlesImsBaseline;
import com.ericsson.becrux.iles.eiffel.events.IlesEventFactory;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import java.io.IOException;
import java.util.List;


/**
 * ILES Global Configuration.
 * Configures ILES global configuration e.g. ILES Tools/Script Directory path,
 * Component DAO path and Event DAO path.
 * Note: addition params was Iles Guardian and Iles DAO.
 */
public class IlesGlobalConfig extends Builder {

    private static IlesImsBaseline processingBaseline = new IlesImsBaseline();

    public IlesGlobalConfig() {}

    // Singleton pattern implementation
    public static IlesGlobalConfig getInstance() {
        return Holder.INSTANCE;
    }

    public IlesImsBaseline getProcessingBaseline() {
        return this.processingBaseline;
    }

    public void setProcessingBaseline(IlesImsBaseline baseline) {
        this.processingBaseline = baseline;
    }

    public IlesDirectory getIlesDirectory() { return getDescriptor().ilesDir; }

    public ComponentDao getComponentDao() { return getDescriptor().componentDao; }

    public EventDao getEventDao() { return getDescriptor().eventDao; }

    public void synchonizeDAO() throws Exception {
        getDescriptor().synchronizeDatabase();
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        // All Jenkins global configuration fields must have getters and setters in this class
        // and textboxes in global.jelly file!!!
        private List<NodeGuardian> ilesGuardians;

        private String ilesDirPath;
        private IlesDirectory ilesDir;

        private String ilesDaoPath;

        private String componentDaoPath;
        private ComponentDao componentDao;

        private String eventDaoPath;
        private EventDao eventDao;

        public DescriptorImpl() {
            load();
            try {
                // fix deployment issue
                synchronizeDatabase();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return false;
        }

        @Override
        public String getDisplayName() {
            return "ILES CI Configurations ";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);

            synchronizeDatabase();

            save();
            return super.configure(req, formData);
        }

        private void synchronizeDatabase() throws FormException{
            this.componentDao = new JsonComponentDao(componentDaoPath, IlesComponentFactory.getInstance());
            this.eventDao = new JsonEventDao(eventDaoPath, IlesEventFactory.getInstance());

            try {
                this.ilesDir = new IlesDirectory(ilesDirPath);
            } catch (BecruxDirectoryException ex) {
                throw  new FormException(ex.getMessage(), "ilesDirPath");
            }
        }


        public FormValidation doCheckIlesDirPath(@QueryParameter String value) {
            return FormValidator.isDir(value);
        }

        public FormValidation doCheckIlesDaoPath(@QueryParameter String value) {
            return FormValidator.isDir(value);
        }

        public FormValidation doCheckComponentDaoPath(@QueryParameter String value) {
            return FormValidator.isDir(value);
        }

        public FormValidation doCheckEventDaoPath(@QueryParameter String value) {
            return FormValidator.isDir(value);
        }

         public List<NodeGuardian> getIlesGuardians() {
            return ilesGuardians;
        }

        public void setIlesGuardians(List<NodeGuardian> ilesGuardians) {
            this.ilesGuardians = ilesGuardians;
        }

        public String getIlesDirPath() {
            return ilesDirPath;
        }

        public void setIlesDirPath(String directoryPath){
            this.ilesDirPath = directoryPath;
        }

        public String getIlesDaoPath() {
            return ilesDaoPath;
        }

        public void setIlesDaoPath(String path) {
            this.ilesDaoPath = path;
        }

        public String getComponentDaoPath() {
            return componentDaoPath;
        }

        public void setComponentDaoPath(String path) {
            this.componentDaoPath = path;
        }

        public String getEventDaoPath() {
            return eventDaoPath;
        }

        public void setEventDaoPath(String path) {
            this.eventDaoPath = path;
        }
    }

    /**
     * Holder of Singleton instance.
     */
    private static class Holder {
        static final IlesGlobalConfig INSTANCE = new IlesGlobalConfig();
    }
}
