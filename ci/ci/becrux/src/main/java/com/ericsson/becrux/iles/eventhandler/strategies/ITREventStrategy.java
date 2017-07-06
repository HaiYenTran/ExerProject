package com.ericsson.becrux.iles.eventhandler.strategies;

import com.ericsson.becrux.base.common.core.*;
import com.ericsson.becrux.base.common.dao.ComponentDao;
import com.ericsson.becrux.base.common.data.Component;
import com.ericsson.becrux.base.common.eiffel.events.Event;
import com.ericsson.becrux.base.common.eiffel.parameters.EiffelEventParameterValue_ToSend;
import com.ericsson.becrux.base.common.eventhandler.EventHandlingResult;
import com.ericsson.becrux.base.common.eventhandler.EventValidationResult;
import com.ericsson.becrux.base.common.eventhandler.exceptions.DatabaseBaselineVersionMissingException;
import com.ericsson.becrux.base.common.eventhandler.exceptions.EventHandlingException;
import com.ericsson.becrux.base.common.eventhandler.strategies.EventHandlingStrategy;
import com.ericsson.becrux.base.common.vise.ViseChannel;
import com.ericsson.becrux.base.common.vise.parameters.ReservedViseChannelParameterValue;
import com.ericsson.becrux.base.common.vise.reservation.ReservationIdentifier;
import com.ericsson.becrux.iles.data.*;
import com.ericsson.becrux.base.common.loop.ComponentParameterValue;
import com.ericsson.becrux.base.common.loop.Phase;
import com.ericsson.becrux.base.common.loop.PhaseStatus;
import com.ericsson.becrux.iles.eiffel.events.EventParamenterValue;
import com.ericsson.becrux.iles.eiffel.events.IlesEventFactory;
import com.ericsson.becrux.iles.exceptions.BaselineVotingException;
import com.ericsson.becrux.iles.exceptions.ViseChannelException;
import com.ericsson.becrux.iles.utils.IlesVersionHelper;
import com.ericsson.becrux.iles.visemanager.MultiViseManager;
import com.ericsson.becrux.iles.configuration.IlesGlobalConfig;
import com.ericsson.becrux.base.common.eiffel.events.impl.BTFEvent;
import com.ericsson.becrux.base.common.eiffel.events.impl.ITREvent;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Cause;
import hudson.model.Run;
import jenkins.model.Jenkins;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import static com.ericsson.becrux.base.common.utils.StringHelper.parseParameterJsonFormatValue;

/**
 * Implementation for ITR event handling
 *
 */
public class ITREventStrategy extends EventHandlingStrategy {

    public static final String INSTALLATION_ABLE = "INSTALLATION_ABLE";
    public static final String PROVISIONING_ABLE = "PROVISIONING_ABLE";
    public static final String TESTEXEC_ABLE = "TESTEXEC_ABLE";
    public static final String CONFIG_PROPERTIES = "config_properties";
    public static final String VISE_INFO = "VISE_INFO";
    public static final String EVENT_TAG = "TAG";
    public static final String QUICK_TEST = "QUICK_TEST";
    public static final String ITR_SENDER_JOB_NAME = "ITR_SENDER_JOB_NAME";
    public static final String ITR_SENDER_JOB_BUILD = "ITR_SENDER_JOB_BUILD";
    public static final String CLIENT_JENKINS = "CLIENT_JENKINS";
    public static final String CUSTOM_PROVISIONING_TOOL_PATH = "PROVISIONING_TOOL_PATH";
    public static final String CUSTOM_TESTEXEC_TOOL_PATH = "TESTEXEC_TOOL_PATH";
    public static final String CUSTOM_PHOENIX_TOOL_PATH = "PHOENIX_TOOL_PATH";
    private String poolName;
    private String workerJobName;
    private AbstractBuild build;
    private ComponentDao compDao = IlesGlobalConfig.getInstance().getComponentDao();
    private MultiViseManager visemgr = new MultiViseManager();


    /**
     * Constructor.
     * @param workerJobName Name of worker job
     * @param poolName Name of Vise channel pool to be used
     * @param build Base implementation that runs build
     */
    public ITREventStrategy(String workerJobName, String poolName, AbstractBuild build) {
        this.workerJobName = workerJobName;
        this.poolName = poolName;
        this.build = build;
    }

    private void println(String text) {
        this.getHandler().getLog().println(text);
    }

    /**
     * Get the build log URL for the job which handle this Strategy
     * @return
     */
    private String getHandlerJobUrl() {
        return Jenkins.getInstance().getRootUrl() + this.build.getUrl() + "consoleFull";
    }

    private boolean requestFromINT(ITREvent e) {
        return e.getProduct().equalsIgnoreCase(Int.class.getSimpleName());
    }

    private boolean hasViseInfo(ITREvent e) {
        if (e.getParameters().containsKey(VISE_INFO))
                return true;
        return false;
    }

    private boolean isStandaloneRequest(ITREvent e) {
        return e.getProduct().equalsIgnoreCase(ITREvent.STANDALONE_TEST_BASELINE_CONFIG);
    }

    private boolean isBaseLineType(ITREvent eITR) {
        return eITR.getLoopType() == ITREvent.LoopType.BASELINE;
    }

    private boolean isTestType(ITREvent eITR) {
        return eITR.getLoopType() == ITREvent.LoopType.TEST;
    }

    /**************************************************************************
     * Setters and getters
     *************************************************************************/

    /**
     * Get Vise Channel.
     * The Vise channel could be on ITR Event or on defined pool.
     */
    private NwftParameterValue getViseChannel(ITREvent eITR) throws Exception {
        ViseChannel channel =  hasViseInfo(eITR) ?
                getViseChannelFromEvent(eITR) :
                getViseChannelFromPool(this.poolName);

        if (channel == null) {
            throw new ViseChannelException("ERROR: No VISE Channels available");
        }

        // for more log details
        println("\n\tVISE channel: " + channel.getFullName());

        return new ReservedViseChannelParameterValue(channel.getFullName(), channel);
    }

    /**
     * Get specific VISE channel from ITR Event.
     */
    private ViseChannel getViseChannelFromEvent(ITREvent eITR) throws Exception {
        String viseInfo = eITR.getParameters().get(VISE_INFO);
        if (viseInfo != null) {
            String viseName = parseParameterJsonFormatValue("name", viseInfo);
            String viseIp = parseParameterJsonFormatValue("ip", viseInfo);
            return  visemgr.reserveViseChannel(new ViseChannel(viseName, viseIp), new ReservationIdentifier(build));
        }
        else {
            throw new IllegalArgumentException("ERROR: No Vise channel is provided");
        }
    }

    /**
     * Get a free VISE channel from pool.
     */
    private ViseChannel getViseChannelFromPool(String poolName) throws IOException {
        return visemgr.reserveViseChannelFromPool(poolName, new ReservationIdentifier(build));
    }

    /**
     * Get current {@link AbstractBuild}.
     * @return the current build.
     */
    public AbstractBuild getBuild() {
        return build;
    }

    /**
     * Set the {@link AbstractBuild} that is processing this strategy.
     * @param build
     */
    public void setBuild(AbstractBuild build) {
        this.build = build;
    }

    /**************************************************************************
     * Methods for getting parameters from ITR Event.
     *************************************************************************/
    private boolean getInstallableFromParameter(Object value) {
        try {
            return parseParameterJsonFormatValue("installable", value).equals("true");
        }
        catch (Exception e) {
            return false;
        }
    }

    private String getPdbFromParameter(Object value) throws Exception {
        try {
            return parseParameterJsonFormatValue("pdb", value);
        }
        catch (Exception e) {
            return null;
        }
    }

    private String getVersionFromParameter(Object value) throws Exception {
        try {
            return parseParameterJsonFormatValue("version", value);
        } catch (Exception e) {
            throw new Exception(e.getMessage() + ": Version not given in parameters");
        }
    }

    private NwftParameterValue getConfigPropertiesPath(ITREvent e) throws Exception {
        String intConfig = e.getParameters().get(Int.class.getSimpleName().toUpperCase() + "_CONFIG");

        if (intConfig != null) {
            String configPropertiesPath = parseParameterJsonFormatValue(CONFIG_PROPERTIES, intConfig);
            return new CommonParamenterValue(CONFIG_PROPERTIES, configPropertiesPath);
        }
        else {
            throw new Exception("EEROR: The config.properties file not given in parameters");
        }

    }

    private List<NwftParameterValue> getManualRunParams(ITREvent e) {
        return getBuildParams(e, INSTALLATION_ABLE, PROVISIONING_ABLE, TESTEXEC_ABLE);
    }

    private List<NwftParameterValue> getCustomToolPathParams(ITREvent e) {
        return getBuildParams(e, CUSTOM_PROVISIONING_TOOL_PATH, CUSTOM_TESTEXEC_TOOL_PATH, CUSTOM_PHOENIX_TOOL_PATH);
    }

    private List<NwftParameterValue> getBuildParams(ITREvent e, String... keys) {
        List<NwftParameterValue> buildParams  = new ArrayList<>();

        for (String key : keys) {
            if (e.getParameters().containsKey(key))
                buildParams.add(new CommonParamenterValue(key, e.getParameters().get(key)));
        }

        return buildParams;
    }

    /**************************************************************************
     * Build up list of components
     *************************************************************************/
    private List<Component> getListOfSpecifiedNodesForTest(ITREvent e) throws Exception {
        List<Component> components = new ArrayList<>();

        for (String type : IlesComponentFactory.getInstance().getRegisteredClassNames()) {
            String key = type.toUpperCase() + "_CONFIG";

            if (!e.getParameters().containsKey(key))
                continue;

            String nodeConfig = e.getParameters().get(key);
            Component component = IlesComponentFactory.getInstance().create(type);
            component.setInstallable(getInstallableFromParameter(nodeConfig));
            component.setVersion(getVersionFromParameter(nodeConfig));
            component.setPdb(getPdbFromParameter(nodeConfig));
            components.add(component);
        }

        return components;
    }

    private List<Component> getListOfAllNodesForTest(ITREvent eITR) throws Exception {
        Component product = getProductFromITREvent(eITR);
        List<Component> components = new ArrayList<>();

        for (String type : IlesComponentFactory.getInstance().getRegisteredClassNames()) {
            if (product != null && !type.equalsIgnoreCase(product.getType())) {
                if (eITR.getParameters().containsKey(type))
                    components.add(compDao.loadComponent(type, eITR.getParameters().get(type)));
                else
                    components.add(compDao.loadNewestComponent(type, Component.State.BASELINE_APPROVED));
            }
        }

        return components;
    }

    /*
        This method is back up validation in case that if the event somehow not validated.
        Get Component base on ITREvent.product.
        If product type not exit, return exception.
        In case product come from Standalone GUI ITREvent.product == UNKNOWN, the method will return null without exception.
     */
    private Component getProductFromITREvent(ITREvent eITR) throws Exception {
        Component product = IlesComponentFactory.getInstance().getComponentFromEvent(eITR);
        if (product == null && !ITREvent.STANDALONE_TEST_BASELINE_CONFIG.equals(eITR.getProduct())) {
            throw new Exception("Can not get product from ITREvent " + IlesEventFactory.getInstance().toJson(eITR));
        }

        return product;
    }

    private List<Component> prepareForTestJob(ITREvent eITR) throws Exception {
        List<Component> components = isStandaloneRequest(eITR)?
                getListOfSpecifiedNodesForTest(eITR) :
                getListOfAllNodesForTest(eITR);

        Component product = getProductFromITREvent(eITR);
        if (product != null && !components.contains(product))
            components.add(0, product);

        return components;
    }

    private List<Component> prepareForBaselineJob(ITREvent eITR) throws Exception {
        Component product = getProductFromITREvent(eITR);

        if (requestFromINT(eITR)) {
            //Set artifact for INT to store in DB
            FileInputStream config = new FileInputStream(getConfigPropertiesPath(eITR).getValue().toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(config));

            List<String> lines = reader.lines().filter(line -> line.startsWith("intTgz")).collect(Collectors.toList());
            if (lines.size() == 2) {
                String artifactName = null;
                String path = null;

                for (String line : lines) {
                    if (line.startsWith("intTgzName")) {
                        artifactName = line.substring(line.indexOf("=") + 1);
                    }
                    if (line.startsWith("intTgzLocation")) {
                        path = line.substring(line.indexOf("=") + 1);
                    }
                }

                product.setArtifact("file://" + path + "/" + artifactName);

            } else {
                throw new Exception("ERROR: Couldn't find the INT Artifact ");
            }
        }

        product.setState(Component.State.NEW_BUILD);
        compDao.saveComponent(product);

        List<Component> components = new ArrayList<>();
        components.add(product);

        checkLoopRunningOrVoting();

        for (String type : IlesComponentFactory.getInstance().getRegisteredClassNames()) {
            if (type.equalsIgnoreCase(product.getType()))
                continue;

            Component baseline = compDao.loadNewestComponent(type, Component.State.BASELINE_APPROVED);
            if (baseline == null)
                throw new DatabaseBaselineVersionMissingException(eITR, type);

            components.add(baseline);
        }

        return components;
    }

    /**
     * Get the client Job Name which sent ITR to ILES CI
     * @param eITR - ITR event
     * @return - Name of Job which sent ITR to ILES CI
     * @throws Exception
     */
    private String getItrSenderJobName(ITREvent eITR) throws Exception{
        String itrSenderJob = null;
        if (eITR.getParameters().containsKey(ITR_SENDER_JOB_NAME)) {
        itrSenderJob = eITR.getParameters().get(ITR_SENDER_JOB_NAME);
    }
        if (itrSenderJob == null || itrSenderJob.isEmpty()) {
        throw new Exception("ERROR: ITR_SENDER_JOB_NAME is null or empty!");
    }
        return itrSenderJob;
}

    /**
     * Get the client Job Name Build ID which sent ITR to ILES CI
     * @param eITR
     * @return
     */
    private String getItrSenderJobBuild(ITREvent eITR) {
        if (eITR.getParameters().containsKey(ITR_SENDER_JOB_BUILD)) {
            return eITR.getParameters().get(ITR_SENDER_JOB_BUILD);
        } else {
            println("WARNING: Couldn't get ITR_SENDER_JOB_BUILD!");
            return null;
        }
    }

    /**
     * Get the client Jenkins instance that is communicating with ILES CI
     * @param eITR
     * @return
     */
    private String getClientJenkins(ITREvent eITR) {
        if (eITR.getParameters().containsKey(CLIENT_JENKINS)) {
            return eITR.getParameters().get(CLIENT_JENKINS);
        } else {
            println("WARNING: Couldn't get CLIENT_JENKINS!");
            return null;
        }
    }

    /**
     * Get the full link of client Jenkins which sent ITR to ILES CI
     * @param eITR
     * @return
     * @throws Exception
     */
    private String getFullClientJenkinsLink (ITREvent eITR) throws Exception{
        StringBuilder clientJenkinsFullLink = new StringBuilder();
        String clientJenkins = getClientJenkins(eITR);
        String itrSenderJobName = getItrSenderJobName(eITR);
        String itrSenderJobBuild = getItrSenderJobBuild(eITR);

        if (clientJenkins == null || clientJenkins.isEmpty() || itrSenderJobBuild == null || itrSenderJobBuild.isEmpty()) {
            println("WARNING: Couldn't get the full link of client Jenkins!");
            clientJenkinsFullLink.append(clientJenkins).append(" | ").append(itrSenderJobName).append(" | ").append(itrSenderJobBuild);
        }
        else {
            clientJenkinsFullLink.append(clientJenkins).append("job/").append(itrSenderJobName).append("/").append(itrSenderJobBuild).append("/console");
        }
        return clientJenkinsFullLink.toString();
    }
    /** {@inheritDoc} */
    @Override
    public boolean canBeHandled(Event e) {
        return e instanceof ITREvent;
    }

    /** {@inheritDoc} */
    @Override
    public EventHandlingResult handle(Event e) throws EventHandlingException {
        ITREvent eITR = (ITREvent) e;
        List<Component> components = new ArrayList<>();

        try {
            println("\n--> Handling ITR event from " + getFullClientJenkinsLink(eITR));
            println("\n\tEvent ID: " + eITR.getID());
            NwftParametersAction buildParams = new NwftParametersAction();
            buildParams.addParam(new EventParamenterValue(eITR));
            // Add the QUICK_TEST parameter to NwftParameter
            if (eITR.getParameters().containsKey(QUICK_TEST)) {
                buildParams.addParam(new CommonParamenterValue(QUICK_TEST, eITR.getParameters().get(QUICK_TEST)));
            }

            if (isBaseLineType(eITR)) {
                components = prepareForBaselineJob(eITR);
            }
            else if (isTestType(eITR)) {
                components = prepareForTestJob(eITR);

                // add extra condition to build params
                buildParams.addParam(getManualRunParams(eITR));
                buildParams.addParam(getCustomToolPathParams(eITR));

                //get and add the config.properties path to NwftParametersAction
                if (!eITR.getParameters().containsKey(TESTEXEC_ABLE)
                        || eITR.getParameters().get(TESTEXEC_ABLE).equalsIgnoreCase("true")) {
                    buildParams.addParam(getConfigPropertiesPath(eITR));
                }
            }
            else {
                throw new Exception("Unknown loop type");
            }

            println("\n\tList of Components will be used for testing: ");
            components.forEach(component -> {
                println("\t" + IlesComponentFactory.getInstance().toJson(component));
                buildParams.addParam(new ComponentParameterValue(component.getType(), component));
            });

            // Get vise channel from event or pool and check to see if any free vise
            buildParams.addParam(getViseChannel(eITR));

            // Start run loop for the ITR event
            return  runLoop(components, buildParams, eITR);
        } catch (BaselineVotingException blex) {
            //return BTF with type WAITING
            createFeedBackBTFEvent(eITR, PhaseStatus.QUEUED, Phase.VERIFICATION, BTFEvent.BtfType.WAITING, components, "The ITR event is queued, reason: Baseline Voting In Progress", getHandlerJobUrl());
            return EventHandlingResult.createRetryLaterResult(blex.getMessage(), null);
        } catch (ViseChannelException vex) {
            //return BTF with type WAITING
            createFeedBackBTFEvent(eITR, PhaseStatus.QUEUED, Phase.VERIFICATION, BTFEvent.BtfType.WAITING, components, "The ITR event is queued, reason: No Vise Channels Available", getHandlerJobUrl());
            return EventHandlingResult.createRetryLaterResult(vex.getMessage(), null);
        } catch (Exception ex) {
            //return BTF with type REJECT
            createFeedBackBTFEvent(eITR, PhaseStatus.FAILURE, Phase.VERIFICATION, BTFEvent.BtfType.REJECT, components, "The ITR event is rejected, reason: " + ex.getMessage(), getHandlerJobUrl());
            return EventHandlingResult.createFailedResult(ex.getMessage(), null);
        }
    }

    /**
     * Check to see if there is Baseline loop running or any voting in progress
     */
    private void checkLoopRunningOrVoting() throws Exception {
        IlesImsBaseline processingBaseline = IlesGlobalConfig.getInstance().getProcessingBaseline();

        if (processingBaseline.isLoopRunning()) {
            // ILES can't handle this ITR at this time, schedule ITR for next execution.
            throw new BaselineVotingException("Can't start MNBL, there is a Baseline Loop running, " +
                    "saved this ITR event to scheduled events queue in DB.");
        }
    }

    /**
     * Process the test loop.
     * @param params the needed Parameter action
     * @return the process result
     */
    private EventHandlingResult runLoop(List<Component> components, NwftParametersAction params, ITREvent eITR) {
        try {
            // Trigger workerJobName start running Baseline Loop on reserved VISE channel
            setLoopRunning(components, eITR, true);
            List<Action> actions = new LinkedList<>();
            actions.add(params);

            NwftDownstreamJob job = new NwftDownstreamJob(this.workerJobName, actions, new Cause.UpstreamCause((Run<?, ?>)build), 0, 0);
            job.schedule();
            // Check if WORKER is running and send BTF back, type = STARTLOOP
            job.waitForStart();
            String buildLogUrl = job.getFullBuildLink();

            println("\n\tRunning loop on " + job.getBuildLink(null));

            if (eITR.isEventFromQueue()) {
                createFeedBackBTFEvent(eITR, PhaseStatus.PROGRESS, Phase.PROCESSING, BTFEvent.BtfType.RESTART, components, "The ITR event is re-handling", buildLogUrl);
            }
            else {
                createFeedBackBTFEvent(eITR, PhaseStatus.PROGRESS, Phase.PROCESSING, BTFEvent.BtfType.STARTLOOP, components, "The ITR event is handling", buildLogUrl);
            }
        } catch (Exception ex) {
            setLoopRunning(components, eITR, false);
            return EventHandlingResult.createRetryLaterResult(ex.getMessage(), ex);
        }

        return EventHandlingResult.createSuccessfulResult();
    }

    /**
     * This function will set loopRunning parameter value
     */
    private void setLoopRunning(List<Component> components, ITREvent requestEvent, boolean isLoopRunning) {
        // only when baseline we set loop running
        if(isBaseLineType(requestEvent)) {
            IlesImsBaseline processBaseline = new IlesImsBaseline(components, requestEvent);
            processBaseline.setLoopRunning(isLoopRunning);
            IlesGlobalConfig.getInstance().setProcessingBaseline(processBaseline);
        }
    }

    /** {@inheritDoc} */
    @Override
    public EventValidationResult validateEvent(Event e) {
        // TODO: refactor follow https://jirapducc.mo.ca.am.ericsson.se/browse/IMSCIENGIN-461
        ITREvent eITR = (ITREvent) e;
        EventValidationResult result = new EventValidationResult();
        List<Component> comps = new ArrayList<>();
        comps.add(IlesComponentFactory.getInstance().getComponentFromEvent(eITR));

        try {
            // validate Event
            result = super.validateEvent(eITR);
            if(result.isSuccessful()) {
                // Validate the product type.
                String productType = eITR.getProduct();
                 if (!IlesComponentFactory.getInstance().getRegisteredClassNames().stream().anyMatch(p -> p.equalsIgnoreCase(productType))
                        && !ITREvent.STANDALONE_TEST_BASELINE_CONFIG.equalsIgnoreCase(productType)) {
                    result.addError("The product type " + productType + " is not supported.");
                }

                //validate the config.properties file for all scenarios except TESTEXEC_ABLE is False in Standalone env
                if (!(isStandaloneRequest(eITR) && eITR.getParameters().get(TESTEXEC_ABLE).equals("false"))) {
                   if(getConfigPropertiesPath(eITR) == null) {
                        result.addError("The config.properties in ITR is NULL.");
                   }
                }

                // Check if is MNBL
                if (eITR.getLoopType() == ITREvent.LoopType.BASELINE) {
                    // TODO: find better way to get component, refactor/replace loadNewestComponent() if needed
                    // Find the current Approved Baseline in DB for the new Node
                    Component nb = compDao.loadNewestComponent(eITR.getProduct(), Component.State.BASELINE_APPROVED);
                    // Compare new Node version with the one exist in DB
                    if (nb != null) {
                        int compareResult = getVersionHelper().compareIlesComponentVersions(eITR.getBaseline(), nb.getVersion(), nb.getType());

                        if (compareResult < 0) {
                            result.addError("ITR value's version is older than the one existing in database.");
                        } else if (compareResult == 0) {
                            result.addError("ITR value's version is the same as the one existing in database.");
                        }
                    }
                }
            }
        } catch (Exception ex) {
            result.addError(ex.getMessage());
        } finally {
            if (!result.isSuccessful()) {
                StringBuilder msg = new StringBuilder();
                if (result.getErrors().size() == 1)
                    msg.append(result.getErrors().get(0));
                else if (result.getErrors().size() > 0)
                    msg.append("Validation errors:" + String.join(",", result.getErrors()));

                if (result.getComments().size() == 1)
                    msg.append(result.getComments().get(0));
                else if (result.getComments().size() > 0)
                    msg.append("Validation comments:" + String.join(",", result.getComments()));

                //return BTF to client in case of invalid ITR
                createFeedBackBTFEvent(eITR, PhaseStatus.FAILURE, Phase.VERIFICATION, BTFEvent.BtfType.REJECT, comps, "The ITR event is rejected, reason: " + msg.toString(), getHandlerJobUrl());
            }
        }
        return result;
    }

    /**
     * Get Tag for sending feedback.
     */
    private String getTagFromITREvent(ITREvent event) {
        if (event.getParameters().containsKey(EVENT_TAG)) {
            // the TAG was defined in ITREvent
            return event.getParameters().get(EVENT_TAG);
        } else {
            return event.getProduct().toLowerCase(); // default: TAG = product
        }
    }

    /**
     * Create a feedback event to inform the current status of ITR event handling
     * @param eITR - ITR event
     * @param status - Status of handling iTR event
     * @param phase - Current processing phase
     * @param type - Type of the BTF event
     * @param msg - Message to inform
     * @param buildLogUrl - build url
     */
    public void createFeedBackBTFEvent(ITREvent eITR, PhaseStatus status, Phase phase, BTFEvent.BtfType type, List<Component> components, String msg, String buildLogUrl) {
        this.getHandler().getLog().println("\n\tCreating BTF event with type = " + type);
        BTFEvent eBTF = new BTFEvent();
        List<String> products = new ArrayList<>();
        List<String> baselines = new ArrayList<>();

        if (eITR.getParameters().containsKey(INSTALLATION_ABLE) &&
                eITR.getParameters().get(INSTALLATION_ABLE).equalsIgnoreCase("false")) {
            products.add(ITREvent.STANDALONE_TEST_BASELINE_CONFIG);
            baselines.add(ITREvent.STANDALONE_TEST_BASELINE_CONFIG);

        }
        else {
            for (Component c : components) {
                if (c != null) {
                    products.add(c.getType());
                    baselines.add(c.getVersion());
                }
            }
        }
        // Setting BTF event
        eBTF.setProducts(products);
        eBTF.setBaselines(baselines);
        eBTF.setBuildId(eITR.getBuildId());
        eBTF.setPhase(phase);
        eBTF.setPhaseStatus(status);
        eBTF.setBtfId(eITR.getID());  //get the ID from ITR
        eBTF.setBtfType(type);
        eBTF.setMessage(msg);
        eBTF.setBuildLogUrl(buildLogUrl);
        eBTF.setViseChannel(null);
        eBTF.setTestScores(null);
        eBTF.setResults(null);
        eBTF.setJobId(0);   //TODO: update LEO
        eBTF.setEventFromQueue(eITR.isEventFromQueue());
        if (!isStandaloneRequest(eITR)) {
            eBTF.setComponentDetails(eITR.getProduct().toUpperCase() + ": " + eITR.getBaseline());
        }
        try {
            eBTF.setRequester(getItrSenderJobName(eITR));
        } catch (Exception e) {
            println("\tAn exception occurred when getting the ITR_SENDER_JOB_NAME");
        }

        // Add this BTF to EiffelEventParameterValue_ToSend
        EiffelEventParameterValue_ToSend feedbackEvent = new EiffelEventParameterValue_ToSend("BTF for ITR verification phase", eBTF);
        feedbackEvent.setTag(getTagFromITREvent(eITR));
        this.getHandler().addFeedbackParam(feedbackEvent);
    }

    private IlesVersionHelper getVersionHelper() {
        return IlesVersionHelper.getInstance();
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "ILES: ITR Event";
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return "Implementation of ITR Event Strategy\nWorker Name: " + workerJobName + "\nVISE Pool Name: " + poolName;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ITR Event Handling Strategy Running Loop: '" + workerJobName + "'";
    }
}
