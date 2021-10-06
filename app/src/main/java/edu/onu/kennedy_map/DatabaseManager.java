package edu.onu.kennedy_map;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseManager {
    private static DatabaseManager databaseManager = null;
    private DatabaseManager(){}

    private final String ENDPOINT = "http://eccs3421.siatkosky.net:3421";

    public static DatabaseManager getInstance(){
        if(databaseManager==null){
            databaseManager = new DatabaseManager();
        }
        return databaseManager;
    }

    //TODO: Basically what we are going to do with these requests is store them in a singleton, then in the LoginHelper
    // look every second or so to see if the request was successful, else we will fail.

    /**
     * The authenticateUser function makes a POST request to our login endpoint in order to log the user in.
     * @param context Context required for android things
     * @param email String email of the user trying to login
     * @param password String password of the user trying to login
     */
    public void authenticateUser(Context context, String email, String password){
        String authenticationEndpoint = ENDPOINT+"/login"; // 1. Endpoint
        // Fill body of request
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password ", password);
        }catch (JSONException ignored){ }
        AtomicInteger test = new AtomicInteger();
        // TODO: figure out how I am going to get response out of the lambda
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, authenticationEndpoint, requestBody,
                        new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // We don't use userInfo for now
                        test.set(1);
                        test.set(4);
                        APIRequestQueue.getInstance(context).storeRegisteredUser(
                                new RegisteredUser(Integer.getInteger(response.toString()), null));
                    }
                    }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {// TODO: Handle error
                        }
                });
        // Add the request to the queue, which will complete eventually
        APIRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * The registerUser function registers a new user and raises a boolean in the APIRequestQueue signaling success.
     * @param context Context required for android things
     * @param email String email of the user trying to register
     * @param password String password of the user trying to register
     * @param first_name String first name of the user trying to register
     * @param last_name String first name of the user trying to register
     */
    public void registerUser(Context context, String email, String password, String first_name, String last_name){
        String authenticationEndpoint = ENDPOINT+"/register"; // 1. Endpoint
        // Fill body of request
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password ", password);
            requestBody.put("first_name", first_name);
            requestBody.put("last_name ", last_name);
        }catch (JSONException ignored){ }
        // TODO: figure out how I am going to get response out of the lambda
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, authenticationEndpoint, requestBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                APIRequestQueue.getInstance(context).storeRegisterSuccess(response.toString().contains("Successfully"));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {// TODO: Handle error
                    }
                });
        // Add the request to the queue, which will complete eventually
        APIRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }



}
