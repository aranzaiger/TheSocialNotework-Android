package com.android_app.matan.ara.sagi.thesocialnotework;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String LOCAL_DATA_TSN = "TSN_DATA_STORE";
    protected final String TAG = "[TSN / MainActivity]";
    protected User user;
    private GPSUtils gpsUtils;
    private boolean locationPermission;
    private GmapFragment gmapFragment;
    private PersonalFragment personalFragment;
    private Toolbar toolbar;
    public static final String BASE_URL = "http://thesocialnotework-api.appspot.com/api";
    private ImageView menu_avatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Personal Notes");
        setSupportActionBar(toolbar);
        gmapFragment = new GmapFragment();
        personalFragment = new PersonalFragment();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //get Bundle data (UserString)
        Bundle b = getIntent().getExtras();
        this.user  = new User(b.getString("UserData"));
        menu_avatar = (ImageView)findViewById(R.id.user_avatar);
        //TODO - Change the menu_avatar to user.getAvatar()


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
            toolbar.setTitle("Explore");
            setSupportActionBar(toolbar);
        } else if (id == R.id.nav_map) {

            Log.d(TAG,"Before going to map");
            toolbar.setTitle("Map");
            setSupportActionBar(toolbar);
            ft.replace(R.id.fragment_container, gmapFragment);
            ft.commit();
        } else if (id == R.id.nav_personal) {

            Log.d(TAG,"Before going to personal");
            ft.replace(R.id.fragment_container, personalFragment);
            ft.commit();
        } else if (id == R.id.nav_settings) {
            toolbar.setTitle("Settings");
            setSupportActionBar(toolbar);
        } else if (id == R.id.nav_logout) {

            SharedPreferences sharedPref = this.getSharedPreferences(MainActivity.LOCAL_DATA_TSN, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("UserId");
            editor.commit();
            Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginActivity);
            finish();
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







  public User getUser(){
    return user;
  }

    public String getUserId(){return user.getId();}

}
