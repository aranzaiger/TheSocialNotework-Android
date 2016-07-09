package com.android_app.matan.ara.sagi.thesocialnotework;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
