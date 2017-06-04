package com.wowza.gocoder.sdk.sampleapp.location;

import android.util.Log;

import com.google.gson.Gson;
import com.wowza.gocoder.sdk.sampleapp.ApiAdapter;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationRepository {

    private LocationService locationService;

    public LocationRepository() {
        this.locationService = ApiAdapter.createService(LocationService.class);
    }

    public void postLocation(LocationModel locationModel) {

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(locationModel));

        locationService.update(body).enqueue(new Callback<LocationModel>() {

            @Override
            public void onResponse(Call<LocationModel> call, Response<LocationModel> response) {

                try {

                    Log.d(LocationRepository.class.getSimpleName(), response.message());
                    Log.d(LocationRepository.class.getSimpleName(), new Gson().toJson(response.body()));

                }catch (Exception ex) {

                    ex.printStackTrace();

                }
            }

            @Override
            public void onFailure(Call<LocationModel> call, Throwable t) {

            }

        });

    }
}
