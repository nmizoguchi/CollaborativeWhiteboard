package client.gui;

import java.util.Enumeration;
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
     * @param user is a new User
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
