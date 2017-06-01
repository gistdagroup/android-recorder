package com.wowza.gocoder.sdk.sampleapp.location;

import android.content.Context;

import com.wowza.gocoder.sdk.sampleapp.Utility;

import java.sql.Timestamp;

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

    public LocationModel(Context context, String lat, String lng) {

        this.type = "ANDROID";
        this.hdop = "0";
        this.date = new Timestamp(System.currentTimeMillis()).toString();
        this.coord = new Coord(lat, lng);
        this.uuid = Utility.getUUID(context);

    }
}


