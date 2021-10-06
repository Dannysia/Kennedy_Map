package edu.onu.kennedy_map;

import java.io.Serializable;
import java.util.HashMap;

public class RegisteredUser extends AbstractUser implements Serializable {
    private String username;
    private int userID;
    private HashMap<String,String> userInfo;
    private boolean isAuthenticated = false;

    /**
     * Constructor for RegisteredUser is filled during object creation. You will read from the database to populate the HashMap
     * @param userInfo The HashMap containing userInfo
     * @param userID The userID returned by the response
     */
    public RegisteredUser(int userID,HashMap<String,String> userInfo){
        this.userID = userID;
        this.userInfo = userInfo;
    }

    /**
     * Returns the value in the HashMap using the passed key
     * @param infoID The key
     * @return The value of the HashMap using the key
     */
    public String getUserInfo(String infoID){
        return userInfo.get(infoID);
    }

    // May never need this
    public boolean isAuthenticated(){
        return this.isAuthenticated;
    }

    @Override
    public int getUserID() {
        return userID;
    }

}
