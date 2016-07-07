package com.android_app.matan.ara.sagi.thesocialnotework;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;


public class GmapFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "[TSN / GmapFragment]";
    private GoogleMap mMap;
    private GPSUtils gpsUtils;
    private MainActivity mainActivity;
    private final int MAX_ZOOM = 16, MIN_ZOOM = 9;


    public GmapFragment() {}


    public static GmapFragment newInstance(String param1, String param2) {
        GmapFragment fragment = new GmapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public GoogleMap getMap() {
        return mMap;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();

        gpsUtils = mainActivity.getGPSUtils();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gmap, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment frag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        frag.getMapAsync(this);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (cameraPosition.zoom > MAX_ZOOM) {
                    getMap().animateCamera(CameraUpdateFactory.zoomTo(MAX_ZOOM));
                }
                if (cameraPosition.zoom < MIN_ZOOM) {
                    getMap().animateCamera(CameraUpdateFactory.zoomTo(MIN_ZOOM));
                }

            }
        });

        VolleyUtilSingleton.getInstance(getActivity()).get(Utils.BASE_URL + "/note/all?uid=" + mainActivity.getUserId(), getNotesSuccessListener, Utils.genericErrorListener);
//        VolleyUtilSingleton.getInstance(getActivity()).get(mainActivity.BASE_URL + "/note/all?uid=" + mainActivity.getUserId(), getNotesSuccessListener, mainActivity.genericErrorListener);
        LatLng userLocation = new LatLng(gpsUtils.getLatitude(), gpsUtils.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(userLocation).title("I Am Here!"));
        if (ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));

    }

    //response listener for getting all user notes
    Response.Listener<JSONObject> getNotesSuccessListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d(TAG, "getNotesSuccessListener: " + response.toString());
            List<Note> listOfNotes = new ArrayList<>();

            try {
                //need to get all notes and add to listOfNotes
                JSONArray noteObjectsArray = response.getJSONArray("notes");
                Date time = new Date();
                for (int i = 0; i < noteObjectsArray.length(); i++) {
                    JSONObject noteObject = noteObjectsArray.getJSONObject(i);
                    time.setTime(noteObject.getLong("created_at"));
                    listOfNotes.add(Utils.getNoteFromJsonObj(noteObject, time));
                }
                new getMarkersFromNotes(mMap).execute(listOfNotes);
//                noteList.setAdapter(noteListAdapter);
            } catch (Exception e) {
                Log.e(TAG, "newNoteSuccess:" + e.getMessage());
                e.printStackTrace();
            }
        }
    };






    private class getMarkersFromNotes extends AsyncTask<List<Note>, MarkerOptions, List<MarkerOptions>> {
        GoogleMap mMap;
//        GmapFragment gmap;

        public getMarkersFromNotes(GoogleMap map) {
            mMap = map;
//            gmap = GmapFragment.
//            mMap = GmapFragment.getMap();
            Log.d(TAG, "in async ctor");
        }

        @Override
        protected void onProgressUpdate(MarkerOptions... mo) {
            mMap.addMarker(mo[0]);
        }

        @Override
        protected void onPostExecute(List<MarkerOptions> markerOptionList) {
            for (MarkerOptions mo : markerOptionList) {
                mMap.addMarker(mo);
            }
            Log.d(TAG, "in async post");

        }

        @Override
        protected List<MarkerOptions> doInBackground(List<Note>... listOfNotes) {
            Log.d(TAG, "in async BG");

            String url = "http://www.aljazeera.com/mritems/images/site/DefaultAvatar.jpg";
            List<MarkerOptions> markerOptionList = new ArrayList<>();
//            for (int i = 0 ; i< listOfNotes.length; i++)
            for (Note n : listOfNotes[0]) {
//                markerOptionList.add(
                MarkerOptions mo = new MarkerOptions()
                        .title(n.getTitle())
                        .position(new LatLng(n.getLat(), n.getLon()))
                        .snippet(n.getBody())
                        .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(Utils.getBitmapFromURL(url),80,80,false)));
                publishProgress(mo);
//                );

            }
            return markerOptionList;


        }
//Bitmap.createScaledBitmap(myBitmap, 80, 80, false);

    }


}
