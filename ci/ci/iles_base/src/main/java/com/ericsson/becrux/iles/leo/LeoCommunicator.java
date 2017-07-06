package com.ericsson.becrux.iles.leo;

import com.ericsson.becrux.base.common.core.NodeGuardian;
import com.ericsson.becrux.iles.leo.domain.*;
import com.ericsson.becrux.base.common.eiffel.events.impl.BTFEvent;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class LeoCommunicator {

    private LeoService service;
    private String leoUrl;

    public LeoCommunicator(String aLeoUrl) {
        leoUrl = aLeoUrl;
        service = initRetrofit();
    }

    private List<String> generateGuardiansSignum(List<NodeGuardian> guardians) {
        List<String> signums = new LinkedList<String>();
        if (guardians != null) {
            for (NodeGuardian g : guardians) {
                signums.add(g.getSignum());
            }
        }
        return signums;
    }

    public Job createJob(Job templateJob) {
        templateJob.ciId  = templateJob.ciId + (new Date().getTime());

        Job createdJob = null;
        try {
            createdJob = service.createJob(templateJob).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return createdJob;
    }

/*	private static String bodyToString(final Request request) {
        Request copy = request.newBuilder().build();
		Buffer buffer = new Buffer();
		try {
			copy.body().writeTo(buffer);
			return buffer.readUtf8();
		} catch (IOException e) {
			return "";
		}
	}*/

    private LeoService initRetrofit() {

        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                //System.out.println(bodyToString(request));
                com.squareup.okhttp.Response response = chain.proceed(request);
                return response;
            }
        });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(leoUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        LeoService service = retrofit.create(LeoService.class);
        return service;
    }

    public UnitResponse createUnit(UnitRequest unit) {

        UnitResponse createdUnit = null;
        try {
            createdUnit = service.createUnit(unit.job.id, unit).execute().body();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return createdUnit;
    }

    public UnitResponse createRootUnit(Job job) {
        //TODO: review all the params! Critically important.
        UnitRequest unit = new UnitRequest();
        unit.job = job;
        unit.parent = null;

        unit.identityName = "CI_TOP";
        //TODO: fill in proper time
        unit.startTime = nicelyFormattedTimeNow();
        unit.endTime = null;
        unit.label = "";

        unit.unitType = UnitType.CI_TOP;

        unit.statusType = StatusType.STARTED;

        UnitResponse createdUnit = null;
        try {
            createdUnit = service.createUnit(job.id, unit).execute().body();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return createdUnit;

    }


    public UnitResponse updateUnit(Job job, UnitRequest unit) {

        UnitResponse updatedUnit = null;
        try {
            updatedUnit = service.updateUnit(job.id, unit.id, unit).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return updatedUnit;
    }

    public static String nicelyFormattedTimeNow() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date now = new Date();
        String reportDate = df.format(now);
        return reportDate;
    }

    public UnitResponse finalizeUnit(Job job, UnitRequest aUnit, StatusType status) {

        //TODO: review all the params! Critically important.
        aUnit.job = job;

        //TODO: fill in proper time
        aUnit.startTime = null;
        aUnit.endTime = nicelyFormattedTimeNow();

        aUnit.statusType = status;

        return updateUnit(job, aUnit);
    }

    public UnitResponse createUnitData(UnitData unitData) {

        UnitResponse createdUnit = null;
        try {
            createdUnit = service.createUnitData(unitData).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return createdUnit;

    }

    public List<UnitTestCase> createUnitTestCase(List<UnitTestCase> list, Long unitid) {

        List<UnitTestCase> returnedList = new ArrayList<>();
        try {
            returnedList = service.createUnitTestCase(unitid, list).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnedList;

    }

    public UnitResponse finalizeTestExecUnit(Job job, UnitRequest testExecUnit, StatusType status) {

        //TODO: review all the params! Critically important.
        testExecUnit.job = job;

        //TODO: fill in proper time
        testExecUnit.startTime = null;
        testExecUnit.endTime = nicelyFormattedTimeNow();

        testExecUnit.statusType = status;

        return updateUnit(job, testExecUnit);
    }

    public void addInformationBaselines(Job job, BTFEvent btf, List<NodeGuardian> guardians) {
        List<ProductVersion> products = new ArrayList<ProductVersion>();
        for (String type : btf.getProducts()) {
            String version = btf.getBaselines().get(btf.getProducts().indexOf(type)).getVersion();
            ProductVersion product = new ProductVersion();
            product.name = version;
            ReleaseType rt = new ReleaseType();
            rt.id = 2;
            product.releaseType = rt;
            Product p = new Product();
            p.description = btf.getMessage();
            p.name = type;
            p.productFamily = ProductFamily.OTHER;
            p.authorizedUsers = generateGuardiansSignum(guardians);
            product.product = p;
            products.add(product);
        }
        addInformationBaselines(job, products);
    }

    public List<ProductVersion> addInformationBaselines(Job job, List<ProductVersion> products) {
        Baseline baseline = createBaseline(job);

        try {
//			System.out.println(JSONObject.fromObject(products));
            List<BaselineHelp> bh = new ArrayList<BaselineHelp>();
            for (ProductVersion p : products) {
                bh.add(new BaselineHelp(p));
            }
            return service.addBaselineInformation(baseline.id, bh).execute().body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public Baseline createBaseline(Job job) {
        Baseline baseline = new Baseline();
        baseline.jobId = job.id;
        try {
            Baseline body = service.createBaseline(baseline).execute().body();
            baseline = body;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baseline;
    }

    public void startVoting(Job job) {

    }

    public void reportTimeout(Long jobId) {

    }

    public UnitResponse getRootUnit(Job job){
        try {
            return service.getRootUnit(job.id).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
