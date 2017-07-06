package com.ericsson.becrux.iles.leo;

/**
 * Created by thien.d.vu on 1/12/2017.
 */
public class LeoTests {

//    private String l = "http://esekiws5263.rnd.ki.sw.ericsson.se:8084";
//
//    @Test
//    public void testLeos () {
//        // Step 1
//        String leoLocation = initLeo(l);
//        // Step 2
//        Job jobInit = initLeoJob();
//        // Step 3
//        Job job = createJob(leoLocation, jobInit);
//        UnitResponse rootResponse = createRoot(leoLocation, job);
//        // Step 4
//        UnitRequest unitRequest = initUnitRequest (rootResponse, job, "INSTALLATION", "", UnitType.INSTALLATION, StatusType.STARTED, true, false);
//        UnitResponse installResponse = createUnit(leoLocation, unitRequest);
//        // Step 5
//        unitRequest = initUnitRequest (installResponse, job, "MTAS", "", UnitType.NODE_INSTALLATION, StatusType.STARTED, true, false);
//        UnitResponse mtasResponse = createUnit(leoLocation, unitRequest);
//        unitRequest = initUnitRequest (installResponse, job, "CSCF", "", UnitType.NODE_INSTALLATION, StatusType.STARTED, true, false);
//        UnitResponse cscfResponse = createUnit(leoLocation, unitRequest);
//        unitRequest = initUnitRequest (installResponse, job, "IBCF", "", UnitType.NODE_INSTALLATION, StatusType.STARTED, true, false);
//        UnitResponse ibcfResponse = createUnit(leoLocation, unitRequest);
//        unitRequest = initUnitRequest (installResponse, job, "PCSCF", "", UnitType.NODE_INSTALLATION, StatusType.STARTED, true, false);
//        UnitResponse pcscfResponse = createUnit(leoLocation, unitRequest);
//        // Step 6
//        unitRequest = initUnitRequest (mtasResponse, job, "MTAS", "", UnitType.NODE_INSTALLATION, StatusType.FINISHED, false, true);
//        mtasResponse = updateUnit(leoLocation, job, unitRequest);
//        unitRequest = initUnitRequest (cscfResponse, job, "CSCF", "", UnitType.NODE_INSTALLATION, StatusType.FINISHED, false, true);
//        cscfResponse = updateUnit(leoLocation, job, unitRequest);
//        unitRequest = initUnitRequest (ibcfResponse, job, "IBCF", "", UnitType.NODE_INSTALLATION, StatusType.FINISHED, false, true);
//        ibcfResponse = updateUnit(leoLocation, job, unitRequest);
//        unitRequest = initUnitRequest (pcscfResponse, job, "PCSCF", "", UnitType.NODE_INSTALLATION, StatusType.FINISHED, false, true);
//        pcscfResponse = updateUnit(leoLocation, job, unitRequest);
//        // Step 7
//        unitRequest = initUnitRequest (rootResponse, job, "TESTEXEC", "", UnitType.NWFT, StatusType.STARTED, true, false);
//        UnitResponse testExecResponse = createUnit(leoLocation, unitRequest);
//        // Step 8
//        UnitData unitData = initUnitData(testExecResponse, 10, 10, 8, 2, 0);
//        UnitResponse dataResponse = createUnitData(leoLocation, unitData);
//        // Step 9
//        unitRequest = initUnitRequest (testExecResponse, job, "TESTEXEC", "", UnitType.NWFT, StatusType.FINISHED, false, true);
//        testExecResponse = updateUnit(leoLocation, job, unitRequest);
//        // Step 10
//        List<ProductVersion> productVersions = new ArrayList<>();
//        productVersions.add(generateProductVersion("MTAS","3.0", 2, "mtas", Arrays.asList("xthdidi")));
//        productVersions.add(generateProductVersion("CSCF","3.1", 2, "cscf", Arrays.asList("xthdidi")));
//        productVersions.add(generateProductVersion("IBCF","3.2", 2, "ibcf", Arrays.asList("xthdidi")));
//        productVersions.add(generateProductVersion("P-CSCF","3.3", 2, "pcscf", Arrays.asList("xthdidi")));
//        createBaseline(leoLocation, job, productVersions);
//    }
//
//    public String initLeo (String leoLocation) {
//        return leoLocation;
//    }
//
//    public Job initLeoJob () {
//        // initial Job
//        Job job = new Job();
//        job.ciId = "xthdidi";
//        job.loop = "Legacy Loop";
//        job.productName = "ILES 1.1";
//        job.projectName = "ILES";
//        job.streamName = "ILESCI_development";
//        job.ciStreamName = "ciStreamName";
//        job.teamName = "CI Engine";
//        job.buildLocation = "test Location";
//        job.baseline = "baseline";
//        job.prevReleaseBaseline = "prevReleaseBaseline";
//        job.viewName = "viewName";
//        job.node = 17;
//        job.type = "sv";
//        job.site = 1;
//        job.loopType = "loopType";
//
//        return job;
//    }
//
//    public Job createJob (String leoLocation, Job job) {
//        // create Leo communicate
//        LeoCommunicator com = new LeoCommunicator(leoLocation);
//        return com.createJob(job);
//    }
//
//    public UnitResponse createRoot (String leoLocation, Job job) {
//        // create Leo communicate
//        LeoCommunicator com = new LeoCommunicator(leoLocation);
//        return com.createRootUnit(job);
//    }
//
//    public UnitRequest initUnitRequest (UnitResponse unitResponse, Job job, String identityName, String label,
//                                        UnitType unitType, StatusType statusType, boolean startTime, boolean endTime) {
//        UnitRequest unitRequest = new UnitRequest();
//        if (unitResponse != null) {
//            unitRequest = new UnitRequest(unitResponse);
//        }
//        unitRequest.job = job;
//        unitRequest.identityName = identityName;
//        unitRequest.label = label;
//        if (!endTime) {
//            unitRequest.parent = unitResponse;
//        }
//        unitRequest.unitType = unitType;
//        unitRequest.statusType = statusType;
//        if (startTime) {
//            unitRequest.startTime = LeoCommunicator.nicelyFormattedTimeNow();
//        }
//        if (endTime) {
//            unitRequest.endTime = LeoCommunicator.nicelyFormattedTimeNow();
//        }
//
//        return unitRequest;
//    }
//
//    public UnitResponse createUnit (String leoLocation, UnitRequest unitRequest){
//        LeoCommunicator com = new LeoCommunicator(leoLocation);
//        return com.createUnit(unitRequest);
//    }
//
//    public UnitResponse updateUnit (String leoLocation, Job job, UnitRequest unitRequest) {
//        LeoCommunicator com = new LeoCommunicator(leoLocation);
//        return com.updateUnit(job, unitRequest);
//    }
//
//    public UnitData initUnitData (UnitResponse unitResponse, int total, int executed, int succeeded, int failed, int unitDataErrors){
//        UnitData unitData = new UnitData();
//        unitData.total = total;
//        unitData.executed = executed;
//        unitData.succeeded = succeeded;
//        unitData.failed = failed;
//        unitData.unitDataErrors = unitDataErrors;
//        unitData.unit=unitResponse;
//
//        return unitData;
//    }
//
//    public UnitResponse createUnitData (String leoLocation, UnitData unitData){
//        LeoCommunicator com = new LeoCommunicator(leoLocation);
//        return com.createUnitData(unitData);
//    }
//
//    public ProductVersion generateProductVersion(String productName, String productVersion, long releaseId, String description, List<String> signums){
//        ProductVersion productVersions = new ProductVersion();
//        Product product = new Product();
//        ReleaseType releaseType = new ReleaseType();
//
//        product.name = productName;
//        product.description = description;
//        product.authorizedUsers = signums;
//
//        releaseType.id = releaseId;
//
//        productVersions.name = productVersion;
//        productVersions.product = product;
//        productVersions.releaseType = releaseType;
//
//        return productVersions;
//    }
//
//    public List<ProductVersion> createBaseline (String leoLocation, Job job, List<ProductVersion> productVersions) {
//        LeoCommunicator com = new LeoCommunicator(leoLocation);
//        return com.addInformationBaselines(job, productVersions);
//    }
}