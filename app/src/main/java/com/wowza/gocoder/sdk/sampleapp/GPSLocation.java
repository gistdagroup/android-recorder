package com.wowza.gocoder.sdk.sampleapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class GPSLocation implements LocationListener {

    public Location location;
    protected LocationManager locationManager;
    Activity appContext = null;

    public static final int REQUEST_PERMISSION_CODE = 110;

    public static  String getUtmText() {
        return utmText;
    }

    public void setUtmText(String utmText) {
        this.utmText = utmText;
    }

    public static String utmText;

    private UpdateGPSListener listener;

    public void setUpdateGPSListener(UpdateGPSListener listener) {
        this.listener = listener;
    }

    public GPSLocation(Activity context) {
        appContext = context;

        locationManager = (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public Location getLocation(String s) {
        if (locationManager.isProviderEnabled(s)) {
            if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(appContext
                        , new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                                , Manifest.permission.ACCESS_COARSE_LOCATION}
                        , REQUEST_PERMISSION_CODE);
                return null;

            }

            locationManager.requestLocationUpdates(s, 1000, 0, this);
        }

        if(null != location){
            location = locationManager.getLastKnownLocation(s);
            return location;
        }

        return null;
    }
    @Override
    public void onLocationChanged(Location location) {
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();

        if (listener != null)
            listener.updateLocation(""+latitude+"", ""+longitude+"");

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(appContext, "Gps Enabled",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(appContext, "Gps Disabled",Toast.LENGTH_SHORT ).show();
    }
}
