package shared.models;

import java.util.UUID;

/**
 * Represents a User that is using the collaborative whiteboard application. It
 * is mutable.
 * 
 * @author Nicholas M. Mizoguchi
 * 
 */
public class User {

    /*
     * Thread-safe argument: Since it is a mutable class, uses the monitor
     * pattern to guarantee thread safety.
     */
    private final UUID uid;
    private String name;

    /**
     * Constructor. Defines a random unique id to this object.
     * 
     * @param username
     *            the User's desired username.
     */
    public User(String username) {

        this.uid = UUID.randomUUID();
        this.setName(username);
    }

    /**
     * Constructor. Creates an instance of user. Sets the unique id.
     * 
     * @param uid
     *            desired uid as a string. Need to be a representation of an
     *            UUID as a string.
     * @param username
     *            the desired username.
     */
    public User(String uid, String username) {

        this.uid = UUID.fromString(uid);
        this.setName(username);
    }

    /**
     * @return the unique id of this object.
     */
    public synchronized UUID getUid() {
        return uid;
    }

    /**
     * @return the name of this user.
     */
    public synchronized String getName() {
        return name;
    }

    /**
     * Sets the name of this user
     * 
     * @param username
     *            the desired username.
     */
    public synchronized void setName(String username) {
        this.name = username;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public synchronized boolean equals(Object o) {
        if (!(o instanceof User)) {
            return false;
        }

        User that = (User) o;
        if (!that.getUid().equals(uid)) {
            return false;
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public synchronized int hashCode() {
        return uid.hashCode();
    }

    /**
     * Returns a representation of the user in the format "uid username".
     */
    @Override
    public synchronized String toString() {
        return this.getUid() + " " + this.getName();
    }
}
