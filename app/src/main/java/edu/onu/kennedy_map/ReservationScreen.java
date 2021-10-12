package edu.onu.kennedy_map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dpizarro.uipicker.library.picker.PickerUI;
import com.dpizarro.uipicker.library.picker.PickerUISettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReservationScreen extends AppCompatActivity {

    AbstractUser authenticatedUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authenticatedUser = (AbstractUser) getIntent().getSerializableExtra("user");
        Toast.makeText(ReservationScreen.this, ""+authenticatedUser.getUserID(), Toast.LENGTH_LONG).show();

        //TODO: Add drop down menu back so they can go back without having to click the back button
        setTitle("Reservation Screen            Options:");
        setContentView(R.layout.reservation_screen);
        //TODO put this all in DatabaseHelper
        String reservableRoomsEndpoint = APIRequestQueue.getInstance(this).getENDPOINT()+"/rooms/reservable"; // 1. Endpoint
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, reservableRoomsEndpoint, null,
                        response -> {
                            try{
                                //TODO Change name of JSON to whatever danny makes it
                                ArrayList<String> roomIDs = new ArrayList<>();
                                JSONArray roomIdsJSON = response.getJSONArray("reservableRooms");
                                for (int i = 0; i<roomIdsJSON.length(); i++){
                                    roomIDs.add((String)roomIdsJSON.get(i));
                                }
                                PickerUI roomPicker = (PickerUI)findViewById(R.id.picker_ui_view);
                                PickerUISettings pickerUISettings = new PickerUISettings.Builder()
                                        .withItems(roomIDs)
                                        .withAutoDismiss(false)
                                        .withItemsClickables(false)
                                        .withUseBlur(false)
                                        .build();

                                roomPicker.setSettings(pickerUISettings);
                            }catch(Exception e){
                                Toast.makeText(ReservationScreen.this, "Some error occured", Toast.LENGTH_LONG).show();
                            }
                        }, error -> { System.out.println("Error "+error.toString());
                });
        //TODO pop up waiting symbol, until the response is received.
        APIRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        private Button button;
        private TextView storageHour;
        private TextView storageMinute;
        public TimePickerFragment(Button button,TextView storageHour,TextView storageMinute){
            this.button = button;
            this.storageHour = storageHour;
            this.storageMinute = storageMinute;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }
        @SuppressLint("SetTextI18n")
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String minuteText;
            String hourText;
            if(minute<10){
                minuteText="0"+minute;
            }else{
                minuteText=Integer.toString(minute);
            }
            if(hourOfDay<10){
                hourText="0"+ hourOfDay;
            }else{
                hourText=Integer.toString(hourOfDay);
            }
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("HHmm");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            LocalTime convertToRealTime = LocalTime.from(inputFormatter.parse(""+hourText+minuteText));
            String realTime = outputFormatter.format(convertToRealTime);
            button.setText(realTime);
            storageHour.setText(""+hourText);
            storageMinute.setText(""+minuteText);
        }
    }
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private Button clickedButton;
        private TextView storageDay;
        private TextView storageMonth;
        private TextView storageYear;
        public DatePickerFragment(Button clickedButton,TextView storageDay,TextView storageMonth,TextView storageYear){
            this.clickedButton = clickedButton;
            this.storageDay = storageDay;
            this.storageMonth = storageMonth;
            this.storageYear = storageYear;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @SuppressLint("SetTextI18n")
        public void onDateSet(DatePicker view, int year, int month, int day) {
            clickedButton.setText(""+month+"/"+day+"/"+year);
            storageDay.setText(""+day);
            storageMonth.setText(""+month);
            storageYear.setText(""+year);
        }
    }
    public void startTimeButton(View view){
        Button startTimeButton = (Button)findViewById(R.id.startTimeButton);
        TextView startHour = (TextView)findViewById(R.id.startHour);
        TextView startMinute = (TextView)findViewById(R.id.startMinute);
        TimePickerFragment newFragment = new TimePickerFragment(startTimeButton,startHour,startMinute);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
    public void startDateButton(View view){
        Button startDateButton = (Button)findViewById(R.id.startDateButton);
        TextView startDay = (TextView)findViewById(R.id.startDay);
        TextView startMonth = (TextView)findViewById(R.id.startMonth);
        TextView startYear = (TextView)findViewById(R.id.startYear);
        DatePickerFragment newFragment = new DatePickerFragment(startDateButton,startDay,startMonth,startYear);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    public void endTimeButton(View view){
        Button endTimeButton = (Button)findViewById(R.id.endTimeButton);
        TextView endHour = (TextView)findViewById(R.id.endHour);
        TextView endMinute = (TextView)findViewById(R.id.endMinute);
        TimePickerFragment newFragment = new TimePickerFragment(endTimeButton,endHour,endMinute);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
    public void endDateButton(View view){
        Button endDateButton = (Button)findViewById(R.id.endDateButton);
        TextView endDay = (TextView)findViewById(R.id.endDay);
        TextView endMonth = (TextView)findViewById(R.id.endMonth);
        TextView endYear = (TextView)findViewById(R.id.endYear);
        DatePickerFragment newFragment = new DatePickerFragment(endDateButton,endDay,endMonth,endYear);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void selectRoomButton(View view){
        Button selectRoomButton = (Button) findViewById(R.id.selectRoomButton);
        PickerUI roomPicker = (PickerUI) findViewById(R.id.picker_ui_view);
        roomPicker.setOnClickItemPickerUIListener(new PickerUI.PickerUIItemClickListener() {
            @Override
            public void onItemClickPickerUI(int which, int position, String valueResult) {
                //TODO: Add so where it zooms in on the room based on the selection, gonna resize all the images to 1000x1000 to do this easy
                // Query the database for the reservations for the selected room
                // TODO put this all in DatabaseHelper
                String reservationsInRoomEndpoint = APIRequestQueue.getInstance(view.getContext()).getENDPOINT()+"/reservations/room/"+valueResult; // 1. Endpoint
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, reservationsInRoomEndpoint, null,
                                response -> {
                                    try{
                                        //Take the JSONArray, for each JSONObject of that array, form a string that will be printed
                                        //TODO Change name of JSON to whatever danny makes it
                                        ArrayList<String> finishedRoomStrings = new ArrayList<>();
                                        JSONArray currentRoomReservationsJSONArray = response.getJSONArray("currentRoomReservations");
                                        ArrayList<JSONObject> currentReservationsJSONObjects = new ArrayList<>();
                                        for(int i=0; i<currentRoomReservationsJSONArray.length(); i++){
                                            JSONObject currentRoomReservationJSONData = currentRoomReservationsJSONArray.getJSONObject(i);
                                            finishedRoomStrings.add(currentRoomReservationJSONData.get("StartTime")+" - "+currentRoomReservationJSONData.get("EndTime"));
                                        }
                                        TextView currentReservationsTextView = (TextView) findViewById(R.id.currentReservationsTextView);
                                        StringBuilder finishedRoomStringDisplay= new StringBuilder();
                                        for(String roomString : finishedRoomStrings){
                                            finishedRoomStringDisplay.append(roomString);
                                        }
                                        currentReservationsTextView.setText(finishedRoomStringDisplay.toString());
                                    }catch(Exception e){
                                        Toast.makeText(ReservationScreen.this, "Some error occurred", Toast.LENGTH_LONG).show();
                                    }
                                }, error -> { System.out.println("Error "+error.toString());
                        });
                //TODO pop up waiting symbol, until the response is received.
                selectRoomButton.setText(valueResult);
                APIRequestQueue.getInstance(view.getContext()).addToRequestQueue(jsonObjectRequest);

            }
        });
        roomPicker.slide(0);
    }

    public void reservationScreenRoomImageView(View view){
        PickerUI roomPicker = (PickerUI) findViewById(R.id.picker_ui_view);
        if(roomPicker.isPanelShown()){
            roomPicker.slide();
        }
    }
    public void selectARoomTextView(View view){
        PickerUI roomPicker = (PickerUI) findViewById(R.id.picker_ui_view);
        if(roomPicker.isPanelShown()){
            roomPicker.slide();
        }
    }
    public void mainReservationLinearLayout(View view){
        PickerUI roomPicker = (PickerUI) findViewById(R.id.picker_ui_view);
        if(roomPicker.isPanelShown()){
            roomPicker.slide();
        }
    }

    public void backToMenuButton(View view){
        Intent intent1 = new Intent(this, MenuScreen.class);
        intent1.putExtra("user",authenticatedUser);
        startActivity(intent1);
    }

    //TODO: Add all error checking here to make reservations, use authenticatedUser
    public void reserveConfirmButton(View view){
        //TODO put this all in DatabaseHelper
        String makeAReservationEndpoint = APIRequestQueue.getInstance(this).getENDPOINT()+"/reservations/create"; // 1. Endpoint
        JSONObject requestBody=null;
        try {
            requestBody = new JSONObject();
            Button selectRoomButton = (Button)findViewById(R.id.selectRoomButton);
            if(selectRoomButton.getText().toString().equals("")||
                    selectRoomButton.getText().toString().equalsIgnoreCase("SELECT A ROOM")){
                Toast.makeText(ReservationScreen.this, "Please select a room.", Toast.LENGTH_LONG).show();
                return;
            }
            requestBody.put("roomID", selectRoomButton.getText().toString());
            requestBody.put("ownerID", authenticatedUser.getUserID());

            DateTimeFormatter inputFormattedStart = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            DateTimeFormatter outputFormatterStart = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            TextView startYear = (TextView)findViewById(R.id.startYear);
            TextView startMonth= (TextView)findViewById(R.id.startMonth);
            TextView startDay  = (TextView)findViewById(R.id.startDay);
            TextView startHour  = (TextView)findViewById(R.id.startHour);
            TextView startMinute  = (TextView)findViewById(R.id.startMinute);
            String startSecond  = "00";
            LocalTime convertToRealTimeStart = LocalTime.from(inputFormattedStart.parse(startYear.getText().toString()+
                    startMonth.getText().toString()+startDay.getText().toString()+startHour.getText().toString()+
                    startMinute.getText().toString()+startSecond));
            String dateTimeFormatStart = outputFormatterStart.format(convertToRealTimeStart);
            requestBody.put("start_time",dateTimeFormatStart);

            DateTimeFormatter inputFormattedEnd = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            DateTimeFormatter outputFormatterEnd = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            TextView endYear = (TextView)findViewById(R.id.endYear);
            TextView endMonth= (TextView)findViewById(R.id.endMonth);
            TextView endDay  = (TextView)findViewById(R.id.endDay);
            TextView endHour  = (TextView)findViewById(R.id.endHour);
            TextView endMinute  = (TextView)findViewById(R.id.endMinute);
            String endSecond  = "00";
            LocalTime convertToRealTimeEnd = LocalTime.from(inputFormattedEnd.parse(endYear.getText().toString()+
                    endMonth.getText().toString()+endDay.getText().toString()+endHour.getText().toString()+
                    endMinute.getText().toString()+endSecond));
            String dateTimeFormatEnd = outputFormatterEnd.format(convertToRealTimeEnd);
            requestBody.put("end_time", dateTimeFormatEnd);

            System.out.println("Sent Content: "+requestBody.toString());
        }catch (JSONException ignored){ }
        AtomicBoolean successBoolean = new AtomicBoolean(false);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, makeAReservationEndpoint, requestBody,
                        response -> {
                            try{
                                //TODO Change name of JSON pair to whatever danny makes it
                                boolean successToReserve = response.getBoolean("reservationSuccessful");
                                successBoolean.set(successToReserve);
                                System.out.println("Received Content: "+successBoolean.get());
                            }catch(Exception e){
                                successBoolean.set(false);
                            }
                            if(successBoolean.get()){
                                Toast.makeText(ReservationScreen.this, "Reservation Successful.", Toast.LENGTH_LONG).show();
                            }
                            else if(!successBoolean.get()) {
                                Toast.makeText(ReservationScreen.this, "Reservation Unsuccessful, try another time.", Toast.LENGTH_LONG).show();
                            }
                        }, error -> { System.out.println("Error "+error.toString());
                });
        //TODO pop up waiting symbol, until the response is received.
        // The code to show will be right here, but the the code to remove it will be in the listeners
        // Add the request to the queue, which will complete eventually
        APIRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    // ---------------------- This stuff is used for the top-right dropdown menu
    //TODO add view reservations screen
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reservations_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // Add cases with the buttonID as you need more menu options
            case R.id.reservationsViewReservations:
                Intent intent1 = new Intent(this, ViewReservationsScreen.class);
                intent1.putExtra("user",authenticatedUser);
                startActivity(intent1);
                return true;
            case R.id.reservationsLogOut:
                logout();
                Intent intent2 = new Intent(this, LoginScreen.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                return true;
            default:
                return false;
        }
    }
    // ---------------------- END stuff used for top-right dropdown menu

    //TODO: This might or might not be used, we might have to do some clean-up when the menu logout button is pressed.
    public boolean logout(){
        return false;
    }

}