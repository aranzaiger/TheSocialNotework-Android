package com.android_app.matan.ara.sagi.thesocialnotework;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aranz on 7/1/2016.
 */
public class VolleyUtilSingleton {

    private static VolleyUtilSingleton mInstance;
    private RequestQueue mRequestQueue;
//    private ImageLoader mImageLoader;
    private static Context mCtx;
    private final String TAG = "VolleyUtilSingleton";


    private VolleyUtilSingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

//        mImageLoader = new ImageLoader(this.mRequestQueue,new LruBitmapCache());
    }

    public static synchronized VolleyUtilSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyUtilSingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

//    public ImageLoader getImageLoader() {
//        return mImageLoader;
//    }

    public void newUser(String url, JSONObject body) {
        JsonObjectRequest request =
                new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        body,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String s = "";

                                try {
                                    s= response.getString("id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d(TAG, "newNoteSuccess: response - " + response.toString());
                                Log.d(TAG, "newNoteSuccess: id response - " + s);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "error: msg: " + error.getMessage());
                            }
                        }
                );
        addToRequestQueue(request);
    }

    public void post(String url, JSONObject body, Response.Listener<JSONObject> successFunction, Response.ErrorListener errorFunction) {
        JsonObjectRequest request =
                new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        body,
                        successFunction,
                        errorFunction
                );
        addToRequestQueue(request);
    }

}
