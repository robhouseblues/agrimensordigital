package br.edu.utfpr.agrimensordigital.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import static android.content.Context.*;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;

public class GPSUtil implements LocationListener {

    private final Context context;

    private Location location;
    private Double latitude;
    private Double longitude;

    public GPSUtil(Context context) {
        this.context = context;
        getLocation();
    }

    @SuppressLint("MissingPermission")
    private Location getLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }


        if (location == null) {
            locationManager.requestLocationUpdates(NETWORK_PROVIDER, 2000, 0, this);

            location = locationManager.getLastKnownLocation(NETWORK_PROVIDER);

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }

        return location;
    }

    public Double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    public Double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
