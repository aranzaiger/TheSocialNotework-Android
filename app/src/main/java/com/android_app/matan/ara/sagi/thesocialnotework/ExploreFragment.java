package com.android_app.matan.ara.sagi.thesocialnotework;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

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
//    private ImageButton userFilter;
    private Button map_small_filter;
    private Button map_medium_filter;
    private Button map_large_filter;
    private LinearLayout exploreFilters;
    private boolean dateFilterIsVisible = false;
    private boolean locationFilterIsVisible = false;
//    private boolean userFilterIsVisible = false;

    private final String day = "24 hours";
    private final String week = "Week";
    private final String month = "Month";
    private final String hundredMeters = "100 meters";
    private final String kilometer = "1 Km";
    private final String threeKilometer = "3 Km";
//    private final String mine = "Mine";
//    private final String others = "Others";
//    private final String all = "All";

    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        parent = (MainActivity) getActivity();
        user = parent.getUser();
        notes = new ArrayList<>();
        //Get Views
        list_notes = (ListView) view.findViewById(R.id.list_notes);
        noteListAdapter = new ListAdapter(parent, notes);
        list_notes.setOnItemClickListener(new ItemClickedListener());


        dateFilter = (ImageButton) view.findViewById(R.id.explore_date_filter);
        locationFilter = (ImageButton) view.findViewById(R.id.explore_location_filter);

        map_small_filter = (Button) view.findViewById(R.id.explore_small_filter);
        map_medium_filter = (Button) view.findViewById(R.id.explore_medium_filter);
        map_large_filter = (Button) view.findViewById(R.id.explore_large_filter);

        exploreFilters = (LinearLayout) view.findViewById(R.id.explore_filter_options);

        dateFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateFilterIsVisible) {
                    dateFilterIsVisible = false;
                    exploreFilters.setVisibility(View.GONE);
                } else {
                    exploreFilters.setVisibility(View.VISIBLE);
                    dateFilterIsVisible = true;
                    locationFilterIsVisible = false;
//                    userFilterIsVisible = false;

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
//                    userFilterIsVisible = false;

                    // set text button in the right filter string
                    map_small_filter.setText(hundredMeters);
                    map_medium_filter.setText(kilometer);
                    map_large_filter.setText(threeKilometer);
                }
            }
        });

//        userFilter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (userFilterIsVisible) {
//                    userFilterIsVisible = false;
//                    exploreFilters.setVisibility(View.GONE);
//                } else {
//                    exploreFilters.setVisibility(View.VISIBLE);
//                    userFilterIsVisible = true;
//                    dateFilterIsVisible = false;
//                    locationFilterIsVisible = false;
//
//                    // set text button in the right filter string
//                    map_small_filter.setText(mine);
//                    map_medium_filter.setText(others);
//                    map_large_filter.setText(all);
//                }
//            }
//        });

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
        VolleyUtilSingleton.getInstance(getActivity()).post(url, payload, getNotesSuccessListener, Utils.genericErrorListener);
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


    // click on listView item
    class ItemClickedListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            //create and configure dialog
            final Note note = notes.get(position);
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
            final TextView permission = (TextView) noteViewDialog.findViewById(R.id.ndf_permission_textview);
            final ImageButton likeBtn = (ImageButton) noteViewDialog.findViewById(R.id.ndf_delete_imagebutton);
            final ImageView avatar = (RoundAvatarImageView) noteViewDialog.findViewById(R.id.note_user_avatar);
            final ImageView permission_image = (ImageView) noteViewDialog.findViewById(R.id.permission_image);

            title.setText(note.getTitle());
            body.setText(note.getBody());
            date.setText(note.getDate());
            time.setText(note.getTime());
            location.setText(note.getAddress());
            if (likes != null) likes.setText("" + note.getLikes());
            likeBtn.setBackgroundResource(R.drawable.like_icon);
//            tags.setText("Tags: "+ note.getTags().toString());
//      permission.setText("" + (note.isPublic() ? "Public" : "Private"));
            permission.setVisibility(View.GONE);
            Utils.URLtoImageView(avatar, note.getAvatar());
            permission_image.setVisibility(View.GONE);

            likeBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
//          //Put up the Yes/No message box
//          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//          builder
//                  .setTitle("Delete Note")
//                  .setMessage("Are you sure you want to delete the note?")
//                  .setIcon(android.R.drawable.ic_dialog_alert)
//                  .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                      //Yes button clicked, do something
//                      Toast.makeText(getActivity(), "Item Deleted!",
//                              Toast.LENGTH_SHORT).show();
//                      //TODO send delete
//                      JSONObject delNote = new JSONObject();
//                      try {
//                        delNote.put("uid", userId);
//                        delNote.put("nid", note.getId());
//                        VolleyUtilSingleton.getInstance(getActivity()).post(BASE_URL + "/note/delete", delNote, Utils.deleteNoteSuccessListener, Utils.genericErrorListener);
//                        listOfNotes.remove(presentedNotes.get(position));
//                        presentedNotes.remove(position);
//
//                      } catch (JSONException e) {
//                        Toast.makeText(getActivity(), "Something went wrong.\n Failed to delete note...", Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                      }
//                      updateShowedNotes();
////                                    noteList.setAdapter(noteListAdapter);
//                      noteViewDialog.dismiss();
//                    }
//                  })
//                  .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                      //Yes button clicked, do something
//                      Toast.makeText(getActivity(), "Canceled",
//                              Toast.LENGTH_SHORT).show();
//                      noteViewDialog.dismiss();
//                    }
//                  })                        //Do nothing on no
//                  .show();
//        }
//      });

                }
            });
        }
    }


}
