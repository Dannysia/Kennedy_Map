package edu.onu.kennedy_map;

import java.io.Serializable;

/**
 * The GuestUser represents a user with reduced privileges accessing the application.
 * Created when the 'Continue as Guest' button is pressed, by the ConcreteGuestUserCreator.
 */
public class GuestUser extends AbstractUser implements Serializable {
    private final String username = "GUEST";
    private final int userID = -1;
    public GuestUser(){
        super.setUserID(-1);
    }

    public String showGuestInfo(){
        return null;
    }
}
