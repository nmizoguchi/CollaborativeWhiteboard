package server;

import java.util.UUID;

import shared.models.User;
import shared.models.Whiteboard;
import Protocol.Protocol;

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

	private final UUID uid;
	private ConnectionOutputHandler scheduler;
	private User user;
	private Whiteboard activeWhiteboard;
	private int lastSentVersion;

	/**
	 * Constructor.
	 * 
	 * @param activeWhiteboard
	 *            the board that it will start working on.
	 */
	public Connection(Whiteboard activeWhiteboard) {
		this.uid = UUID.randomUUID();
		this.activeWhiteboard = activeWhiteboard;
		this.lastSentVersion = lastSentVersion;
	}

	/**
	 * @return the User object regarding this connection.
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

		String message = Protocol.CreateServerMessage("changeboard",
				activeWhiteboard.getName());
		scheduler.scheduleMessage(message);
	}

	/**
	 * Sends through this connection an update of the active board. It sends an
	 * update iff the last version sent to the client is older than the version
	 * in the server.
	 */
	public synchronized void sendClientWhiteboardUpdate() {

		if (this.lastSentVersion < activeWhiteboard.getVersion()) {
			scheduler.scheduleMessage(activeWhiteboard
					.getAction(lastSentVersion));
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

	/**
	 * @return the unique id of this connection.
	 */
	public UUID getUid() {
		return uid;
	}

	/**
	 * Define the equals method for this class. It depends directly to the
	 * unique id that represents this class.
	 */
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

	/**
	 * Redefine the hashcode implementation. Also depends directly in the UID.
	 */
	@Override
	public synchronized int hashCode() {
		return uid.hashCode();
	}
}