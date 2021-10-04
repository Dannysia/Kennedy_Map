package edu.onu.kennedy_map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.dpizarro.uipicker.library.picker.PickerUI;
import com.dpizarro.uipicker.library.picker.PickerUISettings;

import java.util.ArrayList;

public class PathScreen extends AppCompatActivity {

    // TODO On path Screen xml, add an arrow pointing right that lets users know its scrollable


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Path Finding                        Options:");
        setContentView(R.layout.path_screen);

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
        PickerUI startingRoomPicker = (PickerUI)findViewById(R.id.startingRoomPicker);
        PickerUI endingRoomPicker = (PickerUI)findViewById(R.id.endingRoomPicker);
        PickerUISettings pickerUISettings = new PickerUISettings.Builder()
                .withItems(rooms)
                .withAutoDismiss(false)
                .withItemsClickables(false)
                .withUseBlur(false)
                .build();

        startingRoomPicker.setSettings(pickerUISettings);
        endingRoomPicker.setSettings(pickerUISettings);
    }

    public void pathFindRoomOneButton(View view){
        Button pathFindRoomOneButton = (Button) findViewById(R.id.pathFindRoomOneButton);
        PickerUI startingRoomPicker = (PickerUI) findViewById(R.id.startingRoomPicker);
        startingRoomPicker.setOnClickItemPickerUIListener((which, position, valueResult) -> {
            //TODO: Add so where it zooms in on the room based on the selection, gonna resize all the images to 1000x1000 to do this easy
            pathFindRoomOneButton.setText(valueResult);
        });
        startingRoomPicker.slide(0);
    }
    public void pathFindRoomTwoButton(View view){
        Button pathFindRoomTwoButton = (Button) findViewById(R.id.pathFindRoomTwoButton);
        PickerUI endingRoomPicker = (PickerUI) findViewById(R.id.endingRoomPicker);
        endingRoomPicker.setOnClickItemPickerUIListener((which, position, valueResult) -> {
            //TODO: Add so where it zooms in on the room based on the selection, gonna resize all the images to 1000x1000 to do this easy
            pathFindRoomTwoButton.setText(valueResult);
        });
        endingRoomPicker.slide(0);
    }

    // TODO: Call path find algorithm, then update imageViews appropriately
    public void pathFindCalculateButton(View view){

    }

    public void pathFindTitleTextView(View view){
        PickerUI startingRoomPicker = (PickerUI)findViewById(R.id.startingRoomPicker);
        PickerUI endingRoomPicker = (PickerUI)findViewById(R.id.endingRoomPicker);
        if(startingRoomPicker.isPanelShown()){
            startingRoomPicker.slide();
        }else if(endingRoomPicker.isPanelShown()){
            endingRoomPicker.slide();
        }
    }

    public void imageViewScrollView(View view){
        PickerUI startingRoomPicker = (PickerUI)findViewById(R.id.startingRoomPicker);
        PickerUI endingRoomPicker = (PickerUI)findViewById(R.id.endingRoomPicker);
        if(startingRoomPicker.isPanelShown()){
            startingRoomPicker.slide();
        }else if(endingRoomPicker.isPanelShown()){
            endingRoomPicker.slide();
        }
    }
    public void pathFindThirdFloorImage(View view){
        PickerUI startingRoomPicker = (PickerUI)findViewById(R.id.startingRoomPicker);
        PickerUI endingRoomPicker = (PickerUI)findViewById(R.id.endingRoomPicker);
        if(startingRoomPicker.isPanelShown()){
            startingRoomPicker.slide();
        }else if(endingRoomPicker.isPanelShown()){
            endingRoomPicker.slide();
        }
    }
    public void pathFindSecondFloorImage(View view){
        PickerUI startingRoomPicker = (PickerUI)findViewById(R.id.startingRoomPicker);
        PickerUI endingRoomPicker = (PickerUI)findViewById(R.id.endingRoomPicker);
        if(startingRoomPicker.isPanelShown()){
            startingRoomPicker.slide();
        }else if(endingRoomPicker.isPanelShown()){
            endingRoomPicker.slide();
        }
    }
    public void pathFindFirstFloorImage(View view){
        PickerUI startingRoomPicker = (PickerUI)findViewById(R.id.startingRoomPicker);
        PickerUI endingRoomPicker = (PickerUI)findViewById(R.id.endingRoomPicker);
        if(startingRoomPicker.isPanelShown()){
            startingRoomPicker.slide();
        }else if(endingRoomPicker.isPanelShown()){
            endingRoomPicker.slide();
        }
    }

    // ---------------------- This stuff is used for the top-right dropdown menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_screen_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // Add cases with the buttonID as you need more menu options
            case R.id.menuLogOut:
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