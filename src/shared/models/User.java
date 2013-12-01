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

    public UUID getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String username) {
        this.name = username;
    }

    public String getIpAddress() {
        return ipAddress;
    }
    
    @Override
    public boolean equals(Object o) {
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
    public int hashCode() {
        return uid.hashCode();
    }
}
