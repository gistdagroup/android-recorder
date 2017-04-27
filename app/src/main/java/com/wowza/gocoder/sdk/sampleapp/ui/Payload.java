package com.wowza.gocoder.sdk.sampleapp.ui;

import android.content.Context;

import com.wowza.gocoder.sdk.sampleapp.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Payload {

    public static class Builder {

        Context context;

        public Builder(Context context) {
            this.context = context;
        }

        String payload = "$%0|%1|2A|0|0|1|%2|%3|||";
        String lat = "";
        String lng = "";

        public Builder setLat(String lat) {
            this.lat = lat;
            return this;
        }

        public Builder setLng(String lng) {
            this.lng = lng;
            return this;
        }

        public String build() {

            String imei = Utility.getUUID(context);
            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyHHmmss.00");

            return payload.replace("%0", imei)
                    .replace("%1", dateFormat.format(new Date()))
                    .replace("%2", lat)
                    .replace("%3", lng);

        }

    }
}
