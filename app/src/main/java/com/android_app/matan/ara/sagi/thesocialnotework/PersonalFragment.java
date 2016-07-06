package com.android_app.matan.ara.sagi.thesocialnotework;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class PersonalFragment extends Fragment {

    protected ListView noteList;
    protected Button addBtn;
    private final int FINE_PERM = 0;
    private final String BASE_URL = "http://thesocialnotework-api.appspot.com/api";
    private boolean locationPermission;
    private GPSUtils gpsUtils;
    private List<Note> listOfNotes;
    private ListAdapter noteListAdapter;
    private String userId;
    private final String TAG = "[TSN/PersonalFragment]";

    public PersonalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        // Inflate the layout for this fragment
        Bundle bundle = getArguments();
        this.userId = bundle.getString("user_id");
        Log.d(TAG, "onCreateView: userID: " + userId);
        this.locationPermission = true;

        //check for permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERM);


        this.noteList = (ListView) view.findViewById(R.id.ps_list_listview);
        addBtn = (Button) view.findViewById(R.id.ps_new_note_button);
        gpsUtils = ((MainActivity)getActivity()).getGPSUtils();
        gpsUtils.getLocation();


        listOfNotes = new ArrayList<>();
        //TODO - remove -add demo notes to view
//        addDemoNotes(listOfNotes);
        noteListAdapter = new ListAdapter(getContext(), listOfNotes);

        noteList.setAdapter(noteListAdapter);
//        new HeavyWorker(this).execute();
        getAllNotes();

//https://thesocialnotework-api.appspot.com/api/note/all?uid=<USER_ID>
        addBtn.setOnClickListener(addNewNoteDialog);
        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    public void getAllNotes(){
        Log.d(TAG, "url: "+BASE_URL + "/note/all?uid="+userId);
        VolleyUtilSingleton.getInstance(getActivity()).get(BASE_URL + "/note/all?uid="+userId, getNotesSuccessListener, genericErrorListener);
    }

    public void setLocationPermission(boolean locationPermission) {
        this.locationPermission = locationPermission;
    }

    private View.OnClickListener addNewNoteDialog = new View.OnClickListener() {
        public void onClick(View v) {

            //create and configure dialog
            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.note_view_full);
            dialog.setTitle("New Note");
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.setCancelable(false);
            dialog.show();
            dialog.getWindow().setAttributes(lp);


            //get note_view_full layout elements
            final Switch permissionSwitch = (Switch) dialog.findViewById(R.id.nvf_note_permission);
            final EditText newTitle = (EditText) dialog.findViewById(R.id.nvf_note_title);
            final EditText newBody = (EditText) dialog.findViewById(R.id.nvf_note_content);
            Button saveBtn = (Button) dialog.findViewById(R.id.nvf_note_submit_btn);
            Button cancelBtn = (Button) dialog.findViewById(R.id.nvf_note_cancel_btn);

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            saveBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    //title too short
                    if (newTitle.getText().length() == 0)
                    {
                        Toast toast = Toast.makeText(getActivity(), "Title too short.", Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }

                    //title too long
                    if (newTitle.getText().length() > 20)
                    {
                        Toast toast = Toast.makeText(getActivity(), "Title too long.\n Use up to 20 notes.", Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                    //volley post
                    final JSONObject noteJson = new JSONObject();
                    try {

                        //TODO need to get owner id from login screen
                        noteJson.put("owner_id", userId);
                        noteJson.put("title", newTitle.getText());
                        noteJson.put("lat", gpsUtils.getLatitude());
                        noteJson.put("lng", gpsUtils.getLongitude());
                        noteJson.put("address", gpsUtils.getAddress());
                        noteJson.put("body", newBody.getText());
                        noteJson.put("is_public", permissionSwitch.isChecked());
//                      noteJson.put("tags",);
                        Log.d(TAG, "Json: " + noteJson.toString());


                    } catch (Exception e) {
                        Log.d(TAG, "saveBtn: "+e.toString());
                    }

                    //send request and close dialog
                    VolleyUtilSingleton.getInstance(getActivity()).post(BASE_URL + "/note/upsert", noteJson, newNoteSuccessListener, genericErrorListener);
                    dialog.dismiss();
                }
            });

            //change text of switch according to state.
            permissionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        permissionSwitch.setText(R.string.nvf_public_label);
                    else
                        permissionSwitch.setText(R.string.nvf_private_label);
                }
            });


        }
    };

    //response listener for adding new note
    Response.Listener<JSONObject> newNoteSuccessListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d(TAG, "newNoteSuccess: response - " + response.toString());
            try {
                Date time = new Date();
                JSONObject noteObject = response.getJSONObject("note");
                time.setTime(noteObject.getLong("created_at"));
                addNoteFromJsonObj(noteObject, time);

//                Note addNote = new Note(
//                        noteObject.getString("id"),
//                        Float.parseFloat(noteObject.getJSONObject("location").getString("lat")),
//                        Float.parseFloat(noteObject.getJSONObject("location").getString("lng")),
//                        noteObject.getJSONObject("location").getString("address"),
//                        noteObject.getString("title"),
//                        noteObject.getString("body"),
//                        time.toString(),
//                        noteObject.getBoolean("is_public"),
//                        noteObject.getInt("likes"),
//                        jsonArrayToStringArray(noteObject.getJSONArray("tags"))
//                );
//
//                listOfNotes.add(addNote);
                noteList.setAdapter(noteListAdapter);
            } catch (Exception e) {
                Log.e(TAG, "newNoteSuccess:" + e.getMessage());
            }

        }
    };


    //response Error listener for adding new note
    Response.ErrorListener newNoteErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d(TAG, "newNoteError: msg: " + error.getMessage());
        }
    };


    //response listener for getting all user notes
    Response.Listener<JSONObject> getNotesSuccessListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d(TAG,"getNotesSuccessListener: "+response.toString());
            try {
                //need to get all notes and add to listOfNotes
                JSONArray noteObjectsArray = response.getJSONArray("notes");
                Date time = new Date();
                for (int i = 0; i < noteObjectsArray.length(); i++) {
                    JSONObject noteObject = noteObjectsArray.getJSONObject(i);
                    time.setTime(noteObject.getLong("created_at"));

                    addNoteFromJsonObj(noteObject, time);
//                    Note addNote = new Note(
//                            noteObject.getString("id"),
//                            Float.parseFloat(noteObject.getJSONObject("location").getString("lat")),
//                            Float.parseFloat(noteObject.getJSONObject("location").getString("lng")),
//                            noteObject.getJSONObject("location").getString("address"),
//                            noteObject.getString("title"),
//                            noteObject.getString("body"),
//                            time.toString(),
//                            noteObject.getBoolean("is_public"),
//                            noteObject.getInt("likes"),
//                            jsonArrayToStringArray(noteObject.getJSONArray("tags"))
//                    );
//                    listOfNotes.add(addNote);
                }
                noteList.setAdapter(noteListAdapter);
            } catch (Exception e) {
                Log.e(TAG, "newNoteSuccess:" + e.getMessage());
            }

        }
    };


    //response ErrorListener for getting all user notes
    Response.ErrorListener getNotesErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d(TAG,"getNotesErrorListener: "+error.getMessage());
        }
    };

    //Generic response ErrorListener
    Response.ErrorListener genericErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d(TAG,"genericErrorListener");
            error.printStackTrace();
        }
    };


    public void requestPermissions(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_PERM);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    FINE_PERM);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }

    }



    private ArrayList<String> jsonArrayToStringArray(JSONArray jArray){
        ArrayList<String> stringArray = new ArrayList<String>();
        for(int i = 0, count = jArray.length(); i< count; i++)
        {
            try {
                JSONObject jsonObject = jArray.getJSONObject(i);
                stringArray.add(jsonObject.toString());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return stringArray;
    }

    private void addNoteFromJsonObj(JSONObject noteObject, Date time) throws JSONException {
        Note addNote = new Note(
                noteObject.getString("id"),
                Float.parseFloat(noteObject.getJSONObject("location").getString("lat")),
                Float.parseFloat(noteObject.getJSONObject("location").getString("lng")),
                noteObject.getJSONObject("location").getString("address"),
                noteObject.getString("title"),
                noteObject.getString("body"),
                time.toString(),
                noteObject.getBoolean("is_public"),
                noteObject.getInt("likes"),
                jsonArrayToStringArray(noteObject.getJSONArray("tags"))
        );
        listOfNotes.add(addNote);

    }

}
