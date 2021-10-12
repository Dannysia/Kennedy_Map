package edu.onu.kennedy_map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ViewReservationsScreen extends AppCompatActivity {

    AbstractUser authenticatedUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_reservations_screen);
        authenticatedUser = (AbstractUser) getIntent().getSerializableExtra("user");
        setTitle("Your Reservations:          ");
        RecyclerView recyclerView = findViewById(R.id.viewUserReservationsRecyclerView);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getIntent(),this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        // Now make a request and get the list of all the users reservations
        //TODO put this all in DatabaseHelper
        String authenticationEndpoint = APIRequestQueue.getInstance(this).getENDPOINT()+"/reservations/user/"+
                ((AbstractUser)getIntent().getSerializableExtra("user")).getUserID(); // 1. Endpoint

        ArrayList<ReservationHolder> reservationHolders = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, authenticationEndpoint, null,
                        response -> {
                            try{
                                //TODO Change name of JSON pair to whatever danny makes it
                                JSONArray userReservations = response.getJSONArray("currentUserReservations");
                                for (int i = 0; i<userReservations.length(); i++){
                                    JSONObject reservation = userReservations.getJSONObject(i);
                                    ReservationHolder reservationHolder = new ReservationHolder();
                                    reservationHolder.setReservationIDRoomIDUserIDStartTimeEndTime(reservation.getInt("reservationID"),
                                            reservation.getString("roomID"),reservation.getInt("userID"),
                                            reservation.getString("start_time"),reservation.getString("end_time"));
                                    reservationHolders.add(reservationHolder);
                                }
                                ReservationHolder[] reservationHolders1 = new ReservationHolder[reservationHolders.size()];
                                for(int i =0; i<reservationHolders1.length; i++){
                                    reservationHolders1[i] = reservationHolders.get(i);
                                }
                                System.out.println("Recieved Content: "+userReservations);
                                recyclerViewAdapter.setAllUserReservations(reservationHolders1);
                            }catch(Exception ignored){ }
                        }, error -> { System.out.println("Error "+error.toString());
                });
        //TODO pop up waiting symbol, until the response is received.

        // Add the request to the queue, which will complete eventually
        APIRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void returnToReservationScreenButton(View view){
        Intent intent1 = new Intent(this, ReservationScreen.class);
        intent1.putExtra("user",authenticatedUser);
        startActivity(intent1);
    }


}