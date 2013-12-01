package server;

import java.util.UUID;

import shared.models.User;
import shared.models.Whiteboard;
import Protocol.Protocol;

/**
 * Represents a client connection on server-side. This connection has
 * information about the connected client, such as his username, which board the
 * client is working on, and also the last version sent to the client of that
 * board.
 * 
 * @author Nicholas M. Mizoguchi
 */
public class Connection {

    private final UUID uid;
    private ConnectionOutputHandler scheduler;
    private User user;
    private Whiteboard activeWhiteboard;
    private int lastSentVersion;

    public Connection(Whiteboard activeWhiteboard) {
        this.uid = UUID.randomUUID();
        this.activeWhiteboard = activeWhiteboard;
        this.lastSentVersion = lastSentVersion;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isInitialized() {
        return user != null;
    }

    public void addConnectionOutputHandler(ConnectionOutputHandler handler) {
        this.scheduler = handler;
    }

    public synchronized Whiteboard getActiveWhiteboard() {
        return activeWhiteboard;
    }

    public synchronized void setActiveWhiteboard(Whiteboard activeWhiteboard,
            int lastSentVersion) {
        this.activeWhiteboard = activeWhiteboard;
        this.lastSentVersion = lastSentVersion;

        String message = Protocol.CreateServerMessage("changeboard",
                activeWhiteboard.getName());
        scheduler.scheduleMessage(message);
    }

    public synchronized void sendClientWhiteboardUpdate() {

        if (this.lastSentVersion < activeWhiteboard.getVersion()) {
            scheduler.scheduleMessage(activeWhiteboard
                    .getAction(lastSentVersion));
            lastSentVersion += 1;
        }
    }

    public synchronized void updateActiveWhiteboard(String action) {
        activeWhiteboard.update(action);
    }

    public UUID getUid() {
        return uid;
    }

    @Override
    public synchronized boolean equals(Object o) {
        if (!(o instanceof Connection)) {
            return false;
        }

        Connection that = (Connection) o;
        if (!that.getUid().equals(uid)) {
            return false;
        }

        return true;
    }

    @Override
    public synchronized int hashCode() {
        return uid.hashCode();
    }
}