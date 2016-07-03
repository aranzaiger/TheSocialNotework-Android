package com.android_app.matan.ara.sagi.thesocialnotework;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


//http://thesocialnotework.appspot.com/api/status | http://localhost:8080/api/note/all?uid=<userID>
public class PersonalSpaceActivity extends AppCompatActivity {

    protected ListView noteList;
    protected Button addBtn;
    private final String TAG = "Personal Space Activity";
    private final int FINE_PERM = 0;
    private final String BASE_URL = "http://thesocialnotework-api.appspot.com/api";
    private boolean locationPermission;
    private GPSUtils gpsUtils;
    private List<Note> listOfNotes;
    private ListAdapter noteListAdapter;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_space);
        Bundle b = getIntent().getExtras();
        userId = b.getString("user_id");
        Log.d(TAG, "-------------------------USER ID: " + userId);

        this.locationPermission = true;

        //check for permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERM);


        this.noteList = (ListView) findViewById(R.id.ps_list_listview);
        addBtn = (Button) findViewById(R.id.ps_new_note_button);
        gpsUtils = new GPSUtils(this);
        gpsUtils.getLocation();


        listOfNotes = new ArrayList<>();
        //TODO - remove -add demo notes to view
//        addDemoNotes(listOfNotes);
        noteListAdapter = new ListAdapter(this, listOfNotes);

        noteList.setAdapter(noteListAdapter);
        new HeavyWorker(this).execute();
//        getAllNotes();

        addBtn.setOnClickListener(addNewNoteDialog);

        // click on listView item
        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //create and configure dialog
                final Note note = listOfNotes.get(position);
                final Dialog noteViewDialog = new Dialog(PersonalSpaceActivity.this);
                noteViewDialog.setContentView(R.layout.note_display_full);
                noteViewDialog.setTitle("You wrote...");

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(noteViewDialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                noteViewDialog.show();
//                dialog.getWindow().setAttributes(lp);


                //get note_view_full layout elements
                final TextView title = (TextView) noteViewDialog.findViewById(R.id.ndf_title_textview);
                final TextView body = (TextView) noteViewDialog.findViewById(R.id.ndf_body_textview);
                final TextView time = (TextView) noteViewDialog.findViewById(R.id.ndf_time_textview);
                final TextView location = (TextView) noteViewDialog.findViewById(R.id.ndf_address_textview);
                final TextView likes = (TextView) noteViewDialog.findViewById(R.id.ndf_likes_textview);
                final TextView tags = (TextView) noteViewDialog.findViewById(R.id.ndf_tags_textview);
                final TextView permission = (TextView) noteViewDialog.findViewById(R.id.ndf_permission_textview);
                final ImageButton deleteBtn = (ImageButton) noteViewDialog.findViewById(R.id.ndf_delete_imagebutton);


                title.setText(note.getTitle());
                body.setText(note.getBody());
                time.setText(note.getTimestamp());
                location.setText("Tags: " + note.getAddress());
                likes.setText("Likes: " + note.getLikes());
                tags.setText(note.getTags().toString());
                permission.setText("Permission: " + (note.isPublic() ? "Public" : "Private"));

                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //Put up the Yes/No message box
                        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalSpaceActivity.this);
                        builder
                                .setTitle("Delete Note")
                                .setMessage("Are you sure you want to delete the note?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Yes button clicked, do something
                                        Toast.makeText(PersonalSpaceActivity.this, "Item Deleted!",
                                                Toast.LENGTH_SHORT).show();
                                        //TODO send delete
                                        JSONObject delNote = new JSONObject();
                                        try {
                                            delNote.put("uid", userId);
                                            delNote.put("nid", note.getId());
                                            VolleyUtilSingleton.getInstance(PersonalSpaceActivity.this).post(BASE_URL + "/note/delete",delNote, deleteNoteSuccessListener, genericErrorListener);
                                            listOfNotes.remove(position);

                                        } catch (JSONException e) {
                                            Toast.makeText(PersonalSpaceActivity.this, "Something went wrong.\n Failed to delete note...", Toast.LENGTH_LONG).show();
                                            e.printStackTrace();
                                        }
                                        noteList.setAdapter(noteListAdapter);
//                                        dialog.dismiss();
                                        noteViewDialog.dismiss();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Yes button clicked, do something
                                        Toast.makeText(PersonalSpaceActivity.this, "Note still here!",
                                                Toast.LENGTH_SHORT).show();
//                                        dialog.dismiss();
                                        noteViewDialog.dismiss();
                                    }
                                })                        //Do nothing on no
                                .show();
                    }
                });

            }
        });


    }


    public void getAllNotes() {
        Log.d(TAG, "url: " + BASE_URL + "/note/all?uid=" + userId);
        VolleyUtilSingleton.getInstance(PersonalSpaceActivity.this).get(BASE_URL + "/note/all?uid=" + userId, getNotesSuccessListener, genericErrorListener);
    }

    private View.OnClickListener addNewNoteDialog = new View.OnClickListener() {
        public void onClick(View v) {

            //create and configure dialog
            final Dialog dialog = new Dialog(PersonalSpaceActivity.this);
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
                    if (newTitle.getText().length() == 0) {
                        Toast toast = Toast.makeText(PersonalSpaceActivity.this, "Title too short.", Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }

                    //title too long
                    if (newTitle.getText().length() > 20) {
                        Toast toast = Toast.makeText(PersonalSpaceActivity.this, "Title too long.\n Use up to 20 notes.", Toast.LENGTH_LONG);
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
                        Log.d(TAG, "saveBtn: " + e.toString());
                    }

                    //send request and close dialog
                    VolleyUtilSingleton.getInstance(PersonalSpaceActivity.this).post(BASE_URL + "/note/upsert", noteJson, newNoteSuccessListener, genericErrorListener);
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


    public void setLocationPermission(boolean locationPermission) {
        this.locationPermission = locationPermission;
    }


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
            Log.d(TAG, "getNotesSuccessListener: " + response.toString());
            try {
                //need to get all notes and add to listOfNotes
                JSONArray noteObjectsArray = response.getJSONArray("notes");
                Date time = new Date();
                for (int i = 0; i < noteObjectsArray.length(); i++) {
                    JSONObject noteObject = noteObjectsArray.getJSONObject(i);
                    time.setTime(noteObject.getLong("created_at"));

                    addNoteFromJsonObj(noteObject, time);
                }
                noteList.setAdapter(noteListAdapter);
            } catch (Exception e) {
                Log.e(TAG, "newNoteSuccess:" + e.getMessage());
            }
        }
    };

    //response listener for getting all user notes
    Response.Listener<JSONObject> deleteNoteSuccessListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d(TAG, "deleteNoteSuccessListener: " + response.toString());


        }
    };

    //response ErrorListener for getting all user notes
    Response.ErrorListener getNotesErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d(TAG, "getNotesErrorListener: " + error.getMessage());
        }
    };

    //Generic response ErrorListener
    Response.ErrorListener genericErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d(TAG, "genericErrorListener");
            error.printStackTrace();
        }
    };


    public void requestPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(PersonalSpaceActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(PersonalSpaceActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_PERM);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }

        if (ContextCompat.checkSelfPermission(PersonalSpaceActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(PersonalSpaceActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }

        if (ContextCompat.checkSelfPermission(PersonalSpaceActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(PersonalSpaceActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    FINE_PERM);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }

    }


    private ArrayList<String> jsonArrayToStringArray(JSONArray jArray) {
        ArrayList<String> stringArray = new ArrayList<String>();
        for (int i = 0, count = jArray.length(); i < count; i++) {
            try {
                JSONObject jsonObject = jArray.getJSONObject(i);
                stringArray.add(jsonObject.toString());
            } catch (JSONException e) {
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
