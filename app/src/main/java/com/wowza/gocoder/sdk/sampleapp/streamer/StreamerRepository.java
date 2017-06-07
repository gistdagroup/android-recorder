package com.wowza.gocoder.sdk.sampleapp.streamer;

import com.wowza.gocoder.sdk.sampleapp.ApiAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StreamerRepository {

    private static final String STREAMER_ACTION_START = "startRecording";
    private static final String STREAMER_ACTION_STOP = "stopRecording";

    private StreamerService streamerService;

    public StreamerRepository() {
        this.streamerService = ApiAdapter.createService(StreamerService.class, ApiAdapter.HOST + ":8086/");
    }

    public void start(String filename, String streamName) {

        streamerService.record(STREAMER_ACTION_START, filename, streamName).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    public void stop(String filename, String streamName) {

        streamerService.record(STREAMER_ACTION_STOP, filename, streamName).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }

        });

    }
}
