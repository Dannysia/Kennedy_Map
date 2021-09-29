package edu.onu.kennedy_map;

import java.io.Serializable;

public abstract class AbstractUser implements Serializable {
    private String username;
    private int userID;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username){
        this.username=username;
    }
}
