package edu.onu.kennedy_map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuScreen extends AppCompatActivity {

    AbstractUser authenticatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Main Menu                           Options:"); // pro code
        authenticatedUser = (AbstractUser) getIntent().getSerializableExtra("user");
        setContentView(R.layout.menu_screen);
        // use getExtra to get either the GuestUser or RegisteredUser and store it in authenticatedUser

        // Setup floor radio buttons

    }
    //TODO add pan and zoom somehow

    // ------------------------------------------- Floor Buttons ------------------------------------------------
    public void floorOneRadioButton(View view){
        ImageView floorImageView = (ImageView) findViewById(R.id.floorImageView);
        TextView floorDescriptionTextView = (TextView) findViewById(R.id.floorDescriptionTextView);
        floorDescriptionTextView.setText("This is the ground floor. Unique places on this floor include" +
                " the Innovation Center, Dean's Office, and Shop.");
        floorImageView.setImageResource(R.drawable.engineering_floor_one);
    }
    public void floorTwoRadioButton(View view){
        ImageView floorImageView = (ImageView) findViewById(R.id.floorImageView);
        TextView floorDescriptionTextView = (TextView) findViewById(R.id.floorDescriptionTextView);
        floorDescriptionTextView.setText("The second floor includes professor offices' along the outer-right" +
                " edge. There are also multiple computer labs on this floor.");
        floorImageView.setImageResource(R.drawable.engineering_floor_two);
    }
    public void floorThreeRadioButton(View view){
        ImageView floorImageView = (ImageView) findViewById(R.id.floorImageView);
        TextView floorDescriptionTextView = (TextView) findViewById(R.id.floorDescriptionTextView);
        floorDescriptionTextView.setText("The third floor contains mostly classrooms and labs. The balcony provides" +
                " a nice view of the entire campus.");
        floorImageView.setImageResource(R.drawable.engineering_floor_three);
    }
    // --------------------------------------- END Floor Buttons ------------------------------------------------

    // ------------------------------- Reserve Button and Navigate Buttons --------------------------------------

    public void reservationButton(View view){
        //TODO make sure to keep passing along User, make sure guest cannot enter this screen
        Intent intent = new Intent(MenuScreen.this, ReservationScreen.class);
        startActivity(intent);
    }
    public void pathFindButton(View view){
        //TODO make sure to keep passing along User, even though it doesn't matter here you will want to pass it back
        Intent intent = new Intent(MenuScreen.this, PathScreen.class);
        startActivity(intent);
    }

    // --------------------------- END Reserve Button and Navigate Buttons --------------------------------------

    //TODO Need to fix the MapZoomAndPanLayout class in order to do it this way
    public void mapZoomButton(View view){
        MapZoomAndPanLayout mapZoomAndPanLayout = (MapZoomAndPanLayout) findViewById(R.id.mapZoomAndPanLayout);
        mapZoomAndPanLayout.getChildAt(0).setScaleX((float) (mapZoomAndPanLayout.getChildAt(0).getScaleX()+0.25));
        mapZoomAndPanLayout.getChildAt(0).setScaleY((float) (mapZoomAndPanLayout.getChildAt(0).getScaleY()+0.25));
    }
    public void mapZoomOutButton(View view){
        MapZoomAndPanLayout mapZoomAndPanLayout = (MapZoomAndPanLayout) findViewById(R.id.mapZoomAndPanLayout);
        mapZoomAndPanLayout.getChildAt(0).setScaleX((float) (mapZoomAndPanLayout.getChildAt(0).getScaleX()-0.25));
        mapZoomAndPanLayout.getChildAt(0).setScaleY((float) (mapZoomAndPanLayout.getChildAt(0).getScaleY()-0.25));
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

    /**
     * Returns the current authenticatedUser stored, whether it be a guest or a registered user.
     * @return
     */
    public AbstractUser getCurrentUser(){
        return this.authenticatedUser;
    }
}