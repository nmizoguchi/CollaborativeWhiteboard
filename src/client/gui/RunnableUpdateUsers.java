package client.gui;

import java.util.List;

import shared.models.User;

/**
 * Implements Runnable. When called, it updates the list of users to include a new user
 * @author rcha
 *
 */
public class RunnableUpdateUsers implements Runnable {
    
    private final List<User> userList;
    private final UserListModel users;
    
    /**
     * Takes in a UserListModel of active users and the new User that has connected to the server
     * @param users is a UserListModel
     * @param userList is the list of users
     */
    public RunnableUpdateUsers(UserListModel users, List<User> userList) {
        this.userList = userList;
        this.users = users;
    }

    /**
     * Adds the new User to the UserListModel of active users
     */
    @Override
    public void run() {
        
        users.clear();
        
        for(User user : userList) {
                users.addElement(user);
        }
    }
}
