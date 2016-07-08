package com.android_app.matan.ara.sagi.thesocialnotework;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by JERLocal on 7/7/2016.
 */
public class Utils {

    public static final String TAG = "Utils";
    public static final String BASE_URL = "http://thesocialnotework-api.appspot.com/api";
    public static ProgressDialog progress;


    public static Bitmap getBitmapFromURL(String imageUrl) {

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    //    //Generic response ErrorListener
    public static Response.ErrorListener genericErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d(TAG, "genericErrorListener");
            error.printStackTrace();
        }
    };

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


    public static Note getNoteFromJsonObj(JSONObject noteObject, Date time) throws JSONException {
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
                noteObject.getString("avatar"),
                jsonArrayToStringArray(noteObject.getJSONArray("tags"))
        );
        return note;
//        listOfNotes.add(addNote);

    }

    public static ArrayList<String> jsonArrayToStringArray(JSONArray jArray) {
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

  public static void URLtoImageView(ImageView iv, String url){
    new setUserAvatar(iv, url).execute();
  }

  private static class setUserAvatar extends AsyncTask<Void, Void, Bitmap> {
    private ImageView iv;
    private String url;
    public setUserAvatar(ImageView imageView, String url){
      this.iv = imageView;
      this.url = url;
    }

    @Override
    protected Bitmap doInBackground(Void... v) {
      Bitmap b = Utils.getBitmapFromURL(url);
      return b;
    }

    @Override
    protected void onPostExecute(Bitmap b){
      iv.setImageBitmap(b);
    }

  }
}
