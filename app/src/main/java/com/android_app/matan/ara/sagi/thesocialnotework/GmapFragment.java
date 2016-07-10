package com.android_app.matan.ara.sagi.thesocialnotework;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    private final int MAX_ZOOM = 16, MIN_ZOOM = 9, DEFAULT_ZOOM = 12;
    private HashMap<Marker, Note> eventMarkerMap;
    private ImageButton dateFilter;
    private ImageButton locationFilter;
    private ImageButton userFilter;
    private Button map_small_filter;
    private Button map_medium_filter;
    private Button map_large_filter;
    private LinearLayout mapFilters;
    private boolean dateFilterIsVisible = false;
    private boolean locationFilterIsVisible = false;
    private boolean userFilterIsVisible = false;
    private int userFilterSelection;
    private Long dateFilterSelection;
    private float locationFilterSelection;
    List<Note> listOfNotes;

    private final String day = "24 hours";
    private final String week = "Week";
    private final String month = "Month";
    private final String hundredMeters = "100 meters";
    private final String kilometer = "1 Km";
    private final String tenKilometers = "10 Km";
    private final String mine = "Mine";
    private final String others = "Others";
    private final String all = "All";


    public GmapFragment() {
        eventMarkerMap = new HashMap<Marker, Note>();
        dateFilterSelection = Utils.MONTH_MILI;
        userFilterSelection = 3;
        locationFilterSelection = Utils.DISTANCE_LONG;
    }


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
        View view = inflater.inflate(R.layout.fragment_personal, container, false);

        return inflater.inflate(R.layout.fragment_gmap, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment frag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        frag.getMapAsync(this);
        listOfNotes = new ArrayList<>();

        dateFilter = (ImageButton) view.findViewById(R.id.map_date_filter);
        locationFilter = (ImageButton) view.findViewById(R.id.map_location_filter);
        userFilter = (ImageButton) view.findViewById(R.id.map_user_filter);

        map_small_filter = (Button) view.findViewById(R.id.map_small_filter);
        map_medium_filter = (Button) view.findViewById(R.id.map_medium_filter);
        map_large_filter = (Button) view.findViewById(R.id.map_large_filter);

        map_small_filter.setOnClickListener(button1ClickListener);
        map_medium_filter.setOnClickListener(button2ClickListener);
        map_large_filter.setOnClickListener(button3ClickListener);

        mapFilters = (LinearLayout) view.findViewById(R.id.map_filter_options);

        dateFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateFilterIsVisible) {
                    dateFilterIsVisible = false;
                    mapFilters.setVisibility(View.GONE);
                } else {
                    mapFilters.setVisibility(View.VISIBLE);
                    dateFilterIsVisible = true;
                    locationFilterIsVisible = false;
                    userFilterIsVisible = false;

                    // set text button in the right filter string
                    map_small_filter.setText(day);
                    map_medium_filter.setText(week);
                    map_large_filter.setText(month);
                }
                updateShowedNotes();
            }
        });

        locationFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationFilterIsVisible) {
                    locationFilterIsVisible = false;
                    mapFilters.setVisibility(View.GONE);
                } else {
                    mapFilters.setVisibility(View.VISIBLE);
                    locationFilterIsVisible = true;
                    dateFilterIsVisible = false;
                    userFilterIsVisible = false;

                    // set text button in the right filter string
                    map_small_filter.setText(hundredMeters);
                    map_medium_filter.setText(kilometer);
                    map_large_filter.setText(tenKilometers);
                }
                updateShowedNotes();
            }
        });

        userFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userFilterIsVisible) {
                    userFilterIsVisible = false;
                    mapFilters.setVisibility(View.GONE);
                } else {
                    mapFilters.setVisibility(View.VISIBLE);
                    userFilterIsVisible = true;
                    dateFilterIsVisible = false;
                    locationFilterIsVisible = false;

                    // set text button in the right filter string
                    map_small_filter.setText(mine);
                    map_medium_filter.setText(others);
                    map_large_filter.setText(all);
                }
                updateShowedNotes();
            }
        });
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
        mMap.setInfoWindowAdapter(infoWindowAdapter);


        LatLng userLocation = new LatLng(gpsUtils.getLatitude(), gpsUtils.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(userLocation).title("I Am Here!"));
        if (ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, DEFAULT_ZOOM));

        //get my notes
        VolleyUtilSingleton.getInstance(getActivity()).get(Utils.BASE_URL + "/note/all?uid=" + mainActivity.getUserId(), getNotesSuccessListener, Utils.genericErrorListener);

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", mainActivity.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //get other notes
        VolleyUtilSingleton.getInstance(getActivity()).post(Utils.BASE_URL + "/note/getPublic", jsonObj, getNotesSuccessListener, Utils.genericErrorListener);


    }

    GoogleMap.InfoWindowAdapter infoWindowAdapter = new GoogleMap.InfoWindowAdapter() { // Use default InfoWindow frame
        @Override
        public View getInfoWindow(Marker args) {
            return null;
        }

        // Defines the contents of the InfoWindow
        @Override
        public View getInfoContents(Marker args) {
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                public void onInfoWindowClick(final Marker marker) {

                    final Note note = eventMarkerMap.get(marker);
                    final Dialog noteViewDialog = new Dialog(getActivity());

                    noteViewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    noteViewDialog.setContentView(R.layout.note_display_full);

                    boolean isOwner = note.getOwnerId().equals(mainActivity.getUserId());
//                    if (isOwner)
//                        noteViewDialog.setTitle("You wrote...");
//                    else
//                        noteViewDialog.setTitle("Someone wrote...");
                    noteViewDialog.show();


                    //get note_view_full layout elements
                    final TextView title = (TextView) noteViewDialog.findViewById(R.id.ndf_title_textview);
                    final TextView body = (TextView) noteViewDialog.findViewById(R.id.ndf_body_textview);
                    final TextView time = (TextView) noteViewDialog.findViewById(R.id.ndf_time_textview);
                    final TextView date = (TextView) noteViewDialog.findViewById(R.id.ndf_date_textview);
                    final TextView location = (TextView) noteViewDialog.findViewById(R.id.ndf_address_textview);
                    final TextView likes = (TextView) noteViewDialog.findViewById(R.id.ndf_likes_textview);
//                    final TextView tags = (TextView) noteViewDialog.findViewById(R.id.ndf_tags_textview);
                    final TextView permission = (TextView) noteViewDialog.findViewById(R.id.ndf_permission_textview);
                    final ImageView avatar = (ImageView) noteViewDialog.findViewById(R.id.note_user_avatar);
                    final ImageView permissionImg = (ImageView) noteViewDialog.findViewById(R.id.permission_image);
                    final ImageButton deleteBtn = (ImageButton) noteViewDialog.findViewById(R.id.ndf_delete_imagebutton);


                    title.setText(note.getTitle());
                    body.setText(note.getBody());
                    time.setText(note.getTime());
                    date.setText(note.getDate());

                    location.setText("" + note.getAddress());
                    likes.setText("" + note.getLikes());
//                    tags.setText("Tags: " + note.getTags().toString());
                    Utils.URLtoImageView(avatar, note.getAvatar());
                    if (isOwner) {
                        permission.setText("" + (note.isPublic() ? "Public" : "Private"));
                    } else {
                        permissionImg.setVisibility(View.INVISIBLE);
                        permission.setText("");
                        deleteBtn.setBackgroundResource(R.drawable.like_icon);
                    }


                    if (isOwner) {
                        deleteBtn.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                //Put up the Yes/No message box
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder
                                        .setTitle("Delete Note")
                                        .setMessage("Are you sure you want to delete the note?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Yes button clicked, do something
                                                Toast.makeText(getActivity(), "Item Deleted!",
                                                        Toast.LENGTH_SHORT).show();
                                                //TODO send delete
                                                JSONObject delNote = new JSONObject();
                                                try {
                                                    delNote.put("uid", mainActivity.getUserId());
                                                    delNote.put("nid", note.getId());
                                                    VolleyUtilSingleton.getInstance(getActivity()).post(Utils.BASE_URL + "/note/delete", delNote, Utils.deleteNoteSuccessListener, Utils.genericErrorListener);
//                                                listOfNotes.remove(position);
                                                    marker.remove();

                                                } catch (JSONException e) {
                                                    Toast.makeText(getActivity(), "Something went wrong.\n Failed to delete note...", Toast.LENGTH_LONG).show();
                                                    e.printStackTrace();
                                                }
//                                            noteList.setAdapter(noteListAdapter);
                                                noteViewDialog.dismiss();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Yes button clicked, do something
                                                Toast.makeText(getActivity(), "Canceled",
                                                        Toast.LENGTH_SHORT).show();
                                                noteViewDialog.dismiss();
                                            }
                                        })                        //Do nothing on no
                                        .show();
                            }
                        });


                    } else {
                        //like Btn
                        deleteBtn.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                //add like only if user is didnt like already
                                if (!mainActivity.getUser().getLiked_notes().contains(note.getId())) {

                                    JSONObject jsonObj = new JSONObject();
                                    try {
                                        jsonObj.put("uid", mainActivity.getUserId());
                                        jsonObj.put("nid", note.getId());

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    VolleyUtilSingleton.getInstance(getActivity()).post(Utils.BASE_URL + "/note/like", jsonObj, getNotesSuccessListener, Utils.genericErrorListener);
                                    mainActivity.getUser().getLiked_notes().add(note.getId());
                                    mainActivity.getUser().updateUser(mainActivity);
                                    likes.setText("Likes: " + (note.getLikes() + 1));
                                }
                            }
                        });
                    }
                }

            });

            return null;
        }

    };


    //response listener for getting all user notes
    Response.Listener<JSONObject> getNotesSuccessListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d(TAG, "getNotesSuccessListener: " + response.toString());
//            listOfNotes = new ArrayList<>();

            try {
                //need to get all notes and add to listOfNotes
                JSONArray noteObjectsArray = response.getJSONArray("notes");
                Date time = new Date();
                for (int i = 0; i < noteObjectsArray.length(); i++) {
                    JSONObject noteObject = noteObjectsArray.getJSONObject(i);
                    time.setTime(noteObject.getLong("created_at"));
                    listOfNotes.add(Utils.getNoteFromJsonObj(noteObject, time));
                }
                updateShowedNotes();
//                new getMarkersFromNotes(mMap, eventMarkerMap).execute(listOfNotes);

            } catch (Exception e) {
                Log.e(TAG, "newNoteSuccess:" + e.getMessage());
                e.printStackTrace();
            }
        }
    };


    private class getMarkersFromNotes extends AsyncTask<List<Note>, MarkerNoteStruct, Void> {
        GoogleMap mMap;
        HashMap<Marker, Note> eventMarkerMap;

        public getMarkersFromNotes(GoogleMap map, HashMap<Marker, Note> eventMarkerMap) {
            Log.d(TAG, "in async ctor");
            this.eventMarkerMap = eventMarkerMap;
            mMap = map;
        }

        @Override
        protected void onProgressUpdate(MarkerNoteStruct... mo) {

            eventMarkerMap.put(mMap.addMarker(mo[0].getMarker()), mo[0].getNote());

        }

        @Override
        protected Void doInBackground(List<Note>... listOfNotes) {
            Log.d(TAG, "in async BG");

            BitmapDescriptor b;

            for (Note n : listOfNotes[0]) {
                b = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(Utils.getBitmapFromURL(n.getAvatar()), 80, 80, false));
                MarkerOptions mo = new MarkerOptions()
                        .title(n.getTitle())
                        .position(new LatLng(n.getLat(), n.getLon()))
                        .snippet(n.getBody())
                        .icon(b);

                publishProgress(new MarkerNoteStruct(n, mo));

            }
            return null;


        }

    }

    //set main filter colors
    private void setButtonsColor() {

        Log.d(TAG, "setButtonsColor: start");
        //set date filter colors
        if (dateFilterIsVisible) {
            Log.d(TAG, "setButtonsColor: dateselection :" + dateFilterSelection);
            dateFilter.setBackgroundColor(Utils.filterColor);
            if (dateFilterSelection == Utils.DAY_MILI) {
                map_small_filter.setBackgroundColor(Utils.filterColor);
                map_medium_filter.setBackgroundResource(android.R.drawable.btn_default);
                map_large_filter.setBackgroundResource(android.R.drawable.btn_default);
            } else if (dateFilterSelection == Utils.WEEK_MILI) {
                map_small_filter.setBackgroundResource(android.R.drawable.btn_default);
                map_medium_filter.setBackgroundColor(Utils.filterColor);
                map_large_filter.setBackgroundResource(android.R.drawable.btn_default);
            } else {
                map_small_filter.setBackgroundResource(android.R.drawable.btn_default);
                map_medium_filter.setBackgroundResource(android.R.drawable.btn_default);
                map_large_filter.setBackgroundColor(Utils.filterColor);
            }
        } else {
            dateFilter.setBackgroundResource(android.R.drawable.btn_default);
        }

        //set date filter colors
        if (userFilterIsVisible) {
            Log.d(TAG, "setButtonsColor: userFilter: " + userFilterSelection);
            userFilter.setBackgroundColor(Utils.filterColor);
            if (userFilterSelection == 1) {
                map_small_filter.setBackgroundColor(Utils.filterColor);
                map_medium_filter.setBackgroundResource(android.R.drawable.btn_default);
                map_large_filter.setBackgroundResource(android.R.drawable.btn_default);
            } else if (userFilterSelection == 2) {
                map_small_filter.setBackgroundResource(android.R.drawable.btn_default);
                map_medium_filter.setBackgroundColor(Utils.filterColor);
                map_large_filter.setBackgroundResource(android.R.drawable.btn_default);
            } else {
                map_small_filter.setBackgroundResource(android.R.drawable.btn_default);
                map_medium_filter.setBackgroundResource(android.R.drawable.btn_default);
                map_large_filter.setBackgroundColor(Utils.filterColor);
            }
        } else {
            userFilter.setBackgroundResource(android.R.drawable.btn_default);
        }

        //set distance filter colors
        if (locationFilterIsVisible) {
            Log.d(TAG, "setButtonsColor: userFilter: " + userFilterSelection);
            locationFilter.setBackgroundColor(Utils.filterColor);
            if (locationFilterSelection == Utils.DISTANCE_SMALL) {
                map_small_filter.setBackgroundColor(Utils.filterColor);
                map_medium_filter.setBackgroundResource(android.R.drawable.btn_default);
                map_large_filter.setBackgroundResource(android.R.drawable.btn_default);
            } else if (locationFilterSelection == Utils.DISTANCE_MEDIUM) {
                map_small_filter.setBackgroundResource(android.R.drawable.btn_default);
                map_medium_filter.setBackgroundColor(Utils.filterColor);
                map_large_filter.setBackgroundResource(android.R.drawable.btn_default);
            } else {
                map_small_filter.setBackgroundResource(android.R.drawable.btn_default);
                map_medium_filter.setBackgroundResource(android.R.drawable.btn_default);
                map_large_filter.setBackgroundColor(Utils.filterColor);
            }
        } else {
            locationFilter.setBackgroundResource(android.R.drawable.btn_default);
        }
    }

    public void updateShowedNotes() {
        List<Note> presentedNotes = new ArrayList<>();
        long timeDifference;
        float distance;
        //get current date and location
        Location currLocation = new Location(gpsUtils.getLocation());
        Date now = new Date();
        Location targetLocation = new Location("");//provider name is unecessary
        Date targetDate;

        for (Note note : listOfNotes) {
//            get note location and date
            targetLocation.setLatitude(note.getLat());//your coords of course
            targetLocation.setLongitude(note.getLon());
            targetDate = new Date(note.getTimestamp());
            //get time and date differences
            timeDifference = now.getTime() - targetDate.getTime();
            distance = currLocation.distanceTo(targetLocation);
            //add to currently presented list according to filters.
            if (timeDifference <= dateFilterSelection
                    && distance <= locationFilterSelection
                    && ((note.getOwnerId().equals(mainActivity.getUserId()) && userFilterSelection == 1) || (!note.getOwnerId().equals(mainActivity.getUserId()) && userFilterSelection == 2) || (userFilterSelection == 3))) {
                presentedNotes.add(note);
            }

        }
        Log.d(TAG, "updateShowedNotes: ======= markers presented: "+ presentedNotes.size()+"=============");
//        noteListAdapter.updateList(presentedNotes);
//        noteList.setAdapter(noteListAdapter);
        mMap.clear();
        new getMarkersFromNotes(mMap, eventMarkerMap).execute(listOfNotes);

    }

    //all buttons listener
    public View.OnClickListener button1ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            //user filters
            if (userFilterIsVisible) {
                userFilterSelection = 1;
            }

            //location filter
            else if (locationFilterIsVisible) {
                locationFilterSelection = Utils.DISTANCE_SMALL;
            }

            //date filters
            else {
                dateFilterSelection = Utils.DAY_MILI;

            }
            //change colors of buttons and update visible notes
            setButtonsColor();
            updateShowedNotes();
        }
    };

    //all buttons listener
    public View.OnClickListener button2ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            //user filters
            if (userFilterIsVisible) {
                userFilterSelection = 2;
            }

            //location filter
            else if (locationFilterIsVisible) {
                locationFilterSelection = Utils.DISTANCE_MEDIUM;
            }

            //date filters
            else {
                dateFilterSelection = Utils.MONTH_MILI;
            }
            //change colors of buttons and update visible notes
            setButtonsColor();
            updateShowedNotes();
        }
    };

    //all buttons listener
    public View.OnClickListener button3ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            //user filters
            if (userFilterIsVisible) {
                userFilterSelection = 3;
            }

            //location filter
            else if (locationFilterIsVisible) {
                locationFilterSelection = Utils.DISTANCE_LONG;
            }

            //date filters
            else {
                dateFilterSelection = Utils.MONTH_MILI;

            }
            //change colors of buttons and update visible notes
            setButtonsColor();
            updateShowedNotes();
        }
    };


}
