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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String LOCAL_DATA_TSN = "TSN_DATA_STORE";
    protected final String TAG = "[TSN / MainActivity]";
    protected String userId;
    private GPSUtils gpsUtils;
    private boolean locationPermission;
    public static ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        PersonalFragment personalFragment = new PersonalFragment();
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_explore) {
            // Handle the camera action
        } else if (id == R.id.nav_map) {

        } else if (id == R.id.nav_personal) {

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



}
