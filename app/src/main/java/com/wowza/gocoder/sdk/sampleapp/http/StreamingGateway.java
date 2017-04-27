package com.wowza.gocoder.sdk.sampleapp.http;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StreamingGateway {

    @GET("/livestreamrecord?app=live&streamname=myStream")
    Call<Object> record(@Query("outputFile") String filename, @Query("action") String action);

}
