package edu.onu.kennedy_map;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 */
public class RegisteredUser extends AbstractUser implements Serializable {
    private final String username;
    private int userID;
    private HashMap<String,String> userInfo;
    private boolean isAuthenticated = false;

    /**
     * Constructor for RegisteredUser is filled during object creation. You will read from the database to populate the HashMap
     * Currently unused
     * @param username The email of the user
     * @param userInfo The HashMap containing userInfo
     * @param userID The userID returned by the API response
     */
    public RegisteredUser(String username, int userID,HashMap<String,String> userInfo){
        this.username = username;
        this.userID = userID;
        this.userInfo = userInfo;
    }

    /**
     * Constructor for the RegisteredUser that doesn't provide a HashMap with additional info.
     * @param username The email of the user.
     * @param userID The userID returned by the API response
     */
    public RegisteredUser(String username, int userID){
        this.username=username;
        this.userID=userID;
    }

    /**
     * Returns the value in the HashMap using the passed key
     * @param infoID The key
     * @return The value of the HashMap using the key
     */
    public String getUserInfo(String infoID){
        return userInfo.get(infoID);
    }

    /**
     * Returns the user ID of the current user
     * @return The User ID
     */
    @Override
    public int getUserID() {
        return userID;
    }

}
