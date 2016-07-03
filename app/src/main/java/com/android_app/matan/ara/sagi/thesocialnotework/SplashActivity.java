package com.android_app.matan.ara.sagi.thesocialnotework;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    private ImageView appSplashScreen;
    private int timerDelay = 3000;
    private final String TAG = "Splash Screen Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(timerDelay);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}
