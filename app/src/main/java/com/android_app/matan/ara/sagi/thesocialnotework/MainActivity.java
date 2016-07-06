package com.android_app.matan.ara.sagi.thesocialnotework;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected static final String TAG = "[TSN / MainActivity]";
    protected String userId;
    private GPSUtils gpsUtils;
    private boolean locationPermission;
    public static ProgressDialog progress;
    private GmapFragment gmapFragment;
    private PersonalFragment personalFragment;
    public static final String BASE_URL = "http://thesocialnotework-api.appspot.com/api";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gmapFragment = new GmapFragment();
        personalFragment = new PersonalFragment();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This should Open the new Note thingy", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //get Bundle data (Userid)
        Bundle b = getIntent().getExtras();
        userId = b.getString("user_id");

        //Change Layout
        Log.d(TAG, "Changing Fragment to Personal Activity");
//        PersonalFragment personalFragment = new PersonalFragment();
        personalFragment.setArguments(b);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, personalFragment);
        ft.commit();
        Log.d(TAG, "Changed");

        gpsUtils = new GPSUtils(this);
        gpsUtils.getLocation();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle b = new Bundle();

        int id = item.getItemId();

        if (id == R.id.nav_explore) {
            // Handle the camera action
        } else if (id == R.id.nav_map) {
//
            Log.d(TAG,"Before going to map");
//            gmapFragment.("note_list", (ArrayList<Note>) listOfNotes);
//            gmapFragment.put("user_lat", gpsUtils.getLatitude());
//            gmapFragment.putExtra("user_lng", gpsUtils.getLongitude());
            ft.replace(R.id.fragment_container, gmapFragment);
            ft.commit();
        } else if (id == R.id.nav_personal) {

            Log.d(TAG,"Before going to personal");
            ft.replace(R.id.fragment_container, personalFragment);
            ft.commit();
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public GPSUtils getGPSUtils() {
        return this.gpsUtils;
    }

    public void setLocationPermission(boolean locationPermission) {
        this.locationPermission = locationPermission;
    }





    public static void showLoadingDialog(Context context, String title, String msg) {
        progress = new ProgressDialog(context);
        progress.setTitle(title);
        progress.setMessage(msg);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    public static void dismissLoadingDialog() {

        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    //    //Generic response ErrorListener
    Response.ErrorListener genericErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d(TAG, "genericErrorListener");
            error.printStackTrace();
        }
    };

        public Note getNoteFromJsonObj(JSONObject noteObject, Date time) throws JSONException {
//            List<Note> listOfNotes = new ArrayList<>();

            Note note = new Note(
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
            return note;
//        listOfNotes.add(addNote);

    }

    public ArrayList<String> jsonArrayToStringArray(JSONArray jArray) {
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

    public String getUserId(){return userId;}

}
