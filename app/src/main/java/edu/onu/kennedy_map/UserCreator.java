package edu.onu.kennedy_map;

/**
 * Abstract UserCreator class is inherited from to create our User factories.
 */
abstract public class UserCreator {
    abstract public AbstractUser createUser(String username, int userID);
}
