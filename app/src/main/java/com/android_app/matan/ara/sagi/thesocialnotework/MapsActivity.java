package com.android_app.matan.ara.sagi.thesocialnotework;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
//
//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
////
////    private GoogleMap mMap;
////    private ArrayList<Note> listOfNotes;
////    private float userLat, userLng;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//        Log.d("Tag","in maps create");
//        listOfNotes = getIntent().getParcelableArrayListExtra("note_list");
//        userLat = getIntent().getFloatExtra("user_lat", -34);
//        userLng = getIntent().getFloatExtra("user_lng", 151);
//        Toast.makeText(this, listOfNotes.get(1).title, Toast.LENGTH_LONG).show();
//    }
//
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        LatLng userLocation = new LatLng(userLat,userLng);
//        LatLng noteLocation = null;
//
//        for (Note note :listOfNotes){
//            noteLocation = new LatLng(note.getLat(),note.getLon());
//            mMap.addMarker(new MarkerOptions().position(noteLocation).title(note.getTitle()));
//        }
//        // Add a marker in Sydney and move the camera
////        LatLng sydney = new LatLng(-34, 151);
////        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.addMarker(new MarkerOptions().position(userLocation).title("I Am Here!"));
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
//    }
//}
