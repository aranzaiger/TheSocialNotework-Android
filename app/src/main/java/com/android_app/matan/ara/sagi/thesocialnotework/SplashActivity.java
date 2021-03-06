package com.android_app.matan.ara.sagi.thesocialnotework;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import java.util.Random;


public class SplashActivity extends AppCompatActivity {

    private ImageView background;
    private int timerDelay = 3500;

    /**
     * called when this class is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        background = (ImageView) findViewById(R.id.background);

        final int sdk = android.os.Build.VERSION.SDK_INT; // holds the sdk
        // Configuring the method to use considering the sdk
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            background.setImageDrawable( getResources().getDrawable(rand_splash()) );
        } else {
            background.setImageDrawable( getResources().getDrawable(rand_splash()));
        }

        final String userData = Utils.getUserFromSharedPrefs(this);

        // Timer for the splash image until it will disappear
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(timerDelay);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    if(userData == null){
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }else{
                        Intent personalSpaceActivity = new Intent(SplashActivity.this, MainActivity.class);
                        Bundle loginUserBundle = new Bundle();
                        loginUserBundle.putString("UserData", userData);
                        personalSpaceActivity.putExtras(loginUserBundle);
                        startActivity(personalSpaceActivity);
                    }
                    finish();
                }
            }
        };
        timerThread.start();
    }

    /**
     * on pause function
     */
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    /**
     * randomize splash screen
     * @return
     */
    protected int rand_splash() {
        int min = 2, max = 4;
        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;
        switch (randomNum){
            case 2:
                return R.drawable.splash_2;
            case 3:
                return R.drawable.splash_3;
            default:
                return R.drawable.splash_4;

        }
    }
}
