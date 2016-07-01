package com.android_app.matan.ara.sagi.thesocialnotework;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by JERLocal on 7/1/2016.
 */
public class GPSUtils extends Service implements LocationListener {

    private static final String TAG ="GPSUtils" ;
    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    protected LocationManager locationManager;


    public GPSUtils(Context context) {
        this.mContext = context;
        getLocation();
    }

//    public Location getLocation() {
//        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        if (locationManager != null) {
//            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if (lastKnownLocationGPS != null) {
//                return lastKnownLocationGPS;
//            } else {
//                Location loc =  locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
//                System.out.println("1::"+loc);----getting null over here
//                System.out.println("2::"+loc.getLatitude());
//                return loc;
//            }
//        } else {
//            return null;
//        }
    public Location getLocation() {
            try {
                Log.d("TAG","in get location");
                locationManager =
                        (LocationManager) mContext
                                .getSystemService(LOCATION_SERVICE);

                // getting GPS status
                isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                showSettingsAlert();
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.e("TAG", "No Permissions");
                        ((PersonalSpaceActivity)mContext).setLocationPermission(false);
                        return null;
                    }else{
                        ((PersonalSpaceActivity)mContext).setLocationPermission(true);
                    }
                    Log.d(TAG, "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {

                        Log.d(TAG, "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
                else{
                    location =  locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public List<Address> getFromLocation(double latitude, double longitude, int maxResults){
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this);
            if (latitude != 0 || longitude != 0) {
                addresses = geocoder.getFromLocation(latitude ,
                        longitude, maxResults);
                return addresses;

            } else {
                Toast.makeText(this, "latitude and longitude are null",
                        Toast.LENGTH_LONG).show();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }else{
            Log.d("Tag", "Got NULL");
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    public Location getLastKnownLocation(){
        if (location == null)
            return getLocation();
        return location;
    }

    public String getAddress() {
        String address="";



        List<Address> listAddress = getFromLocation(getLatitude(),getLongitude(),1);
        for (int i = 0; i <= listAddress.get(0).getMaxAddressLineIndex(); i++) {
            listAddress.get(0).getAddressLine(i);
            address += listAddress.get(0).getAddressLine(i)+" ";
        }
        return address;
    }
}
