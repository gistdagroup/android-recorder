package com.wowza.gocoder.sdk.sampleapp.record.repository;

import com.wowza.gocoder.sdk.sampleapp.record.viewmodel.VideoRecordModel;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface VideoRecordService {

    @POST("videos")
    Call<VideoRecordModel> startRecord(@Body RequestBody body);

    @PATCH("videos/{id}")
    Call<VideoRecordModel> saveRecord(@Path("id") String referenceId, @Body RequestBody body);

}
