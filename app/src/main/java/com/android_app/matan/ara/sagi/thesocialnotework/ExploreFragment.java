package com.android_app.matan.ara.sagi.thesocialnotework;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {

  private static final String TAG = "[TSN/Explore]";
  protected User user;
  protected MainActivity parent;
  private ListAdapter noteListAdapter;
  private List<Note> notes;
  protected ListView list_notes;

  private ImageButton dateFilter;
  private ImageButton locationFilter;
  private ImageButton userFilter;
  private Button map_small_filter;
  private Button map_medium_filter;
  private Button map_large_filter;
  private LinearLayout exploreFilters;
  private boolean dateFilterIsVisible = false;
  private boolean locationFilterIsVisible = false;
  private boolean userFilterIsVisible = false;

  private final String day = "24 hours";
  private final String week = "Week";
  private final String month = "Month";
  private final String hundredMeters = "100 meters";
  private final String kilometer = "1 Km";
  private final String threeKilometer = "3 Km";
  private final String mine = "Mine";
  private final String others = "Others";
  private final String all = "All";

  public ExploreFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_explore, container, false);

    parent = (MainActivity)getActivity();
    user = parent.getUser();
    notes = new ArrayList<>();
    //Get Views
    list_notes = (ListView)view.findViewById(R.id.list_notes);
    noteListAdapter = new ListAdapter(parent, notes);


    dateFilter = (ImageButton) view.findViewById(R.id.explore_date_filter);
    locationFilter = (ImageButton) view.findViewById(R.id.explore_location_filter);
    userFilter = (ImageButton) view.findViewById(R.id.explore_user_filter);

    map_small_filter = (Button) view.findViewById(R.id.explore_small_filter);
    map_medium_filter = (Button) view.findViewById(R.id.explore_medium_filter);
    map_large_filter = (Button) view.findViewById(R.id.explore_large_filter);

    exploreFilters = (LinearLayout) view.findViewById(R.id.explore_filter_options);

    dateFilter.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
//                Log.d(TAG, "" + v.getId());
        if (dateFilterIsVisible) {
          dateFilterIsVisible = false;
          exploreFilters.setVisibility(View.GONE);
        } else {
          exploreFilters.setVisibility(View.VISIBLE);
          dateFilterIsVisible = true;
          locationFilterIsVisible = false;
          userFilterIsVisible = false;

          // set text button in the right filter string
          map_small_filter.setText(day);
          map_medium_filter.setText(week);
          map_large_filter.setText(month);
        }
      }
    });

    locationFilter.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (locationFilterIsVisible) {
          locationFilterIsVisible = false;
          exploreFilters.setVisibility(View.GONE);
        } else {
          exploreFilters.setVisibility(View.VISIBLE);
          locationFilterIsVisible = true;
          dateFilterIsVisible = false;
          userFilterIsVisible = false;

          // set text button in the right filter string
          map_small_filter.setText(hundredMeters);
          map_medium_filter.setText(kilometer);
          map_large_filter.setText(threeKilometer);
        }
      }
    });

    userFilter.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (userFilterIsVisible) {
          userFilterIsVisible = false;
          exploreFilters.setVisibility(View.GONE);
        } else {
          exploreFilters.setVisibility(View.VISIBLE);
          userFilterIsVisible = true;
          dateFilterIsVisible = false;
          locationFilterIsVisible = false;

          // set text button in the right filter string
          map_small_filter.setText(mine);
          map_medium_filter.setText(others);
          map_large_filter.setText(all);
        }
      }
    });

    // TODO: choose a default filter for openning explore mode

    try {
      getAllNotes();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return view;
  }



  public void getAllNotes() throws JSONException {
    Utils.showLoadingDialog(parent, "Exploring...", "Finding some new interesting notes just for you");
    Log.d(TAG, "url: " + Utils.BASE_URL + "/note/getPublic");
    String url = Utils.BASE_URL + "/note/getPublic";
    JSONObject payload = new JSONObject();
    payload.put("id", user.getId());
    VolleyUtilSingleton.getInstance(getActivity()).post(url, payload ,getNotesSuccessListener,Utils.genericErrorListener);
  }

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
        list_notes.setAdapter(noteListAdapter);
        Utils.dismissLoadingDialog();
      } catch (Exception e) {
        Log.e(TAG, "newNoteSuccess:" + e.getMessage());
      }

    }
  };

}
