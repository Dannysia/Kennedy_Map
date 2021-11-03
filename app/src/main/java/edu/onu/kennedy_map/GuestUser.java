package edu.onu.kennedy_map;

import java.io.Serializable;

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
