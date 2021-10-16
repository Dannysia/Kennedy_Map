package edu.onu.kennedy_map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerAdapterViewHolder>{
    private ReservationHolder[] allUserReservations;
    private Intent intent;
    private Activity screen;
    public RecyclerViewAdapter(Intent intent, Activity screen){
        this.intent=intent;
        this.screen=screen;
    }

    public static class RecyclerAdapterViewHolder extends RecyclerView.ViewHolder{
        public final TextView reservationViewRoomIDTextView;
        public final TextView reservationViewStartTime;
        public final TextView reservationViewEndTime;
        public final Button reservationViewCancelButton;

        public RecyclerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            this.reservationViewRoomIDTextView = itemView.findViewById(R.id.reservationViewRoomIDTextView);
            this.reservationViewStartTime = itemView.findViewById(R.id.reservationViewStartTime);
            this.reservationViewEndTime = itemView.findViewById(R.id.reservationViewEndTime);
            this.reservationViewCancelButton = itemView.findViewById(R.id.reservationViewCancelButton);
        }

        private void bind(ReservationHolder reservationHolder,Activity screen){
            int reservationID = reservationHolder.getReservationID();
            String roomID = reservationHolder.getRoomID();
            String startTime = reservationHolder.getStartTime();
            String endTime = reservationHolder.getEndTime();

            bindOrHideTextView(reservationViewRoomIDTextView,roomID);
            bindOrHideTextView(reservationViewStartTime,startTime);
            bindOrHideTextView(reservationViewEndTime,endTime);

            reservationViewCancelButton.setOnClickListener(listener ->{

                AlertDialog.Builder builder = new AlertDialog.Builder(screen);
                builder.setMessage("Do you want to cancel this reservation?");
                builder.setTitle("Cancel Reservation");
                // Add the buttons
                builder.setPositiveButton("Yes, Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked 'Yes, Cancel' button
                        //TODO put this all in DatabaseHelper
                        String authenticationEndpoint = "http://eccs3421.siatkosky.net:3421/reservations/delete"; // 1. Endpoint
                        JSONObject requestBody=null;
                        try {
                            requestBody = new JSONObject();
                            requestBody.put("reservationID", Integer.toString(reservationID));
                            System.out.println("Sent Content: "+requestBody.toString());
                        }catch (JSONException ignored){ }

                        AtomicBoolean reservationDeleted = new AtomicBoolean(false);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                (Request.Method.POST, authenticationEndpoint, requestBody,
                                        response -> {
                                            try{
                                                //TODO Change name of JSON pair to whatever danny makes it
                                                boolean reservationDeleteSuccess = response.getBoolean("reservationDeleted");
                                                reservationDeleted.set(reservationDeleteSuccess);
                                                System.out.println("Recieved Content: "+reservationDeleted.get());
                                            }catch(Exception e){
                                                reservationDeleted.set(false);
                                            }
                                            if(reservationDeleted.get()){
                                                Toast.makeText(screen, "Reservation Successfully Deleted", Toast.LENGTH_LONG).show();
                                            }
                                            else if(!reservationDeleted.get()) {
                                                Toast.makeText(screen, "Reservation Unsuccessfully Deleted", Toast.LENGTH_LONG).show();
                                            }
                                        }, error -> { System.out.println("Error "+error.toString());
                                });
                        //TODO pop up waiting symbol, until the response is received.
                        // The code to show will be right here, but the the code to remove it will be in the listeners

                        // Add the request to the queue, which will complete eventually
                        APIRequestQueue.getInstance(screen.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }});
                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            });
        }

        /**
         * Hides the textView if no data is read in for it.
         * @param textView The TextView that might or might not be hidden
         * @param data The String to be written in, if it exists.
         * @return True if the textView is visible, false otherwise.
         */
        private boolean bindOrHideTextView(TextView textView, String data){
            if (data == null){
                //super.itemView.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                return false;
            }
            else{
                //if(!hideFlag){
                textView.setText(data);
                textView.setVisibility(View.VISIBLE);
                return true;
                //}
                //super.itemView.setVisibility(View.GONE);
                //return true;
            }
        }
    }

    // Required methods for RecyclerView

    @NonNull
    @Override
    public RecyclerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.reservation_entry, parent, false);
        return new RecyclerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterViewHolder holder, int position) {
        holder.bind(allUserReservations[position],this.screen);
    }

    @Override
    public int getItemCount() {
        if (allUserReservations == null) return 0;
        return allUserReservations.length;
    }

    public void setAllUserReservations(ReservationHolder[] allUserReservations){
        this.allUserReservations = allUserReservations;
        notifyDataSetChanged();
    }
}