package client.gui;

import shared.models.User;

/**
 * Implements Runnable. When called, the disconnected user
 *  is removed from the list of active users
 * @author rcha
 *
 */
public class RunnableDisconnecteduser implements Runnable {
    
    private final User user;
    private final UserListModel users;
    
    /**
     * Takes in a list of users and the user that has disconnected
     * @param users is a UserListModel that contains a list of the names of active users
     * @param user is the User that has disconnected
     */
    public RunnableDisconnecteduser(UserListModel users, User user) {
        this.user = user;
        this.users = users;
    }

    /**
     * Removes the user from the list of active users
     */
    @Override
    public void run() {
        if(users.contains(user)) {
            users.removeElement(user);
        }
    }
}
