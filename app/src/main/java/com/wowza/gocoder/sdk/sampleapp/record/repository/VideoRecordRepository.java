package com.wowza.gocoder.sdk.sampleapp.record.repository;

import android.util.Log;

import com.google.gson.Gson;
import com.wowza.gocoder.sdk.sampleapp.ApiAdapter;
import com.wowza.gocoder.sdk.sampleapp.record.viewmodel.VideoRecordModel;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoRecordRepository {

    private VideoRecordService videoRecordService;
    private IVideoRepositoryCallback callback;

    public VideoRecordRepository(IVideoRepositoryCallback callback) {
        this.callback = callback;
        this.videoRecordService = ApiAdapter.createService(VideoRecordService.class);
    }

    public void startRecord(VideoRecordModel videoRecordModel) {

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(videoRecordModel));

        videoRecordService.startRecord(body).enqueue(new Callback<VideoRecordModel>() {

            @Override
            public void onResponse(Call<VideoRecordModel> call, Response<VideoRecordModel> response) {

                try {

                    Log.d(VideoRecordRepository.class.getSimpleName(), response.message());
                    Log.d(VideoRecordRepository.class.getSimpleName(), new Gson().toJson(response.body()));

                    callback.recordStarted(response.body());

                }catch (Exception ex) {

                    ex.printStackTrace();

                }
            }

            @Override
            public void onFailure(Call<VideoRecordModel> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    public void stopRecord(VideoRecordModel videoRecordModel) {

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(videoRecordModel));

        videoRecordService.saveRecord(videoRecordModel.id, body).enqueue(new Callback<VideoRecordModel>() {

            @Override
            public void onResponse(Call<VideoRecordModel> call, Response<VideoRecordModel> response) {

                try {

                    Log.d(VideoRecordRepository.class.getSimpleName(), response.message());
                    Log.d(VideoRecordRepository.class.getSimpleName(), new Gson().toJson(response.body()));

                }catch (Exception ex) {

                    ex.printStackTrace();

                }
            }

            @Override
            public void onFailure(Call<VideoRecordModel> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }
}
