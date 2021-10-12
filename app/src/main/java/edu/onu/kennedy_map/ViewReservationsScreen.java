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
        DatabaseManager.getInstance().viewReservationPagePopulateRecyclerView(this,
                (RegisteredUser) authenticatedUser,recyclerViewAdapter);
    }

    public void returnToReservationScreenButton(View view){
        Intent intent1 = new Intent(this, ReservationScreen.class);
        intent1.putExtra("user",authenticatedUser);
        startActivity(intent1);
    }


}