package com.android_app.matan.ara.sagi.thesocialnotework;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class GmapFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "[TSN / GmapFragment]";
    private GoogleMap mMap;
    private GPSUtils gpsUtils;
    private MainActivity mainActivity;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public GmapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GmapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GmapFragment newInstance(String param1, String param2) {
        GmapFragment fragment = new GmapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        VolleyUtilSingleton.getInstance(getActivity()).get(mainActivity.BASE_URL + "/note/all?uid=" + mainActivity.getUserId(), getNotesSuccessListener, mainActivity.genericErrorListener);

        LatLng userLocation = new LatLng(gpsUtils.getLatitude(), gpsUtils.getLongitude());
        mMap.addMarker(new MarkerOptions().position(userLocation).title("I Am Here!"));
        if (ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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

                    listOfNotes.add(mainActivity.getNoteFromJsonObj(noteObject, time));
                }
                addNotesToMap(listOfNotes);
//                noteList.setAdapter(noteListAdapter);
            } catch (Exception e) {
                Log.e(TAG, "newNoteSuccess:" + e.getMessage());
            }
        }
    };

    private void addNotesToMap(List<Note> listOfNotes) {
        for(Note note : listOfNotes){
            String title = note.getTitle();
            float lat = note.getLat();
            float lng = note.getLon();
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(title));
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

}
