package com.android_app.matan.ara.sagi.thesocialnotework;
import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by aranz on 7/1/2016.
 */
public class VolleyUtilSingleton {

    private static VolleyUtilSingleton mInstance;
    private RequestQueue mRequestQueue;
//    private ImageLoader mImageLoader;
    private static Context mCtx;


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

}
