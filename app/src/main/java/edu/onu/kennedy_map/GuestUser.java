package edu.onu.kennedy_map;

public class GuestUser extends AbstractUser{
    private final String username = "GUEST";
    private final int userID = 1;

    public String showGuestInfo(){
        return null;
    }
}
