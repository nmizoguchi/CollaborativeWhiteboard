package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Handles the connection of the client. Each ConnectionController handles
 * exactly one Connection. It has three threads: One responsible for receiving
 * and client's messages, other responsible for sending information to the
 * clients, and another one responsible for updates to be sent to the client
 * regarding the active board it is working on.
 * 
 * This controller handles all incoming and outgoing packages from client and
 * server.
 * 
 * @author Nicholas M. Mizoguchi
 * 
 */
public class ConnectionController implements ConnectionOutputHandler {

	/*
	 * Rep. Invariant: The outputQueue has the first-in-first-out policy.
	 * 
	 * Thread-safe argument: The only point that we have concurrent access is
	 * the method scheduleMessage. Since it uses a thread-safe datatype, it is
	 * thread-safe. The equals and hashcode methods are also threadsafe since
	 * they require the lock of the object.
	 */
	private final UUID uid;
	private final ConnectionListener listener;
	private Thread incomingMessageHandler;
	private Thread outgoingMessageHandler;
	private Thread updateWhiteboardHandler;
	private final Socket socket;

	private final Queue<String> outputQueue;
	private PrintWriter outputStream;
	private BufferedReader inputStream;

	/**
	 * Constructor.
	 * 
	 * @param l
	 *            the listener that has callbacks so we can send connection
	 *            information to.
	 * @param sock
	 *            the socket of the connection.
	 */
	public ConnectionController(ConnectionListener l, Socket sock) {

		this.uid = UUID.randomUUID();

		this.listener = l;
		this.socket = sock;
		this.outputQueue = new LinkedBlockingQueue<String>();

		/*
		 * Opens streams to communicate with the server. Configures threads to
		 * handle incoming messages, outgoing messages, and one for identifying
		 * required updates of the board to send to the client.
		 */
		try {
			// Opening streams
			inputStream = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			outputStream = new PrintWriter(socket.getOutputStream(), true);

			/* The only method this thread access is handleIncomingMessages. */
			incomingMessageHandler = new Thread(new Runnable() {
				public void run() {
					handleIncomingMessages();
				}
			});

			/* The only method this thread access is handleOutgoingMessages. */
			outgoingMessageHandler = new Thread(new Runnable() {
				public void run() {
					handleOutgoingMessages();
				}
			});

			// TODO: INITIALIZE THIS CONNECTION SUCH THAT THE FIRST THING TO
			// SEND TO THE CLIENT IS THE LIST OF ALL CLIENTS THAT WERE CONNECTED
			// BEFORE HIM THIS HAS TO BE DONE AQUIRING THE LOCK OF THE LIST OF
			// CONNECTIONS, SO IT MAKES SURE THAT WHEN HE IS UPDATING ITSELF
			// WITH THE CURRENT USERS, NO NEW USERS ARE ENTERING.

		} catch (IOException e) {
			e.printStackTrace();
			try {
				inputStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				l.onClientDisconnected(this);
			}
		}
	}

	/**
	 * Handles the input stream of the connection. It is responsible for
	 * receiving messages from the client and informing it through the listener
	 * about what it received. In other words, delegate the treatment of the
	 * messages using the callbacks of the listener.
	 * 
	 * @param socket
	 *            socket where the client is connected
	 */
	private void handleIncomingMessages() {

		// Keeps listening to the input stream, if it fails, informs that the
		// client has disconnected (using the listener).
		try {
			for (String line = inputStream.readLine(); line != null; line = inputStream
					.readLine()) {

				// Informs listener of a received message
				listener.onMessageReceived(this, line);
			}
		}

		catch (IOException e) {
			try {
				inputStream.close();
			} catch (IOException e1) {
			}
		}

		// Closing the connection, informs listener that the client has
		// disconnected.
		finally {
			listener.onClientDisconnected(this);
		}
	}

	/**
	 * Handles the output stream of this connection. If there is a message to be
	 * sent to the client, keep sending. Block otherwise. Also, creates the
	 * thread responsible for checking updates in the board that the client is
	 * working on.
	 */
	private void handleOutgoingMessages() {
		/*
		 * The updater thread is defined here so it can start checking for
		 * updates in the board only when it starts handling outgoing messages.
		 * This way we prevent that it starts checking for updates before.
		 */

		final ConnectionController instance = this;

		// Creates the thread to check for updates
		updateWhiteboardHandler = new Thread(new Runnable() {
			public void run() {
				while (socket.isConnected())
					listener.onCheckForUpdateRequest(instance);
			}
		});

		// Starts updater thread.
		updateWhiteboardHandler.start();

		// Checks if there is a message to be sent. If it has, send it. Block
		// otherwise.
		try {
			while (socket.isConnected()) {
				String message = ((LinkedBlockingQueue<String>) outputQueue)
						.take();
				outputStream.println(message);
			}

			// Checks for checked exceptions that could be raised.
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			outputStream.close();
		}
	}

	/**
	 * Starts the streams between client and server.
	 */
	public void startThreads() {
		incomingMessageHandler.start();
		outgoingMessageHandler.start();
	}

	/**
	 * @return the unique ID that represents this controller.
	 */
	public UUID getUid() {
		return uid;
	}

	/**
	 * Schedules a message to be sent to the client.
	 * 
	 * @param the
	 *            message itself.
	 */
	@Override
	public void scheduleMessage(String message) {
		outputQueue.add(message);
	}

	/**
	 * Checks if two controllers are the same or not.
	 */
	@Override
	public synchronized boolean equals(Object o) {
		if (!(o instanceof ConnectionController)) {
			return false;
		}

		ConnectionController that = (ConnectionController) o;
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
