package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Handles the connection of the client. Each ClientHandler handles exactly one
 * ClientConnection. It has two threads: One responsible for receiving and
 * handling the client's messages, and other responsible for sending information
 * to the clients.
 * 
 * @author Nicholas M. Mizoguchi
 * 
 */
public class ConnectionController implements ConnectionOutputHandler {

    /*
     * 
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

    public ConnectionController(ConnectionListener l, Socket sock) {

        this.uid = UUID.randomUUID();

        this.listener = l;
        this.socket = sock;
        this.outputQueue = new LinkedBlockingQueue<String>();

        /*
         * Opens streams to communicate with the server.
         */
        try {
            inputStream = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            outputStream = new PrintWriter(socket.getOutputStream(), true);
            /*
             * Configures threads to handle incoming messages, outgoing
             * messages, and one for identifying required updates of the board
             * to send to the client.
             */
            incomingMessageHandler = new Thread(new Runnable() {
                public void run() {
                    handleIncomingMessages();
                }
            });

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
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } finally {
                l.onClientDisconnected(this);
            }
        }
    }

    /**
     * Handle a single client connection. Returns when client disconnects.
     * 
     * @param socket
     *            socket where the client is connected
     * @throws IOException
     *             if connection has an error or terminates unexpectedly
     */
    private void handleIncomingMessages() {
        try {
            for (String line = inputStream.readLine(); line != null; line = inputStream
                    .readLine()) {

                listener.onMessageReceived(this, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                inputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        // Closing the connection, disconnects client
        finally {
            listener.onClientDisconnected(this);
        }
    }

    private void handleOutgoingMessages() {

        final ConnectionController instance = this;

        // Creates the thread to check for updates
        updateWhiteboardHandler = new Thread(new Runnable() {
            public void run() {
                while (socket.isConnected())
                    listener.onCheckForUpdateRequest(instance);
            }
        });

        updateWhiteboardHandler.start();

        try {
            // Checks if there is another kind of message to send to the
            // client.
            while (socket.isConnected()) {
                String message = ((LinkedBlockingQueue<String>) outputQueue)
                        .take();
                outputStream.println(message);
            }
        } catch (InterruptedException e) {
            // TODO: Decide, but probably close the server elegantly
            e.printStackTrace();
        } finally {
            outputStream.close();
        }
    }

    public void startThreads() {
        incomingMessageHandler.start();
        outgoingMessageHandler.start();
    }

    public UUID getUid() {
        return uid;
    }

    @Override
    public void scheduleMessage(String message) {
        outputQueue.add(message);
    }

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
