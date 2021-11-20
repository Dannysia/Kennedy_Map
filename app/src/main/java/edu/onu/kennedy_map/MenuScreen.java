package edu.onu.kennedy_map;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 *  This class acts as the jump pad to the features of the app: reservation and navigation.
 *  Additionally, a zoomable map is accessible on this screen.
 */
@SuppressWarnings("unchecked")
public class MenuScreen extends AppCompatActivity {

    private AbstractUser authenticatedUser;
    private ArrayList<Room> allRooms;

    // When this screen is loaded, the display is set and the passed info is retrieved and stored in this activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Main Menu                           Options:");
        // use getExtra to get either the GuestUser or RegisteredUser and store it in authenticatedUser
        authenticatedUser = (AbstractUser) getIntent().getSerializableExtra("user");
        allRooms = (ArrayList<Room>) getIntent().getSerializableExtra("rooms");
        //Toast.makeText(MenuScreen.this, ""+authenticatedUser.getUserID(), Toast.LENGTH_LONG).show();
        setContentView(R.layout.menu_screen);

    }

    // ------------------------------------------- Floor Buttons ------------------------------------------------
    // Floor One Radio button event handle changes the picture to the first floor, along with the first floor description
    public void floorOneRadioButton(View view){
        ImageView floorImageView = findViewById(R.id.floorImageView);
        TextView floorDescriptionTextView = findViewById(R.id.floorDescriptionTextView);
        floorDescriptionTextView.setText("This is the ground floor. Unique places on this floor include" +
                " the Innovation Center, Dean's Office, and Shop.");
        floorImageView.setImageResource(R.drawable.engineering_floor_one);
    }
    // Floor Two Radio button event handle changes the picture to the second floor, along with the second floor description
    public void floorTwoRadioButton(View view){
        ImageView floorImageView = findViewById(R.id.floorImageView);
        TextView floorDescriptionTextView = findViewById(R.id.floorDescriptionTextView);
        floorDescriptionTextView.setText("The second floor includes professor offices' along the outer-right" +
                " edge. There are multiple computer labs on this floor.");
        floorImageView.setImageResource(R.drawable.engineering_floor_two);
    }
    // Floor Three Radio button event handle changes the picture to the third floor, along with the third floor description
    public void floorThreeRadioButton(View view){
        ImageView floorImageView = findViewById(R.id.floorImageView);
        TextView floorDescriptionTextView = findViewById(R.id.floorDescriptionTextView);
        floorDescriptionTextView.setText("The third floor contains mostly classrooms and labs. The balcony provides" +
                " a nice view of the entire campus.");
        floorImageView.setImageResource(R.drawable.engineering_floor_three);
    }
    // --------------------------------------- END Floor Buttons ------------------------------------------------

    // ------------------------------- Reserve Button and Navigate Buttons --------------------------------------

    // Changes the view to the reservations screen if the user is a registered user
    public void reservationButton(View view){
        if(authenticatedUser.getUserID()!=-1){
            Intent intent = new Intent(MenuScreen.this, ReservationScreen.class);
            intent.putExtra("user",authenticatedUser);
            intent.putExtra("rooms",allRooms);
            startActivity(intent);
        }else{
            Toast.makeText(MenuScreen.this, "Guests cannot make Reservations", Toast.LENGTH_LONG).show();
        }
    }

    // Changes the floor to the path find screen
    public void pathFindButton(View view){
        Intent intent = new Intent(MenuScreen.this, PathScreen.class);
        intent.putExtra("user",authenticatedUser);
        intent.putExtra("rooms",allRooms);
        startActivity(intent);
    }

    // --------------------------- END Reserve Button and Navigate Buttons --------------------------------------

    // These may be used in the future
    /*
    public void mapZoomButton(View view){
        MapZoomAndPanLayout mapZoomAndPanLayout = findViewById(R.id.mapZoomAndPanLayout);
        mapZoomAndPanLayout.getChildAt(0).setScaleX((float) (mapZoomAndPanLayout.getChildAt(0).getScaleX()+0.25));
        mapZoomAndPanLayout.getChildAt(0).setScaleY((float) (mapZoomAndPanLayout.getChildAt(0).getScaleY()+0.25));
    }
    public void mapZoomOutButton(View view){
        MapZoomAndPanLayout mapZoomAndPanLayout = findViewById(R.id.mapZoomAndPanLayout);
        mapZoomAndPanLayout.getChildAt(0).setScaleX((float) (mapZoomAndPanLayout.getChildAt(0).getScaleX()-0.25));
        mapZoomAndPanLayout.getChildAt(0).setScaleY((float) (mapZoomAndPanLayout.getChildAt(0).getScaleY()-0.25));
    }
     */


    // ---------------------- This stuff is used for the top-right dropdown menu
    // Presents the menu if the menu button is clicked in the top right
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_screen_menu, menu);
        return true;
    }

    // Declares the event handlers for the various menu options
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // Add cases with the buttonID as you need more menu options
            case R.id.menuLogOut:
                logout();
                Intent intent2 = new Intent(this, LoginScreen.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                return true;

            case R.id.menuSendFeedback:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enter Feedback: ");
                View viewInflated = LayoutInflater.from(this).inflate(R.layout.send_feedback,
                        (ViewGroup) findViewById(R.id.menuScreenConstraintLayout).getRootView(), false);
                final EditText input = (EditText) viewInflated.findViewById(R.id.feedbackEntryEditText);
                builder.setView(viewInflated);

                // Set button listeners
                builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    DatabaseManager.getInstance().sendFeedback(MenuScreen.this,authenticatedUser.getUserID()+"",input);
                });
                builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
                builder.show();

                return true;

            default:
                return false;
        }
    }
    // ---------------------- END stuff used for top-right dropdown menu

    //This might or might not be used in the future, we might have to do some clean-up when the menu logout button is pressed.
    public boolean logout(){
        return false;
    }
}