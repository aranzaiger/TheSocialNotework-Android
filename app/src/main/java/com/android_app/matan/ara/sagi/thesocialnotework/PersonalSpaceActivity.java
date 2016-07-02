package com.android_app.matan.ara.sagi.thesocialnotework;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_space);

        this.locationPermission = true;

        //check for permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERM);


        this.noteList = (ListView) findViewById(R.id.ps_list_listview);
        addBtn = (Button) findViewById(R.id.ps_new_note_button);
        gpsUtils = new GPSUtils(this);


        listOfNotes = new ArrayList<>();
        //add demo notes to view
        addDemoNotes(listOfNotes);
        ListAdapter la = new ListAdapter(this, listOfNotes);
        noteList.setAdapter(la);

        addBtn.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View v) {
                                          final Dialog dialog = new Dialog(PersonalSpaceActivity.this);

                                          dialog.setContentView(R.layout.note_view_full);
                                          dialog.setTitle("New Note");
                                          WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                                          lp.copyFrom(dialog.getWindow().getAttributes());
                                          lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                          lp.height = WindowManager.LayoutParams.MATCH_PARENT;


                                          //                final EditText editText = (EditText) dialog.findViewById(R.id.editText);
                                          //                Button btnSave          = (Button) dialog.findViewById(R.id.save);
                                          //                Button btnCancel        = (Button) dialog.findViewById(R.id.cancel);
                                          dialog.setCancelable(false);
                                          dialog.show();
                                          dialog.getWindow().setAttributes(lp);


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
                                                  //volley post
                                                  final JSONObject noteJson = new JSONObject();
                                                  try {

                                                      noteJson.put("owner_id", "5634472569470976");
                                                      noteJson.put("title", newTitle.getText());
                                                      noteJson.put("lat", gpsUtils.getLatitude());
                                                      noteJson.put("lng", gpsUtils.getLongitude());
                                                      noteJson.put("address", gpsUtils.getAddress());
                                                      noteJson.put("body", newBody.getText());
                                                      noteJson.put("is_public", permissionSwitch.isChecked());
//                                                      noteJson.put("tags",);
                                                      Log.d(TAG, "Json: " + noteJson.toString());


                                                  } catch (Exception e) {
                                                      Log.d(TAG, e.toString());
                                                  }

                                                  VolleyUtilSingleton.getInstance(PersonalSpaceActivity.this).post(BASE_URL + "/note/upsert", noteJson, newNoteSuccess, newNoteError);
                                                  dialog.dismiss();
                                              }
                                          });

                                          permissionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                              public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                  if (isChecked)
                                                      permissionSwitch.setText(R.string.nvf_public_label);
                                                  else
                                                      permissionSwitch.setText(R.string.nvf_private_label);

                                                  // do something, the isChecked will be
                                                  // true if the switch is in the On position
                                              }
                                          });


                                      }
                                  }

        );


    }


    public void addDemoNotes(List<Note> listOfNotes) {
        Note n1 = new Note("1", 100, 100, "location1", "My 1st Title", "ohh i'm so sexy1", System.currentTimeMillis() / 1000, true);
        Note n2 = new Note("2", 200, 200, "location2", "My 2st Title", "ohh i'm so sexy2", System.currentTimeMillis() / 1000, true);
        Note n3 = new Note("3", 300, 300, "hell", "My 3st Title", "ohh i'm so sexy3", System.currentTimeMillis() / 1000, true);
        Note n4 = new Note("4", 400, 400, "hell2", "My 4st Title", "ohh i'm so sexy4", System.currentTimeMillis() / 1000, true);
        listOfNotes.add(n1);
        listOfNotes.add(n2);
        listOfNotes.add(n3);
        listOfNotes.add(n4);
    }

    public void setLocationPermission(boolean locationPermission) {
        this.locationPermission = locationPermission;
    }


    Response.Listener<JSONObject> newNoteSuccess = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            String s = "";

            try {
                s= response.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "newNoteSuccess: response - " + response.toString());
            Log.d(TAG, "newNoteSuccess: id response - " + s);
            try {
                Note addNote = new Note(
                        "12345",
                        Float.parseFloat(response.getJSONObject("location").getString("lat")),
                        Float.parseFloat(response.getJSONObject("location").getString("lng")),
                        response.getJSONObject("location").getString("address"),
                        response.getString("title"),
                        response.getString("body"),
                        response.getLong("created_at"),
                        response.getBoolean("is_public")
                );
                listOfNotes.add(addNote);
//                addNoteToArray(addNote);
            } catch (JSONException e) {
                Log.e(TAG, "newNoteSuccess:" + e.getMessage());
            }

        }
    };

//    private void addNoteToArray(Note addNote) {
//        listOfNotes.addNote
//    }

    Response.ErrorListener newNoteError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d(TAG, "newNoteError: msg: " + error.getMessage());
        }
    };


}
