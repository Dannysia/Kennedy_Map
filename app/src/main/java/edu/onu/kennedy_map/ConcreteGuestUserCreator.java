package edu.onu.kennedy_map;

/**
 * Creates and Returns GuestUsers as part of the Factory Method.
 */
public class ConcreteGuestUserCreator extends UserCreator{
    @Override
    public AbstractUser createUser(String username, int userID) {
        return new GuestUser();
    }
}
