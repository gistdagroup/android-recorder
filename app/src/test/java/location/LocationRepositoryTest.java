package location;

import com.wowza.gocoder.sdk.sampleapp.location.LocationModel;
import com.wowza.gocoder.sdk.sampleapp.location.LocationRepository;
import com.wowza.gocoder.sdk.sampleapp.location.LocationService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import static junit.framework.Assert.fail;
import static org.mockito.Mockito.mock;

public class LocationRepositoryTest {

    MockRetrofit mockRetrofit;
    Retrofit retrofit;

    @Before
    public void setUp() throws Exception {

        NetworkBehavior behavior = NetworkBehavior.create();

        retrofit = new Retrofit.Builder().baseUrl("http://a.com")
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mockRetrofit = new MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build();

    }

    @Test
    public void testLocationRepositoryCallLocationServiceOnceTimes() throws Exception {

        BehaviorDelegate<LocationService> delegate = mockRetrofit.create(LocationService.class);

        LocationService service = new MockLocationService(delegate);
        LocationModel locationModel = new LocationModel();
        LocationRepository repository = new LocationRepository();
        repository.setLocationService(service);

        repository.postLocation(locationModel);

        verify_LocationServiceShouldCallOnceTimes(service);

    }

    private void verify_LocationServiceShouldCallOnceTimes(LocationService service) {
        if (((MockLocationService) service).times != 1) {
            fail("update() should call at least once times");
        }
    }

    @After
    public void tearDown() throws Exception {
        mockRetrofit = null;
    }
}

class MockLocationService implements LocationService {

    private BehaviorDelegate<LocationService> delegate;

    public int times = 0;

    public MockLocationService(BehaviorDelegate<LocationService> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call<LocationModel> update(@Body RequestBody body) {

        times++;

        return delegate.returningResponse(mock(LocationModel.class)).update(null);

    }
}
