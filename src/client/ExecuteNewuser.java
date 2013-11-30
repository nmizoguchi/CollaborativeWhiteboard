package client;

public class ExecuteNewuser implements Runnable {
    
    private final String username;
    private final OnlineUserListModel users;
    
    public ExecuteNewuser(OnlineUserListModel users, String username) {
        this.username = username;
        this.users = users;
    }

    @Override
    public void run() {
        users.addElement(new OnlineUser(username));
    }
}
