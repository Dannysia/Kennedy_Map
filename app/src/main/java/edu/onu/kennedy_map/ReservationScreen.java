package edu.onu.kennedy_map;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.dpizarro.uipicker.library.picker.PickerUI;
import com.dpizarro.uipicker.library.picker.PickerUISettings;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

@SuppressWarnings("unchecked")
public class ReservationScreen extends AppCompatActivity {

    AbstractUser authenticatedUser;
    ArrayList<Room> allRooms;
    ArrayList<Reservation> selectedRoomReservations = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authenticatedUser = (AbstractUser) getIntent().getSerializableExtra("user");
        allRooms = (ArrayList<Room>) getIntent().getSerializableExtra("rooms");
        //Toast.makeText(ReservationScreen.this, ""+authenticatedUser.getUserID(), Toast.LENGTH_LONG).show();
        setTitle("Reservation Screen            Options:");
        setContentView(R.layout.reservation_screen);

        //TODO we can worry about moving all this stuff to the rooms class later

        //Populate dropdown
        //DatabaseManager.getInstance().reservationPageLoadRooms(this);
        PickerUI roomPicker = findViewById(R.id.picker_ui_view);
        ArrayList<Room> reservableRooms = new ArrayList<>();
        ArrayList<String> roomShortNames = new ArrayList<>();

        for (Room room : allRooms){
            if(room.isReservable()){
                reservableRooms.add(room);
            }
        }
        for (int i = 0; i<reservableRooms.size();i++){
            roomShortNames.add(reservableRooms.get(i).getShortName());
        }

        PickerUISettings pickerUISettings = new PickerUISettings.Builder()
                .withItems(roomShortNames)
                .withAutoDismiss(false)
                .withItemsClickables(false)
                .withUseBlur(false)
                .build();

        roomPicker.setSettings(pickerUISettings);
        LinearLayout loadingLinearLayout = findViewById(R.id.reservationLoadingLinearLayout);
        loadingLinearLayout.setVisibility(View.GONE);
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
    // TODO: Make sure display month is fixed
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
            clickedButton.setText(""+(month+1)+"/"+day+"/"+year);
            if(day<10){
                storageDay.setText("0"+day);
            }else{
                storageDay.setText(""+day);
            }
            if((month+1)<10){
                storageMonth.setText("0"+ (month+1));
            }else{
                storageMonth.setText(""+ (month+1));
            }
            storageYear.setText(""+year);
        }
    }
    public void startTimeButton(View view){
        Button startTimeButton =findViewById(R.id.startTimeButton);
        TextView startHour = findViewById(R.id.startHour);
        TextView startMinute = findViewById(R.id.startMinute);
        TimePickerFragment newFragment = new TimePickerFragment(startTimeButton,startHour,startMinute);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
    public void startDateButton(View view){
        Button startDateButton = findViewById(R.id.startDateButton);
        TextView startDay = findViewById(R.id.startDay);
        TextView startMonth = findViewById(R.id.startMonth);
        TextView startYear = findViewById(R.id.startYear);
        DatePickerFragment newFragment = new DatePickerFragment(startDateButton,startDay,startMonth,startYear);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    public void endTimeButton(View view){
        Button endTimeButton = findViewById(R.id.endTimeButton);
        TextView endHour = findViewById(R.id.endHour);
        TextView endMinute = findViewById(R.id.endMinute);
        TimePickerFragment newFragment = new TimePickerFragment(endTimeButton,endHour,endMinute);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
    public void endDateButton(View view){
        Button endDateButton = findViewById(R.id.endDateButton);
        TextView endDay = findViewById(R.id.endDay);
        TextView endMonth = findViewById(R.id.endMonth);
        TextView endYear = findViewById(R.id.endYear);
        DatePickerFragment newFragment = new DatePickerFragment(endDateButton,endDay,endMonth,endYear);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void selectRoomButton(View view){
        Button selectRoomButton = findViewById(R.id.selectRoomButton);
        PickerUI roomPicker = findViewById(R.id.picker_ui_view);
        roomPicker.setOnClickItemPickerUIListener((which, position, valueResult) -> {

            // Query the database for the reservations for the selected room
            if(valueResult != null && !valueResult.equals("")){
                System.out.println(valueResult);
                DatabaseManager.getInstance().reservationPageSelectedRoom(ReservationScreen.this,valueResult,selectRoomButton,
                        allRooms,selectedRoomReservations);
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
    public void reserveConfirmButton(View view){

        DatabaseManager.getInstance().reservationPageReserveRoom(this, (RegisteredUser) authenticatedUser,
                allRooms,selectedRoomReservations);
    }

    // ---------------------- This stuff is used for the top-right dropdown menu
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
                intent1.putExtra("rooms",allRooms);
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