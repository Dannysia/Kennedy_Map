package edu.onu.kennedy_map;

/**
 * Creates and returns RegisteredUsers as part of the Factory Method
 */
public class ConcreteRegisteredUserCreator extends UserCreator{
    @Override
    public AbstractUser createUser(String username, int userID) {
        RegisteredUser newRegisteredUser = new RegisteredUser(username,userID);
        return newRegisteredUser;
    }
}
