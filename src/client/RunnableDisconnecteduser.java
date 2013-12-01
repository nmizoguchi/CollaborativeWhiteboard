package client;

import server.ServerApplication;
import shared.models.User;

public class RunnableDisconnecteduser implements Runnable {
    
    private final User user;
    private final UserListModel users;
    
    public RunnableDisconnecteduser(UserListModel users, User user) {
        this.user = user;
        this.users = users;
    }

    @Override
    public void run() {
        users.removeElement(user);
    }
}
