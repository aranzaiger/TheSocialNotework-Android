package com.android_app.matan.ara.sagi.thesocialnotework;


import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * The Explore Fragment - See all public notes
 */
public class ExploreFragment extends Fragment {

  private static final String TAG = "[TSN/Explore]";
  protected User user;
  protected MainActivity parent;
  private ListAdapter noteListAdapter;
  private List<Note> notes;
  protected ListView list_notes;
  private ImageButton dateFilter, locationFilter;
  private Button map_small_filter, map_medium_filter, map_large_filter;
  private LinearLayout exploreFilters;
  private boolean dateFilterIsVisible = false, locationFilterIsVisible = false;
  private Long dateFilterSelection;
  private float locationFilterSelection;
  private GPSUtils gpsUtils;

  public ExploreFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    parent = (MainActivity) getActivity();
    gpsUtils = parent.getGPSUtils();
    user = parent.getUser();

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_explore, container, false);


    notes = new ArrayList<>();
    //Get all Views from layout
    list_notes = (ListView) view.findViewById(R.id.list_notes);
    noteListAdapter = new ListAdapter(parent, notes);
    list_notes.setOnItemClickListener(new ItemClickedListener());
    dateFilterSelection = Utils.MONTH_MILI;
    locationFilterSelection = Utils.DISTANCE_LONG;
    dateFilter = (ImageButton) view.findViewById(R.id.explore_date_filter);
    locationFilter = (ImageButton) view.findViewById(R.id.explore_location_filter);
    map_small_filter = (Button) view.findViewById(R.id.explore_small_filter);
    map_medium_filter = (Button) view.findViewById(R.id.explore_medium_filter);
    map_large_filter = (Button) view.findViewById(R.id.explore_large_filter);
    map_small_filter.setOnClickListener(button1ClickListener);
    map_medium_filter.setOnClickListener(button2ClickListener);
    map_large_filter.setOnClickListener(button3ClickListener);
    exploreFilters = (LinearLayout) view.findViewById(R.id.explore_filter_options);


      //set listener for date filter button
    dateFilter.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          //check if already pressed
          if (dateFilterIsVisible) {
          dateFilterIsVisible = false;
          exploreFilters.setVisibility(View.GONE);
        } else {
          exploreFilters.setVisibility(View.VISIBLE);
          dateFilterIsVisible = true;
          locationFilterIsVisible = false;

          // set text button in the right filter string
          map_small_filter.setText(R.string.day);
          map_medium_filter.setText(R.string.week);
          map_large_filter.setText(R.string.month);
        }
        setButtonsColor();
      }
    });

      //set listener for location filter button
      locationFilter.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

          //check if already pressed
        if (locationFilterIsVisible) {
          locationFilterIsVisible = false;
          exploreFilters.setVisibility(View.GONE);
        } else {
          exploreFilters.setVisibility(View.VISIBLE);
          locationFilterIsVisible = true;
          dateFilterIsVisible = false;
          // set text button in the right filter string
          map_small_filter.setText(R.string.shortDistance);
          map_medium_filter.setText(R.string.mediumDistance);
          map_large_filter.setText(R.string.longDistance);
        }
        setButtonsColor();
      }
    });

    try {
      getAllNotes();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return view;
  }

  /**
   * This function will retrieve all the Public Notes from our Server.
   * And will throw JSON exception on error
   * @throws JSONException
     */
  public void getAllNotes() throws JSONException {
    Utils.showLoadingDialog(parent, "Exploring...", "Finding some new interesting notes just for you");
    Log.d(TAG, "url: " + Utils.BASE_URL + "/note/getPublic");
    String url = Utils.BASE_URL + "/note/getPublic";
    JSONObject payload = new JSONObject();
    payload.put("id", user.getId());
    VolleyUtilSingleton.getInstance(getActivity()).post(url, payload, getNotesSuccessListener, Utils.genericErrorListener);
  }

  /**
   * The Success callback for our HTTP API call
   */
  Response.Listener<JSONObject> getNotesSuccessListener = new Response.Listener<JSONObject>() {
    @Override
    public void onResponse(JSONObject response) {
      Log.d(TAG, "getNotesSuccessListener: " + response.toString());
      Utils.dismissLoadingDialog();
      try {
        //need to get all notes and add to listOfNotes
        JSONArray noteObjectsArray = response.getJSONArray("notes");
        parent.getUser().setNumber_of_notes(noteObjectsArray.length());
        Date time = new Date();
        for (int i = 0; i < noteObjectsArray.length(); i++) {
          JSONObject noteObject = noteObjectsArray.getJSONObject(i);
          time.setTime(noteObject.getLong("created_at"));
          notes.add(Utils.getNoteFromJsonObj(noteObject, time));
        }
        updateShowedNotes();
        Utils.dismissLoadingDialog();
      } catch (Exception e) {
        Log.e(TAG, "newNoteSuccess:" + e.getMessage());
      }

    }
  };


  // click on listView item
  class ItemClickedListener implements AdapterView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
      //create and configure dialog
      final Note note = notes.get(position);
      final MainActivity localParent = (MainActivity) getActivity();
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
      final ImageButton likeBtn = (ImageButton) noteViewDialog.findViewById(R.id.ndf_delete_imagebutton);
      final ImageView avatar = (RoundAvatarImageView) noteViewDialog.findViewById(R.id.note_user_avatar);
      final ImageView permission_image = (ImageView) noteViewDialog.findViewById(R.id.permission_image);

      title.setText(note.getTitle());
      body.setText(note.getBody());
      date.setText(note.getDate());
      time.setText(note.getTime());
      location.setText(note.getAddress());

      if (likes != null) {
        likeBtn.setBackgroundResource(R.drawable.like_icon);
        if (localParent.getUser().getLiked_notes().contains(note.getId())) {
          likeBtn.setBackgroundResource(R.drawable.like_icon);
        } else {
          likeBtn.setBackgroundResource(R.drawable.unlike_icon);
        }
      }
      likes.setText("" + note.getLikes());

      permission.setVisibility(View.GONE);
      Utils.URLtoImageView(avatar, note.getAvatar());
      permission_image.setVisibility(View.GONE);

      likeBtn.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          //add like only if user didnt like already
          if (!user.getLiked_notes().contains(note.getId())) {
            JSONObject jsonObj = new JSONObject();
            try {
              jsonObj.put("uid", localParent.getUserId());
              jsonObj.put("nid", note.getId());

            } catch (JSONException e) {
              e.printStackTrace();
            }
            VolleyUtilSingleton.getInstance(getActivity()).post(Utils.BASE_URL + "/note/like", jsonObj, Utils.genericSuccessListener, Utils.genericErrorListener);
            user.getLiked_notes().add(note.getId());
            user.updateUser(localParent);
            note.setLikes(note.getLikes() + 1);
            likes.setText("" + note.getLikes());
            noteListAdapter.updateList(notes);
            list_notes.setAdapter(noteListAdapter);
            likeBtn.setBackgroundResource(R.drawable.like_icon);
          }
        }
      });
    }
  }

  //all buttons listener
  public View.OnClickListener button1ClickListener = new View.OnClickListener() {
    @Override
    public void onClick(final View v) {

      //location filter
      if (locationFilterIsVisible) {
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

      //location filter
      if (locationFilterIsVisible) {
        locationFilterSelection = Utils.DISTANCE_MEDIUM;
      }

      //date filters
      else {
        dateFilterSelection = Utils.WEEK_MILI;
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

      //location filter
      if (locationFilterIsVisible) {
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


  //set main filter colors
  private void setButtonsColor() {

    Log.d(TAG, "setButtonsColor: start");
    //set date filter colors
    if (dateFilterIsVisible) {
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

    //set distance filter colors
    if (locationFilterIsVisible) {
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

  /**
   * Will update the notes within the list view
   */
  public void updateShowedNotes() {
    List<Note> presentedNotes = new ArrayList<>();
    long timeDifference;
    float distance;
    //get current date and location
    Location currLocation = new Location(gpsUtils.getLocation());
    Date now = new Date();
    Location targetLocation = new Location("");//provider name is unecessary
    Date targetDate;

    for (Note note : notes) {
      // get note location and date
      targetLocation.setLatitude(note.getLat());//your coords of course
      targetLocation.setLongitude(note.getLon());
      targetDate = new Date(note.getTimestamp());
      //get time and date differences
      timeDifference = now.getTime() - targetDate.getTime();
      distance = currLocation.distanceTo(targetLocation);
      //add to currently presented list according to filters.
      if (timeDifference <= dateFilterSelection
        && distance <= locationFilterSelection) {
        presentedNotes.add(note);
      }

    }
    noteListAdapter.updateList(presentedNotes);
    list_notes.setAdapter(noteListAdapter);
  }


}
