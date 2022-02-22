package com.sayurbox.kale.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface KaleApi {

    @GET("v1/featureflag/allocation/{userId}/{featureId}")
    Call<GetAllocateDataResponse> getFeatureAllocation(
            @Path("userId") String userId,
            @Path("featureId") String featureId
    );

}
