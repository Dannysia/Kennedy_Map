package edu.onu.kennedy_map;

public class ConcreteGuestUserCreator extends UserCreator{
    @Override
    public AbstractUser createUser(String username, int userID) {
        return new GuestUser();
    }
}
