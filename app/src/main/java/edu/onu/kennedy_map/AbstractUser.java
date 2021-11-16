package edu.onu.kennedy_map;

import java.io.Serializable;

/**
 * This class acts as the 'Product', that is implemented into ConcreteProducts as part of our Factory method.
 */
public abstract class AbstractUser implements Serializable {
    private String username;
    private int userID;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username){
        this.username=username;
    }
    public int getUserID() { return userID; }
    public void setUserID(int userID){this.userID=userID;}
}
