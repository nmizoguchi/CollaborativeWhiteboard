package server;

import client.Whiteboard;

/**
 * Represents a client connection on server-side. This connection has
 * information about the connected client, such as his username, which board the
 * client is working on, and also the last version sent to the client of that
 * board.
 * 
 * @author Nicholas M. Mizoguchi
 */
public class ClientConnection {

    /*
     * Rep. Invariant: usernames cannot contain spaces. clientBoardVersion is
     * always less or equal to the activeBoard version.
     */

    private final ApplicationServer server;
    private Whiteboard activeBoard;
    private int clientBoardVersion;
    private String username;

    /**
     * Constructor. Receives which server it is working for, and also the active
     * board that should be sent to the client.
     * 
     * @param server
     *            the server that owns this ClientConnection
     */
    public ClientConnection(ApplicationServer server) {
        this.server = server;
        this.activeBoard = server.getWhiteboard("Default");
        this.username = "";
        this.clientBoardVersion = 0;
    }

    /**
     * @return the client's active board.
     */
    public Whiteboard getActiveBoard() {
        return activeBoard;
    }

    /**
     * Change the client's active board. If the board doesn't exist, creates a
     * new board on server with the given name, and set it as the active one.
     * 
     * @param name
     *            a String with no spaces.
     */
    public void setActiveBoard(String name) {
        this.activeBoard = server.getWhiteboard(name);
        this.clientBoardVersion = 0;
    }

    /**
     * @return the client's username.
     */
    public String getUserame() {
        return username;
    }

    /**
     * Set the client's username.
     * 
     * @param name
     *            Requires that the argument doesn't have spaces.
     */
    public void setUsername(String name) {
        if (name.split(" ").length > 1) {
            throw new IllegalArgumentException("Username cannot have spaces.");
        }
        this.username = name;
    }

    /**
     * Gives the last version sent to this client.
     * 
     * @return the last version sent to the client of the active board.
     */
    public int getClientBoardVersion() {
        return clientBoardVersion;
    }

    /**
     * Updates the last version sent to the client to the number passed as
     * argument.
     * 
     * @param version
     *            the value of the new version.
     */
    public void setClientBoardVersion(int version) {
        this.clientBoardVersion = version;
    }

    /**
     * Gives the names of all active boards in the server. Note that the order
     * doesn't say nothing about the server.
     * 
     * @return String of names of all Whiteboards active in the server,
     *         separated by spaces.
     */
    public String getWhiteboardNames() {
        return server.getWhiteboardNames();
    }
}
