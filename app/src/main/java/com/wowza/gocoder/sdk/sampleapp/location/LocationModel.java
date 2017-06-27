package com.wowza.gocoder.sdk.sampleapp.location;

import android.content.Context;

import com.wowza.gocoder.sdk.sampleapp.Utility;

import java.sql.DatabaseMetaData;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class LocationModel {

    public String type;
    public String date;
    public String hdop;
    public Coord coord;
    public String uuid;

    public class Coord {

        public String lat;
        public String lng;

        public Coord(String lat, String lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }

    public LocationModel() {}

    public LocationModel(Context context, String lat, String lng) {

        this.type = "ANDROID";
        this.hdop = "0";
        this.date = Utility.getDateTimeUTC(new Date());
        this.coord = new Coord(lat, lng);
        this.uuid = Utility.getUUID(context);

    }
}


