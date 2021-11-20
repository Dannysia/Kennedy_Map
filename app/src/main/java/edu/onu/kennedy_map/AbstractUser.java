package edu.onu.kennedy_map;

import java.io.Serializable;

/**
 * This class acts as the 'Product', that is implemented into ConcreteProducts as part of our Factory method.
 */
public abstract class AbstractUser implements Serializable {
    private String username;
    private int userID;

    /**
     * Getter function used to return the username of the current user
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter function used to set the username of the current user
     * @param username The username
     */
    public void setUsername(String username){
        this.username=username;
    }

    /**
     * Getter function used to return the id of the current user
     * @return The user ID
     */
    public int getUserID() { return userID; }

    /**
     * Setter function used to set the id of the current user
     * @param userID The user ID
     */
    public void setUserID(int userID){this.userID=userID;}
}
