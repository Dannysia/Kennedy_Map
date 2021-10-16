package edu.onu.kennedy_map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.dpizarro.uipicker.library.picker.PickerUI;
import com.dpizarro.uipicker.library.picker.PickerUISettings;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class PathScreen extends AppCompatActivity {

    // TODO On path Screen xml, add an arrow pointing right that lets users know its scrollable
    private DrawableSurfaceView floor1DSV;
    private DrawableSurfaceView floor2DSV;
    private DrawableSurfaceView floor3DSV;
    private PathFind pathFind;

    ArrayList<Room> allRooms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Path Finding                        Options:");
        allRooms = (ArrayList<Room>)getIntent().getSerializableExtra("rooms");
        setContentView(R.layout.path_screen);

        //TODO Added
        //Get all the surface views and store them
        floor1DSV = findViewById(R.id.floor1DSV);
        floor2DSV = findViewById(R.id.floor2DSV);
        floor2DSV = findViewById(R.id.floor3DSV);

        //TODO Added
        pathFind = new PathFind(this, floor1DSV, floor2DSV, floor3DSV);

        //TODO change this list to the rooms later
        ArrayList<String> roomShortNames = new ArrayList<>();
        for (int i = 0; i<allRooms.size();i++){
            roomShortNames.add(allRooms.get(i).getShortName());
        }
        PickerUI startingRoomPicker = (PickerUI)findViewById(R.id.startingRoomPicker);
        PickerUI endingRoomPicker = (PickerUI)findViewById(R.id.endingRoomPicker);
        PickerUISettings pickerUISettings = new PickerUISettings.Builder()
                .withItems(roomShortNames)
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

            pathFindRoomOneButton.setText(valueResult);
        });
        startingRoomPicker.slide(0);
    }
    public void pathFindRoomTwoButton(View view){
        Button pathFindRoomTwoButton = (Button) findViewById(R.id.pathFindRoomTwoButton);
        PickerUI endingRoomPicker = (PickerUI) findViewById(R.id.endingRoomPicker);
        endingRoomPicker.setOnClickItemPickerUIListener((which, position, valueResult) -> {

            pathFindRoomTwoButton.setText(valueResult);
        });
        endingRoomPicker.slide(0);
    }

    // TODO: Call path find algorithm, then update imageViews appropriately
    public void pathFindCalculateButton(View view){
        // Using the name of the selected rooms in the buttons, get the Room object from the arraylist and pass to algorithm
        Button pathFindRoomOneButton = findViewById(R.id.pathFindRoomOneButton);
        Button pathFindRoomTwoButton = findViewById(R.id.pathFindRoomTwoButton);
        Room startRoom=null;
        Room endRoom=null;

        for (Room room : allRooms){
            if(pathFindRoomOneButton.getText().equals(room.getShortName())){
                startRoom = room;
            }
        }
        for (Room room : allRooms){
            if(pathFindRoomTwoButton.getText().equals(room.getShortName())){
                endRoom = room;
            }
        }


        //TODO Added
        pathFind.initialize();

        //Pathfind returns true if a path was found and false if there is no path
        //this can be used to trigger a message to inform the user (especially if we let the user pick a point themselves)


        if(pathFind.pathFindBetween(startRoom, endRoom)){
            Log.d("PathFind", "debugClick: Path Found!");
        } else {
            Log.d("PathFind", "debugClick: Path NOT Found!");
        }


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

    // TODO: Add option to return to Menu screen
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // Add cases with the buttonID as you need more menu options
            case R.id.menuLogOut:
                Intent intent2 = new Intent(this, LoginScreen.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                return true;
            default:
                return false;
        }
    }
    // ---------------------- END stuff used for top-right dropdown menu

}