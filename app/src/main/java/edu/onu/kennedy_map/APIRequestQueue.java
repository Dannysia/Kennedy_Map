package edu.onu.kennedy_map;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class APIRequestQueue {
    private static APIRequestQueue instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    private RegisteredUser registeredUser;
    private Boolean registerSuccess;
    private final String ENDPOINT = "http://eccs3421.siatkosky.net:3421";

    private APIRequestQueue(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized APIRequestQueue getInstance(Context context) {
        if (instance == null) {
            instance = new APIRequestQueue(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    // Used to pass around the registered user, be careful of stale data
    public void storeRegisteredUser(RegisteredUser registeredUser){
        this.registeredUser = registeredUser;
    }
    // Stale data removal
    public RegisteredUser getRegisteredUser(){
        RegisteredUser tempRegUser = this.registeredUser;
        this.registeredUser = null;
        return tempRegUser;
    }

    // Used to find out if register was successful, be careful of stale data
    public void storeRegisterSuccess(boolean registerSuccess){
        this.registerSuccess = registerSuccess;
    }
    // Stale Data removal
    public boolean isRegisterSuccess() {
        boolean tempRegSucc = this.registerSuccess;
        this.registerSuccess = null;
        return tempRegSucc;
    }

    public String getENDPOINT(){
        return ENDPOINT;
    }
}
