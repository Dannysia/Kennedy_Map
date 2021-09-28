package edu.onu.kennedy_map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MenuScreen extends AppCompatActivity {

    AbstractUser authenticatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Main Menu                           Options:"); // pro code
        setContentView(R.layout.menu_screen);
        // use getExtra to get either the GuestUser or RegisteredUser and store it in authenticatedUser
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