package edu.onu.kennedy_map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dpizarro.uipicker.library.picker.PickerUI;
import com.dpizarro.uipicker.library.picker.PickerUISettings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReservationScreen extends AppCompatActivity {

    AbstractUser authenticatedUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Add drop down menu back so they can go back without having to click the back button
        setTitle("Reservation Screen             ");
        setContentView(R.layout.reservation_screen);

        //TODO change this list to the rooms we want to be reservable later
        ArrayList<String> rooms = new ArrayList<>();
        rooms.add("Test1");
        rooms.add("Test2");
        rooms.add("Test3");
        rooms.add("Test4");
        rooms.add("Test5");
        rooms.add("Test6");
        rooms.add("Test7");
        rooms.add("Test8");
        PickerUI roomPicker = (PickerUI) findViewById(R.id.picker_ui_view);
        PickerUISettings pickerUISettings = new PickerUISettings.Builder()
                .withItems(rooms)
                .withAutoDismiss(false)
                .withItemsClickables(false)
                .withUseBlur(false)
                .build();

        roomPicker.setSettings(pickerUISettings);
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
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String minuteText;
            if(minute==0){
                minuteText="00";
            }else{
                minuteText=Integer.toString(minute);
            }
            button.setText(""+hourOfDay+":"+minuteText);
            storageHour.setText(""+hourOfDay);
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
                selectRoomButton.setText(valueResult);
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

    //TODO: Add all error checking here to make reservations, use authenticatedUser
    public void reserveConfirmButton(View view){

    }

}