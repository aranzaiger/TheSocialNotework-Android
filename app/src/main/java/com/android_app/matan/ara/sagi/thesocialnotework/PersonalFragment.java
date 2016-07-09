package com.android_app.matan.ara.sagi.thesocialnotework;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
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
    private final String BASE_URL = "http://thesocialnotework-api.appspot.com/api";
    private GPSUtils gpsUtils;
    private List<Note> listOfNotes;
    private ListAdapter noteListAdapter;
    private String userId;
    private final String TAG = "[TSN/PersonalFragment]";
    private MainActivity activity;
    private final int FINE_PERM = 0, CAMERA_PERM = 1;


    private ImageButton dateFilter;
    private ImageButton userFilter;
    private Button map_small_filter;
    private Button map_medium_filter;
    private Button map_large_filter;
    private LinearLayout personalSpaceFilters;
    private boolean dateFilterIsVisible = false;
    private boolean userFilterIsVisible = false;

    private final String day = "24 hours";
    private final String week = "Week";
    private final String month = "Month";
    private final String privateNote = "Private";
    private final String publicNote = "Public";
    private final String privateAndPublic = "All";

    public PersonalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        // Inflate the layout for this fragment
        activity = (MainActivity) getActivity();
        Bundle bundle = getArguments();
        this.userId = activity.getUserId();
        Log.d(TAG, "onCreateView: userID: " + userId);

        ActivityCompat.requestPermissions(activity, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.CAMERA
                },
                FINE_PERM
            );
//        ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERM);

//        if (!Utils.arePermissionsGranted()) {
//            Toast.makeText(getContext(), "Missing some Permissions...\nPlease go to app info and enable them", Toast.LENGTH_LONG).show();
//        }


        this.noteList = (ListView) view.findViewById(R.id.ps_list_listview);
        gpsUtils = activity.getGPSUtils();
        gpsUtils.getLocation();
        listOfNotes = new ArrayList<>();
        noteListAdapter = new ListAdapter(getContext(), listOfNotes);
        noteList.setAdapter(noteListAdapter);
        noteList.setOnItemClickListener(new ItemClickedListener());
        Utils.showLoadingDialog(getActivity(), "Fetching..", "getting your notes");

        dateFilter = (ImageButton) view.findViewById(R.id.personalSpace_date_filter);
        userFilter = (ImageButton) view.findViewById(R.id.personalSpace_premission_filter);

        map_small_filter = (Button) view.findViewById(R.id.personalSpace_small_filter);
        map_medium_filter = (Button) view.findViewById(R.id.personalSpace_medium_filter);
        map_large_filter = (Button) view.findViewById(R.id.personalSpace_large_filter);

        personalSpaceFilters = (LinearLayout) view.findViewById(R.id.personalSpace_filter_options);

        dateFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateFilterIsVisible) {
                    dateFilterIsVisible = false;
                    personalSpaceFilters.setVisibility(View.INVISIBLE);
                } else {
                    personalSpaceFilters.setVisibility(View.VISIBLE);
                    dateFilterIsVisible = true;
                    userFilterIsVisible = false;

                    // set text button in the right filter string
                    map_small_filter.setText(day);
                    map_medium_filter.setText(week);
                    map_large_filter.setText(month);
                }
            }
        });

        userFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userFilterIsVisible) {
                    userFilterIsVisible = false;
                    personalSpaceFilters.setVisibility(View.INVISIBLE);
                } else {
                    personalSpaceFilters.setVisibility(View.VISIBLE);
                    userFilterIsVisible = true;
                    dateFilterIsVisible = false;

                    // set text button in the right filter string
                    map_small_filter.setText(privateNote);
                    map_medium_filter.setText(publicNote);
                    map_large_filter.setText(privateAndPublic);
                }
            }
        });

        // get all notes according to some default filter ? // TODO: Aran?
        getAllNotes();


//https://thesocialnotework-api.appspot.com/api/note/all?uid=<USER_ID>
        // The New "Add Button"
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(addNewNoteDialog);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Utils.dismissLoadingDialog();
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Utils.dismissLoadingDialog();
    }

    public void getAllNotes() {
        Log.d(TAG, "url: " + BASE_URL + "/note/all?uid=" + userId);
        VolleyUtilSingleton.getInstance(getActivity()).get(BASE_URL + "/note/all?uid=" + userId, getNotesSuccessListener, Utils.genericErrorListener);
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
                    if (newTitle.getText().length() == 0) {
                        Toast toast = Toast.makeText(getActivity(), "Title too short.", Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }

                    //title too long
                    if (newTitle.getText().length() > 20) {
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
                        Log.d(TAG, "saveBtn: " + e.toString());
                    }

                    //send request and close dialog
                    VolleyUtilSingleton.getInstance(getActivity()).post(BASE_URL + "/note/upsert", noteJson, newNoteSuccessListener, Utils.genericErrorListener);
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
                noteList.setAdapter(noteListAdapter);
            } catch (Exception e) {
                Log.e(TAG, "newNoteSuccess:" + e.getMessage());
            }

        }
    };


//  //response Error listener for adding new note
//  Response.ErrorListener newNoteErrorListener = new Response.ErrorListener() {
//    @Override
//    public void onErrorResponse(VolleyError error) {
//      Log.d(TAG, "newNoteError: msg: " + error.getMessage());
//    }
//  };


    //response listener for getting all user notes
    Response.Listener<JSONObject> getNotesSuccessListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d(TAG, "getNotesSuccessListener: " + response.toString());
            Utils.dismissLoadingDialog();
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


//  //response ErrorListener for getting all user notes
//  Response.ErrorListener getNotesErrorListener = new Response.ErrorListener() {
//    @Override
//    public void onErrorResponse(VolleyError error) {
//      Log.d(TAG, "getNotesErrorListener: " + error.getMessage());
//      MainActivity.dismissLoadingDialog();
//    }
//  };

//  //Generic response ErrorListener
//  Response.ErrorListener genericErrorListener = new Response.ErrorListener() {
//    @Override
//    public void onErrorResponse(VolleyError error) {
//      Log.d(TAG, "genericErrorListener");
//      MainActivity.dismissLoadingDialog();
//      error.printStackTrace();
//    }
//  };


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
                noteObject.getString("avatar"),
                noteObject.getString("owner_id"),
                jsonArrayToStringArray(noteObject.getJSONArray("tags"))
        );
        listOfNotes.add(addNote);

    }

    // click on listView item
    class ItemClickedListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            //create and configure dialog
            final Note note = listOfNotes.get(position);
            final Dialog noteViewDialog = new Dialog(getActivity());
            noteViewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            noteViewDialog.setContentView(R.layout.note_display_full);
//            noteViewDialog.setTitle("You wrote...");

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
            final TextView date = (TextView) noteViewDialog.findViewById(R.id.ndf_date_textview);
            final TextView location = (TextView) noteViewDialog.findViewById(R.id.ndf_address_textview);
            final TextView likes = (TextView) noteViewDialog.findViewById(R.id.ndf_likes_textview);
//            final TextView tags = (TextView) noteViewDialog.findViewById(R.id.ndf_tags_textview);
            final TextView permission = (TextView) noteViewDialog.findViewById(R.id.ndf_permission_textview);
            final ImageButton deleteBtn = (ImageButton) noteViewDialog.findViewById(R.id.ndf_delete_imagebutton);
            final ImageView avatar = (RoundAvatarImageView) noteViewDialog.findViewById(R.id.note_user_avatar);


            title.setText(note.getTitle());
            body.setText(note.getBody());
            date.setText(note.getDate());
            time.setText(note.getTime());
            location.setText(note.getAddress());
            if(likes !=null )likes.setText("" + note.getLikes());
//            tags.setText("Tags: "+ note.getTags().toString());
            permission.setText("" + (note.isPublic() ? "Public" : "Private"));
            Utils.URLtoImageView(avatar, note.getAvatar());

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
                                        delNote.put("uid", userId);
                                        delNote.put("nid", note.getId());
                                        VolleyUtilSingleton.getInstance(getActivity()).post(BASE_URL + "/note/delete", delNote, Utils.deleteNoteSuccessListener, Utils.genericErrorListener);
                                        listOfNotes.remove(position);

                                    } catch (JSONException e) {
                                        Toast.makeText(getActivity(), "Something went wrong.\n Failed to delete note...", Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
                                    noteList.setAdapter(noteListAdapter);
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

        }


    }

}
