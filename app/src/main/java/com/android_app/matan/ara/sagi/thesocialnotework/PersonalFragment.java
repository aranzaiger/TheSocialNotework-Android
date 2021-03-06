package com.android_app.matan.ara.sagi.thesocialnotework;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
  private List<Note> listOfNotes, presentedNotes;
  private ListAdapter noteListAdapter;
  private String userId;
  private final String TAG = "[TSN/PersonalFragment]";
  private MainActivity activity;
  private final int FINE_PERM = 0;
  private int userFilterSelection;
  private Long dateFilterSelection;
  private ImageButton dateFilter;
  private ImageButton userFilter;
  private Button map_small_filter;
  private Button map_medium_filter;
  private Button map_large_filter;
  private LinearLayout personalSpaceFilters;
  private boolean dateFilterIsVisible = false;
  private boolean userFilterIsVisible = false;

  public PersonalFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_personal, container, false);
    // Inflate the layout for this fragment
    this.activity = (MainActivity) getActivity();
    Bundle bundle = getArguments();
    this.userId = activity.getUserId();
    Log.d(TAG, "onCreateView: userID: " + userId);
    //set default values for filters
    dateFilterSelection = Utils.MONTH_MILI;
    userFilterSelection = 3;


    ActivityCompat.requestPermissions(activity, new String[]{
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
      },
      FINE_PERM
    );


    this.noteList = (ListView) view.findViewById(R.id.ps_list_listview);
    gpsUtils = activity.getGPSUtils();
    gpsUtils.getLocation();
    listOfNotes = new ArrayList<>();
    presentedNotes = new ArrayList<>();

    noteListAdapter = new ListAdapter(getContext(), presentedNotes);
    noteList.setAdapter(noteListAdapter);
    noteList.setOnItemClickListener(new ItemClickedListener());
    Utils.showLoadingDialog(getActivity(), "Fetching..", "getting your notes");

    dateFilter = (ImageButton) view.findViewById(R.id.personalSpace_date_filter);
    userFilter = (ImageButton) view.findViewById(R.id.personalSpace_premission_filter);

    map_small_filter = (Button) view.findViewById(R.id.personalSpace_small_filter);
    map_medium_filter = (Button) view.findViewById(R.id.personalSpace_medium_filter);
    Log.d(TAG, "onCreateView: personalSpace_filter_options = " + R.id.personalSpace_filter_options);

    map_large_filter = (Button) view.findViewById(R.id.personalSpace_large_filter);

    map_small_filter.setOnClickListener(button1ClickListener);
    map_medium_filter.setOnClickListener(button2ClickListener);
    map_large_filter.setOnClickListener(button3ClickListener);

    personalSpaceFilters = (LinearLayout) view.findViewById(R.id.personalSpace_filter_options);

    // Date Filter Listener
    dateFilter.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View v) {
        Log.d(TAG, "onClick: dateFilter pressed");
        if (dateFilterIsVisible) {
          dateFilterIsVisible = false;
          personalSpaceFilters.setVisibility(View.GONE);
        } else {
          personalSpaceFilters.setVisibility(View.VISIBLE);
          dateFilterIsVisible = true;
          userFilterIsVisible = false;

          // set text button in the right filter string
          map_small_filter.setText(R.string.day);
          map_medium_filter.setText(R.string.week);
          map_large_filter.setText(R.string.month);
        }
        setButtonsColor();

      }
    });
    // User Filter Listener
    userFilter.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.d(TAG, "onClick: userFilter pressed");
        //if pressed same filter twice - close filters.
        if (userFilterIsVisible) {
          userFilterIsVisible = false;
          personalSpaceFilters.setVisibility(View.GONE);
        } else {
          personalSpaceFilters.setVisibility(View.VISIBLE);
          userFilterIsVisible = true;
          dateFilterIsVisible = false;

          // set text button in the right filter string
          map_small_filter.setText(R.string.privateNote);
          map_medium_filter.setText(R.string.publicNote);
          map_large_filter.setText(R.string.privateAndPublic);
        }
        setButtonsColor();

      }
    });

    VolleyUtilSingleton.getInstance(getActivity()).get(BASE_URL + "/note/all?uid=" + userId, getNotesSuccessListener, Utils.genericErrorListener);
    // The New "Add Button" - Floating Fab Button
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

//  public void getAllNotes() {
//    Log.d(TAG, "url: " + BASE_URL + "/note/all?uid=" + userId);
//    VolleyUtilSingleton.getInstance(getActivity()).get(BASE_URL + "/note/all?uid=" + userId, getNotesSuccessListener, Utils.genericErrorListener);
//  }

  // New Note Dialog (View)
  private View.OnClickListener addNewNoteDialog = new View.OnClickListener() {
    public void onClick(View v) {
      //create and configure dialog
      final Dialog dialog = new Dialog(getActivity());
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      dialog.setContentView(R.layout.note_view_full);
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
          if (newBody.getText().length() < 1) {
            Toast.makeText(getActivity(), "Cant Submit an empty body", Toast.LENGTH_LONG).show();
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
        addNoteFromJsonObj(noteObject, time, 0);
        updateShowedNotes();
      } catch (Exception e) {
        Log.e(TAG, "newNoteSuccess:" + e.getMessage());
      }

    }
  };


  //response listener for getting all user notes
  Response.Listener<JSONObject> getNotesSuccessListener = new Response.Listener<JSONObject>() {
    @Override
    public void onResponse(JSONObject response) {
      Log.d(TAG, "getNotesSuccessListener: " + response.toString());
      Utils.dismissLoadingDialog();
      try {
        //need to get all notes and add to listOfNotes
        JSONArray noteObjectsArray = response.getJSONArray("notes");
        activity.getUser().setNumber_of_notes(noteObjectsArray.length());
        Date time = new Date();
        for (int i = 0; i < noteObjectsArray.length(); i++) {
          JSONObject noteObject = noteObjectsArray.getJSONObject(i);
          time.setTime(noteObject.getLong("created_at"));

          addNoteFromJsonObj(noteObject, time, -1);
        }
        updateShowedNotes();
      } catch (Exception e) {
        Log.e(TAG, "newNoteSuccess:" + e.getMessage());
      }

    }
  };

  /**
   * Converts JSON Array to A Java String ArrayList
   * @param jArray JSONArray Object
   * @return ArrayList<String>
     */
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


  /**
   * Add note from JSON Object to the list of notes
   * @param noteObject
   * @param time
   * @param position
   * @throws JSONException
     */
  private void addNoteFromJsonObj(JSONObject noteObject, Date time, int position) throws JSONException {
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
    Log.d(TAG, "addNoteFromJsonObj: " + addNote.getDate() + " " + addNote.getTime());
    if (position != 0)
      listOfNotes.add(addNote);
    else
      listOfNotes.add(position, addNote);


  }

  /**
   * The ListView Item Listener
   */
  class ItemClickedListener implements AdapterView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
      //create and configure dialog
      final Note note = presentedNotes.get(position);
      final Dialog noteViewDialog = new Dialog(getActivity());
      noteViewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      noteViewDialog.setContentView(R.layout.note_display_full);

      WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
      lp.copyFrom(noteViewDialog.getWindow().getAttributes());
      lp.width = WindowManager.LayoutParams.MATCH_PARENT;
      lp.height = WindowManager.LayoutParams.MATCH_PARENT;
      noteViewDialog.show();


      //get note_view_full layout elements
      final TextView title = (TextView) noteViewDialog.findViewById(R.id.ndf_title_textview);
      final TextView body = (TextView) noteViewDialog.findViewById(R.id.ndf_body_textview);
      final TextView time = (TextView) noteViewDialog.findViewById(R.id.ndf_time_textview);
      final TextView date = (TextView) noteViewDialog.findViewById(R.id.ndf_date_textview);
      final TextView location = (TextView) noteViewDialog.findViewById(R.id.ndf_address_textview);
      final TextView likes = (TextView) noteViewDialog.findViewById(R.id.ndf_likes_textview);
      final TextView permission = (TextView) noteViewDialog.findViewById(R.id.ndf_permission_textview);
      final ImageButton deleteBtn = (ImageButton) noteViewDialog.findViewById(R.id.ndf_delete_imagebutton);
      final ImageView avatar = (RoundAvatarImageView) noteViewDialog.findViewById(R.id.note_user_avatar);


      title.setText(note.getTitle());
      body.setText(note.getBody());
      date.setText(note.getDate());
      time.setText(note.getTime());
      location.setText(note.getAddress());
      if (likes != null) likes.setText("" + note.getLikes());
      permission.setText("" + (note.isPublic() ? "Public" : "Private"));
      Utils.URLtoImageView(avatar, note.getAvatar());
      // DElete Button Listener
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
                JSONObject delNote = new JSONObject();
                try {
                  delNote.put("uid", userId);
                  delNote.put("nid", note.getId());
                  VolleyUtilSingleton.getInstance(getActivity()).post(BASE_URL + "/note/delete", delNote, Utils.deleteNoteSuccessListener, Utils.genericErrorListener);
                  listOfNotes.remove(presentedNotes.get(position));
                  presentedNotes.remove(position);
                } catch (JSONException e) {
                  Toast.makeText(getActivity(), "Something went wrong.\n Failed to delete note...", Toast.LENGTH_LONG).show();
                  e.printStackTrace();
                }
                updateShowedNotes();
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

  //all buttons listener
  public View.OnClickListener button1ClickListener = new View.OnClickListener() {
    @Override
    public void onClick(final View v) {
      //user filters
      if (userFilterIsVisible) {
        userFilterSelection = 1;
      }
      //date filters
      else {
        dateFilterSelection = Utils.DAY_MILI;

      }
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
      //date filters
      else {
        dateFilterSelection = Utils.WEEK_MILI;
      }
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
      //date filters
      else {
        dateFilterSelection = Utils.MONTH_MILI;

      }
      setButtonsColor();

      updateShowedNotes();

    }
  };


  /**
   * Update The Shown Notes (FIlters)
   */
  public void updateShowedNotes() {
    presentedNotes = new ArrayList<>();
    long timeDifference;
    Date now = new Date();
    Date targetDate;
    for (Note note : listOfNotes) {
      //get note location and date
      targetDate = new Date(note.getTimestamp());
      //get time and date differences
      timeDifference = now.getTime() - targetDate.getTime();
      //add to currently presented list according to filters.
      if (timeDifference <= dateFilterSelection
        && ((!note.isPublic && userFilterSelection == 1) || (note.isPublic && userFilterSelection == 2) || (userFilterSelection == 3))) {
        presentedNotes.add(note);
      }

    }
    noteListAdapter.updateList(presentedNotes);
    noteList.setAdapter(noteListAdapter);
  }

  /**
   * Set The Filter Button Colors
   */
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
  }

}
