package com.android_app.matan.ara.sagi.thesocialnotework;

import android.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.MarkerOptions;

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
  private SettingsFragment settingsFragment;
  private ExploreFragment exploreFragment;
  private Toolbar toolbar;
  public static final String BASE_URL = "http://thesocialnotework-api.appspot.com/api";
  private ImageView menu_avatar;
  private MainActivity self;
  private NavigationView nav_view;
  private final int FINE_PERM = 0, CAMERA_PERM = 1;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    this.self = this;
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle("Personal Notes");
    setSupportActionBar(toolbar);
    gmapFragment = new GmapFragment();
    personalFragment = new PersonalFragment();
    settingsFragment = new SettingsFragment();
    exploreFragment = new ExploreFragment();
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
    this.user = new User(b.getString("UserData"));

    //Get The Nav_View Avatar View
    nav_view = (NavigationView) findViewById(R.id.nav_view);
    View header_v = nav_view.getHeaderView(0);
    menu_avatar = (ImageView) header_v.findViewById(R.id.nav_user_avatar);


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

    //Change The Avatar
    Utils.URLtoImageView(menu_avatar, user.getAvatar());
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

    Log.d(TAG, "onOptionsItemSelected: id -> "+id);
    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      toolbar.setTitle("Settings");
      setSupportActionBar(toolbar);
      ft.replace(R.id.fragment_container, settingsFragment);
      ft.commit();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

    int id = item.getItemId();

    if (id == R.id.nav_explore) {
      toolbar.setTitle("Explore");
      setSupportActionBar(toolbar);
      Log.d(TAG, "Before going to Explore");
      ft.replace(R.id.fragment_container, exploreFragment);
      ft.commit();
    } else if (id == R.id.nav_map) {

      Log.d(TAG, "Before going to map");
      toolbar.setTitle("Map");
      setSupportActionBar(toolbar);
      ft.replace(R.id.fragment_container, gmapFragment);
      ft.commit();
    } else if (id == R.id.nav_personal) {
      toolbar.setTitle("Personal Notes");
      setSupportActionBar(toolbar);
      Log.d(TAG, "Before going to personal");
      ft.replace(R.id.fragment_container, personalFragment);
      ft.commit();
    } else if (id == R.id.nav_settings) {
      toolbar.setTitle("Settings");
      setSupportActionBar(toolbar);
      ft.replace(R.id.fragment_container, settingsFragment);
      ft.commit();
    } else if (id == R.id.nav_logout) {

      try {
        Utils.removeUserDataFromPrefs();
      } catch (Exception e) {
        e.printStackTrace();
      }
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


  public User getUser() {
    return user;
  }

  public String getUserId() {
    return user.getId();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    Log.d(TAG, "onRequestPermissionsResult: in func");
    switch (requestCode) {
      case FINE_PERM: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 2) {
          if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Log.d(TAG, "onRequestPermissionsResult: Did Not get permission for location");
            Toast.makeText(MainActivity.this, "No Location Permissions granted.\n\"An App is nothing without its permissions\"", Toast.LENGTH_LONG).show();
            System.exit(0);

          }

          if (!(grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            Log.d(TAG, "onRequestPermissionsResult:DIDNT  get permission for camera");
            Toast.makeText(MainActivity.this, "No Camera Permissions granted.\nyou will not be able to change avatar", Toast.LENGTH_LONG).show();
            Utils.setCameraPermission(false);
          } else {
            Utils.setCameraPermission(true);
          }

          if (grantResults[2] != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onRequestPermissionsResult:DIDNT get READ\\WRITE External storage  permission");
            Toast.makeText(MainActivity.this, "No READ\\WRITE External storage  Permissions granted.\nyou will not be able to change avatar", Toast.LENGTH_LONG).show();
            Utils.setCameraPermission(false);
          } else {
            Utils.setCameraPermission(true);
          }
          return;
        }
      }
    }

  }

  public void updateNavAvatar() {
    //Change The Avatar
    Log.d(TAG, "updateNavAvatar: PINPINPINPIN");
    Utils.URLtoImageView(menu_avatar, user.getAvatar());
    Toast.makeText(this, "Profile Updated!", Toast.LENGTH_LONG).show();

  }
}



