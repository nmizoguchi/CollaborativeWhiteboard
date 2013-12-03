package client.gui;

import shared.models.User;

public class RunnableNewuser implements Runnable {
    
    private final User user;
    private final UserListModel users;
    
    public RunnableNewuser(UserListModel users, User user) {
        this.user = user;
        this.users = users;
    }

    @Override
    public void run() {
        users.addElement(user);
    }
}
