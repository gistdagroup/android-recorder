package location;

import com.google.gson.Gson;
import com.wowza.gocoder.sdk.sampleapp.location.LocationModel;
import com.wowza.gocoder.sdk.sampleapp.location.LocationService;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.mock.BehaviorDelegate;

import static org.mockito.Matchers.any;

public class MockLocationService implements LocationService {

    private BehaviorDelegate<LocationService> delegate;

    private String mockResponse() {
        return "{\"coord\":{\"lat\":\"13.77916383\",\"lng\":\"100.54635601\"},\"date\":\"2017-06-01T16:40:14.540Z\",\"hdop\":\"0\",\"type\":\"ANDROID\",\"uuid\":\"f0ddaf005b460c\"}";
    }

    public int times = 0;

    public MockLocationService(BehaviorDelegate<LocationService> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call<LocationModel> update(@Body RequestBody body) {

        times++;

        return delegate.returningResponse(new Gson().fromJson(mockResponse(), LocationModel.class)).update(any(RequestBody.class));

    }
}
