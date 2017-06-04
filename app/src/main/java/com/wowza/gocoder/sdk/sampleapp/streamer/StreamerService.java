package com.wowza.gocoder.sdk.sampleapp.streamer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StreamerService {

    @GET("/livestreamrecord?app=live&streamname=myStream")
    Call<String> record(@Query("action") String action, @Query("fileTemplate") String template);

}
