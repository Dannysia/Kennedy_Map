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

@SuppressWarnings("unchecked")
public class ViewReservationsScreen extends AppCompatActivity {

    AbstractUser authenticatedUser;
    ArrayList<Room> allRooms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_reservations_screen);
        authenticatedUser = (AbstractUser) getIntent().getSerializableExtra("user");
        allRooms = (ArrayList<Room>)getIntent().getSerializableExtra("rooms");
        setTitle("Your Reservations:          ");
        RecyclerView recyclerView = findViewById(R.id.viewUserReservationsRecyclerView);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getIntent(),this,authenticatedUser,allRooms);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        //TODO make list item disappear when reservation is canceled

        // Now make a request and get the list of all the users reservations
        DatabaseManager.getInstance().viewReservationPagePopulateRecyclerView(this,
                (RegisteredUser) authenticatedUser,recyclerViewAdapter,allRooms);
    }

    public void returnToReservationScreenButton(View view){
        Intent intent1 = new Intent(this, ReservationScreen.class);
        intent1.putExtra("user",authenticatedUser);
        intent1.putExtra("rooms",allRooms);
        startActivity(intent1);
    }


}