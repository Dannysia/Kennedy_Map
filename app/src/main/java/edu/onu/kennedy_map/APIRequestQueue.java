package edu.onu.kennedy_map;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * APIRequestQueue is required to send out http requests using Volley.
 */
public class APIRequestQueue {
    private static APIRequestQueue instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    /**
     * Singleton set-up constructor, requires application context
     * @param context Application context
     */
    private APIRequestQueue(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    /**
     * Getter function for the single instance of the singleton, lazy instantiation
     * @param context Application context
     * @return The instance of the APIRequestQueue
     */
    public static synchronized APIRequestQueue getInstance(Context context) {
        if (instance == null) {
            instance = new APIRequestQueue(context);
        }
        return instance;
    }

    /**
     * Used to get the current request queue, lazy instantiation of a Volley request queue
     * @return A request queue for Volley communication
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * Adds a new request to the Volley request queue, will be sent out at discretion of the OS.
     * @param req The request to be sent
     * @param <T> The type of the object to be sent, in our case it is JSON
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
