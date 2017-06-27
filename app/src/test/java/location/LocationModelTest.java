package location;

import com.google.gson.Gson;
import com.wowza.gocoder.sdk.sampleapp.location.LocationModel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LocationModelTest {

    @Test
    public void testParseResponse() throws Exception {

        String response = "{\"coord\":{\"lat\":\"13.77916383\",\"lng\":\"100.54635601\"},\"date\":\"2017-06-01T16:40:14.540Z\",\"hdop\":\"0\",\"type\":\"ANDROID\",\"uuid\":\"f0ddaf005b460c\"}";

        LocationModel locationModel = new Gson().fromJson(response, LocationModel.class);

        assertEquals("ANDROID", locationModel.type);
    }
}
