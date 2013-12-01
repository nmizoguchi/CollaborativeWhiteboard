package client;

import server.Connection;

public class OnlineUser {

    private String name;

    public OnlineUser(String name) {
        this.name = name;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OnlineUser)) {
            return false;
        }

        OnlineUser that = (OnlineUser) o;

        if (!name.equals(that.name))
            return false;

        return true;
    }
}
