package edu.onu.kennedy_map;

public class ConcreteRegisteredUserCreator extends UserCreator{
    @Override
    public AbstractUser createUser(String username, int userID) {
        RegisteredUser newRegisteredUser = new RegisteredUser(username,userID);
        return newRegisteredUser;
    }
}
