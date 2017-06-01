package com.wowza.gocoder.sdk.sampleapp.location;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LocationService {

    @POST("locations")
    Call<LocationModel> update(@Body RequestBody body);
}
