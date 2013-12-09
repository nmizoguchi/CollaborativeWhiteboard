package client.gui;

import shared.models.User;

/**
 * Implements Runnable. When called, it updates the list of users to include a new user
 * @author rcha
 *
 */
public class RunnableNewuser implements Runnable {
    
    private final User user;
    private final UserListModel users;
    
    /**
     * Takes in a UserListModel of active users and the new User that has connected to the server
     * @param users is a UserListModel
     * @param user is a new User
     */
    public RunnableNewuser(UserListModel users, User user) {
        this.user = user;
        this.users = users;
    }

    /**
     * Adds the new User to the UserListModel of active users
     */
    @Override
    public void run() {
        users.addElement(user);
    }
}
