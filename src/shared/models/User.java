package shared.models;

import java.util.UUID;

public class User {

    private final UUID uid;
    private String name;
    private String ipAddress;

    public User(String username) {

        this.uid = UUID.randomUUID();
        this.setName(username);
        this.ipAddress = "0.0.0.0";
    }
    
    public User(String uid, String username) {

        this.uid = UUID.fromString(uid);
        this.setName(username);
        this.ipAddress = "0.0.0.0";
    }

    public synchronized UUID getUid() {
        return uid;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String username) {
        this.name = username;
    }

    public synchronized String getIpAddress() {
        return ipAddress;
    }
    
    @Override
    public synchronized boolean equals(Object o) {
        if( !( o instanceof User)) {
            return false;
        }
        
        User that = (User) o;
        if(!that.getUid().equals(uid)) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public synchronized int hashCode() {
        return uid.hashCode();
    }
    
    @Override
    public synchronized String toString() {
        return this.getUid() + " " + this.getName();
    }
}
