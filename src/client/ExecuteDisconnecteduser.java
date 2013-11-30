package client;

public class ExecuteDisconnecteduser implements Runnable {
    
    private final OnlineUser user;
    private final OnlineUserListModel users;
    
    public ExecuteDisconnecteduser(OnlineUserListModel users, OnlineUser user) {
        this.user = user;
        this.users = users;
    }

    @Override
    public void run() {
        users.removeElement(user);
    }
}
