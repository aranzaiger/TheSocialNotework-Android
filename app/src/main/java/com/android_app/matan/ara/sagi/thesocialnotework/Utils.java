package com.android_app.matan.ara.sagi.thesocialnotework;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Environment;
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
import java.util.HashMap;

/**
 * Created by JERLocal on 7/7/2016.
 */
public class Utils {

    public static final String TAG = "Utils";
    public static final String BASE_URL = "http://thesocialnotework-api.appspot.com/api", UPLOAD_IMAGE_PATH = "/file/upload";
    public static ProgressDialog progress;
    private static HashMap<String, Bitmap> bitmapHash = new HashMap<>();
    public static final String PHOTOS_DIR_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/TheSocialNotework/";

    private static boolean mLocationPermission = false;
    private static boolean mCameraPermission = false;
    private static SharedPreferences prefs;
    public static int filterColor = Color.parseColor("#33adff"), circleColor = 0x6666a3ff;

    public static final long DAY_MILI = 86400000L, WEEK_MILI = 604800000L, MONTH_MILI = 2592000000L;
    public static final float DISTANCE_SMALL = 1000, DISTANCE_MEDIUM = 10000, DISTANCE_LONG = 100000;


    /**
     * gets the bitmap from the url
     *
     * @param url
     * @return
     */
    public static Bitmap getBitmapFromURL(String url) {
        if (Utils.bitmapHash.containsKey(url)) {
            Log.d(TAG, "getBitmapFromURL: Found is hash");
            return bitmapHash.get(url);
        } else {
            Log.d(TAG, "getBitmapFromURL: New value to HashMap");
            try {
                URL new_url = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) new_url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                bitmapHash.put(url, myBitmap);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

    /**
     * Generic response ErrorListener
     */
    public static Response.ErrorListener genericErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d(TAG, "genericErrorListener");
            error.printStackTrace();
            Utils.dismissLoadingDialog();
        }
    };
    /**
     * Generic response deleteNoteSuccessListener
     */
    public static Response.Listener<JSONObject> deleteNoteSuccessListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d(TAG, "deleteNoteSuccessListener: " + response.toString());
        }
    };

    /**
     * response listener for getting all user notes
     */
    public static Response.Listener<JSONObject> genericSuccessListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d(TAG, "likeNotesSuccessListener: " + response.toString());
        }
    };

    /**
     * Show loading dialog
     *
     * @param context
     * @param title
     * @param msg
     */
    public static void showLoadingDialog(Context context, String title, String msg) {
        progress = new ProgressDialog(context);
        progress.setTitle(title);
        progress.setMessage(msg);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    /**
     * Stop loading dialog
     */
    public static void dismissLoadingDialog() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    /**
     * get all the notes from the object
     * @param noteObject
     * @param time
     * @return
     * @throws JSONException
     */
    public static Note getNoteFromJsonObj(JSONObject noteObject, Date time) throws JSONException {
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
                noteObject.getString("owner_id"),
                jsonArrayToStringArray(noteObject.getJSONArray("tags"))
        );
        return note;
    }

    /**
     * convert json array to string array
     * @param jArray
     * @return
     */
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

    /**
     * convert url to image view
     * @param iv
     * @param url
     */
    public static void URLtoImageView(ImageView iv, String url) {
        if (bitmapHash.containsKey(url)) {
            iv.setImageBitmap(bitmapHash.get(url));
        } else {
            new setUserAvatar(iv, url).execute();
        }
    }

    /**
     * sets the user avatar
     */
    private static class setUserAvatar extends AsyncTask<Void, Void, Bitmap> {
        private ImageView iv;
        private String url;

        public setUserAvatar(ImageView imageView, String url) {
            this.iv = imageView;
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Void... v) {
            return Utils.getBitmapFromURL(url);
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            iv.setImageBitmap(b);
        }
    }

    /**
     * gets a rounded bitmap
     * @param bitmap
     * @param pixels
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Log.d(TAG, "rounded bitmap");
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * set location permission
     * @param locationPermission
     */
    public static void setLocationPermission(boolean locationPermission) {
        mLocationPermission = locationPermission;
    }

    /**
     * set camera permission
     * @param cameraPermission
     */
    public static void setCameraPermission(boolean cameraPermission) {
        mCameraPermission = cameraPermission;
    }

    /**
     * check for permissions
     * @return
     */
    public static boolean arePermissionsGranted() {
        return (mLocationPermission && mCameraPermission);
    }

    /**
     * checks for camera permission
     * @return
     */
    public static boolean isCameraPermissionGranted() {
        return mCameraPermission;
    }

    /**
     * checks for location permission
     * @return
     */
    public static boolean isLocationPermissionGranted() {
        return mLocationPermission;
    }

    /**
     * get user from shared prefs
     * @param contexst
     * @return
     */
    public static String getUserFromSharedPrefs(Context contexst) {
        if (prefs == null) {
            prefs = contexst.getSharedPreferences(MainActivity.LOCAL_DATA_TSN, Context.MODE_PRIVATE);
        }
        return prefs.getString("UserData", null);
    };

    /**
     * update user shared pref
     * @param data
     * @throws Exception
     */
    public static void updateUserSharedPref(String data) throws Exception {
        if (prefs == null) throw new Exception("Prefs are not available");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("UserData", data);
        editor.commit();
    }

    /**
     * remove user data
     * @throws Exception
     */
    public static void removeUserDataFromPrefs() throws Exception {
        if (prefs == null) throw new Exception("Prefs are not available");
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("UserData");
        editor.commit();
    }
}
