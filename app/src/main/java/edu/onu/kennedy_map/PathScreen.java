package edu.onu.kennedy_map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.dpizarro.uipicker.library.picker.PickerUI;
import com.dpizarro.uipicker.library.picker.PickerUISettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@SuppressWarnings("unchecked")
public class PathScreen extends AppCompatActivity {

    private DrawableSurfaceView floorDSV;
    private PathFind pathFind;
    private AbstractUser authenticatedUser;
    private ArrayList<Room> allRooms;
    private boolean showAnimation = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Path Finding                         Options:");
        allRooms = (ArrayList<Room>)getIntent().getSerializableExtra("rooms");
        authenticatedUser = (AbstractUser) getIntent().getSerializableExtra("user");
        setContentView(R.layout.path_screen);

        floorDSV = findViewById(R.id.floorDSV);
        pathFind = new PathFind(this, floorDSV);

        // Initialize Pickers
        ArrayList<String> roomShortNames = new ArrayList<>();
        for (int i = 0; i<allRooms.size();i++){
            roomShortNames.add(allRooms.get(i).getShortName());
        }

        // Sort roomShortNames
        Collections.sort(roomShortNames);
        for (String string : roomShortNames){
            System.out.println(string);
        }

        // Add placeholder at beginning of arraylist:
        roomShortNames.add(0,"Click to Set Room");

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

        //MapZoomAndPanLayout pathFindZoomMap = findViewById(R.id.pathFindZoomMap);

    }

    public void pathFindRoomOneButton(View view){
        Button pathFindRoomOneButton = (Button) findViewById(R.id.pathFindRoomOneButton);
        PickerUI startingRoomPicker = (PickerUI) findViewById(R.id.startingRoomPicker);
        startingRoomPicker.setOnClickItemPickerUIListener((which, position, valueResult) -> {

            pathFindRoomOneButton.setText(valueResult);
        });
        Button testButton = findViewById(R.id.testButton);
        testButton.setVisibility(View.VISIBLE);
        startingRoomPicker.slide(0);
    }
    public void pathFindRoomTwoButton(View view){
        Button pathFindRoomTwoButton = (Button) findViewById(R.id.pathFindRoomTwoButton);
        PickerUI endingRoomPicker = (PickerUI) findViewById(R.id.endingRoomPicker);
        endingRoomPicker.setOnClickItemPickerUIListener((which, position, valueResult) -> {

            pathFindRoomTwoButton.setText(valueResult);
        });
        Button testButton = findViewById(R.id.testButton);
        testButton.setVisibility(View.VISIBLE);
        endingRoomPicker.slide(0);
    }
    public void closeSliders(View view){
        PickerUI startingRoomPicker = (PickerUI)findViewById(R.id.startingRoomPicker);
        PickerUI endingRoomPicker = (PickerUI)findViewById(R.id.endingRoomPicker);
        if(startingRoomPicker.isPanelShown()){

            startingRoomPicker.slide();
        }else if(endingRoomPicker.isPanelShown()){
            endingRoomPicker.slide();
        }
        Button testButton = findViewById(R.id.testButton);
        testButton.setVisibility(View.GONE);
    }

    public void pathFindCalculateButton(View view){
        // Using the name of the selected rooms in the buttons, get the Room object from the arraylist and pass to algorithm
        Button pathFindRoomOneButton = findViewById(R.id.pathFindRoomOneButton);
        Button pathFindRoomTwoButton = findViewById(R.id.pathFindRoomTwoButton);
        Room startRoom=null;
        Room endRoom=null;

        if(pathFindRoomOneButton.getText().toString().equalsIgnoreCase("CLICK TO SET ROOM")||
        pathFindRoomTwoButton.getText().toString().equalsIgnoreCase("CLICK TO SET ROOM")){
            Toast.makeText(this, "Please select start room and end room", Toast.LENGTH_LONG).show();
            return;
        }

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

        if(InputValidation.checkIfStartRoomIsEndRoom(startRoom,endRoom)){
            Toast.makeText(this, "Start Room is same as End Room", Toast.LENGTH_LONG).show();
            return;
        }

        pathFind.initialize();


        //TODO change from logd to user GUI popup?
        //Pathfind returns true if a path was found and false if there is no path
        //this can be used to trigger a message to inform the user (especially if we let the user pick a point themselves)
        if(pathFind.pathFindBetween(startRoom, endRoom)){
            Log.d("PathFind", "debugClick: Path Found!");
        } else {
            Log.d("PathFind", "debugClick: Path NOT Found!");
        }
    }
    public void pathFloorOneRadioButton(View view){
        ImageView pathFindImageView = findViewById(R.id.pathFindImageView);
        pathFindImageView.setImageResource(R.drawable.floor_1);
        floorDSV.changeFloorCMD(1);
    }
    public void pathFloorTwoRadioButton(View view){
        ImageView pathFindImageView = findViewById(R.id.pathFindImageView);
        pathFindImageView.setImageResource(R.drawable.floor_2);
        floorDSV.changeFloorCMD(2);
    }
    public void pathFloorThreeRadioButton(View view){
        ImageView pathFindImageView = findViewById(R.id.pathFindImageView);
        pathFindImageView.setImageResource(R.drawable.floor_3);
        floorDSV.changeFloorCMD(3);
    }

    public void animationSwitch(View view){
        SwitchCompat animationSwitch = findViewById(R.id.animationSwitch);
        if(animationSwitch.isChecked()){
            showAnimation=true;
            pathFind.toggleAnimVisibility(true);
        }else{
            showAnimation=false;
            pathFind.toggleAnimVisibility(false);
        }
    }

    // ---------------------- This stuff is used for the top-right dropdown menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.path_screen_menu, menu);
        return true;
    }


    // TODO: Add option to return to Menu screen
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // Add cases with the buttonID as you need more menu options
            case R.id.menuReturn:
                Intent intent = new Intent(this, MenuScreen.class);
                intent.putExtra("user",authenticatedUser);
                intent.putExtra("rooms",allRooms);
                startActivity(intent);
                return true;
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