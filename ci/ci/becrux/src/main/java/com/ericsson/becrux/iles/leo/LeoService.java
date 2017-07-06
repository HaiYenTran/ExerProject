package com.ericsson.becrux.iles.leo;

import com.ericsson.becrux.iles.leo.domain.*;
import retrofit.Call;
import retrofit.http.*;

import java.util.List;


public interface LeoService {

    @POST("jobs")
    Call<Job> createJob(@Body Job job);

    @POST("jobs/{jobid}/units")
    Call<UnitResponse> createUnit(@Path("jobid") Long jobId, @Body UnitRequest unit);

    @POST("unitTestCase/batch")
    Call<List<UnitTestCase>> createUnitTestCase(@Query("unitId") Long unitId, @Body List<UnitTestCase> list);

    @PUT("jobs/{jobid}/units/{unitId}")
    Call<UnitResponse> updateUnit(@Path("jobid") Long jobId, @Path("unitId") Long unitId, @Body UnitRequest unit);

    @DELETE("job/{jobid}")
    Call<UnitResponse> deleteUnit(@Path("jobid") Long jobId);

    @POST("unitDatas")
    Call<UnitResponse> createUnitData(@Body UnitData unitData);

    @POST("baselineProduct/batch")
    Call<List<ProductVersion>> addBaselineInformation(@Query("baselineId") long baselineId, @Body List<BaselineHelp> baselineProducts);

    @POST("baselines")
    Call<Baseline> createBaseline(@Body Baseline baseline);

    @GET("jobs/{jobid}/rootUnit")
    Call<UnitResponse> getRootUnit(@Path("jobid") Long jobId);
}
