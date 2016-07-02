package com.android_app.matan.ara.sagi.thesocialnotework;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by JERLocal on 7/2/2016.
 */
public class HeavyWorker extends AsyncTask< String , Context , Void > {

    private ProgressDialog progressDialog ;
    private Context targetCtx ;

    public HeavyWorker ( Context context ) {
        this.targetCtx = context ;
        progressDialog = new ProgressDialog ( targetCtx ) ;
        progressDialog.setCancelable ( false ) ;
        progressDialog.setMessage ( "Retrieving data..." ) ;
        progressDialog.setTitle ( "Please wait" ) ;
        progressDialog.setIndeterminate ( true ) ;
    }

    @ Override
    protected void onPreExecute ( ) {
        progressDialog.show ( ) ;
    }

    @ Override
    protected Void doInBackground ( String ... params ) {
        // Do Your WORK here

        PersonalSpaceActivity ps = (PersonalSpaceActivity)targetCtx;
        ps.getAllNotes();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return null ;
    }

    @ Override
    protected void onPostExecute ( Void result ) {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss ( ) ;
        }
    }
}
