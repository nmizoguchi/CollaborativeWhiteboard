package server;

import java.util.UUID;

import shared.models.User;
import shared.models.Whiteboard;
import Protocol.CWPMessage;

/**
 * Represents a client connection on server-side. This connection has
 * information about the connected user, such as its username, which board the
 * user is working on, and also the last version sent to the client of that
 * board. It is mutable.
 * 
 * @author Nicholas M. Mizoguchi
 */
public class Connection {

    /*
     * Representation Invariant: The lastSentVersion is always in respect to the
     * last version sent to the client of the activeWhiteboard. If the
     * activeWhiteboard changes, then the lastSentVersion should change
     * accordingly.
     * 
     * The user should always represent the user in the other end of this
     * Connection.
     * 
     * Thread-safe argument: The threads that access this class are the one
     * responsible for incoming messages from the client, inside the
     * ConnectionController, and the one that checks for updates to send to the
     * client. The later only access them method sendClientWhiteboardUpdate().
     * 
     * We designed this class to implement the monitor pattern, so that all
     * atomic methods are guaranteed to maintain the rep. invariant after
     * finishing.
     */
    private ConnectionOutputHandler scheduler;
    private User user;
    private final User serverUser;
    private Whiteboard activeWhiteboard;
    private int lastSentVersion;

    /**
     * Constructor.
     * 
     * @param activeWhiteboard
     *            the board that it will start working on.
     */
    public Connection(Whiteboard activeWhiteboard, User serverUser) {
        this.activeWhiteboard = activeWhiteboard;
        this.lastSentVersion = 0;
        this.serverUser = serverUser;
    }

    /**
     * @return the User object regarding this connection. This method can only
     *         be called if the Connection was initialized.
     */
    public synchronized User getUser() {
        return user;
    }

    /**
     * @param the
     *            User to be set to this connection
     */
    public synchronized void setUser(User user) {
        this.user = user;
    }

    /**
     * Verify if the connection is initialized. The connection being initialized
     * means that other clients can acknowledge its existence.
     * 
     * @return true if it is initialized. false otherwise.
     */
    public synchronized boolean isInitialized() {
        return user != null;
    }

    /**
     * Sets a handler that knows how to send messages to the server.
     * 
     * @param handler
     *            the Handler itself, that can inform the server that it wants
     *            to send something through this connection.
     */
    public synchronized void addConnectionOutputHandler(
            ConnectionOutputHandler handler) {
        this.scheduler = handler;
    }

    /**
     * @return the whiteboard in which the user is working on.
     */
    public synchronized Whiteboard getActiveWhiteboard() {
        return activeWhiteboard;
    }

    /**
     * Change the client's active board.
     * 
     * @param activeWhiteboard
     *            the whiteboard in which the client will work on.
     * @param lastSentVersion
     *            the last version sent to the client of the new board.
     */
    public synchronized void setActiveWhiteboard(Whiteboard activeWhiteboard,
            int lastSentVersion) {
        this.activeWhiteboard = activeWhiteboard;
        this.lastSentVersion = lastSentVersion;

        String[] args = new String[] { activeWhiteboard.getName() };
        String message = CWPMessage.Encode(serverUser, "changeboard", args);

        scheduler.scheduleMessage(message);
    }

    /**
     * Sends through this connection an update of the active board. It sends an
     * update iff the last version sent to the client is older than the version
     * in the server.
     */
    public synchronized void sendClientWhiteboardUpdate() {

        if (this.lastSentVersion < activeWhiteboard.getVersion()) {
            String message = CWPMessage.EncodePaintAction(serverUser,
                    activeWhiteboard.getAction(lastSentVersion));
            scheduler.scheduleMessage(message);
            lastSentVersion += 1;
        }
    }

    /**
     * Update the current active board with an action. This method updates the
     * version of the server's representation of the board.
     * 
     * @param action
     */
    public synchronized void updateActiveWhiteboard(String action) {
        activeWhiteboard.update(action);
    }
}