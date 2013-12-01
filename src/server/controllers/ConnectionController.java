package server.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import shared.models.Whiteboard;
import Protocol.Protocol;
import client.OnlineUser;

/**
 * Handles the connection of the client. Each ClientHandler handles exactly one
 * ClientConnection. It has two threads: One responsible for receiving and
 * handling the client's messages, and other responsible for sending information
 * to the clients.
 * 
 * @author Nicholas M. Mizoguchi
 * 
 */
public class ConnectionController implements ConnectionMessageScheduler {

    /*
     * 
     */

    private final ConnectionMessageListener listener;
    private final Thread incomingMessageHandler;
    private final Thread outgoingMessageHandler;
    private Thread updateWhiteboardHandler;
    private final Socket socket;

    private final PrintWriter outputStream;
    private final BufferedReader inputStream;
    private final Queue<String> outputQueue;

    public ConnectionController(ConnectionMessageListener l, Socket sock) throws IOException {

        this.listener = l;
        this.socket = sock;
        this.outputQueue = new LinkedBlockingQueue<String>();

        /*
         * Opens streams to communicate with the server.
         */
        inputStream = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        outputStream = new PrintWriter(socket.getOutputStream(), true);

        /*
         * Configures threads to handle incoming messages, outgoing messages,
         * and one for identifying required updates of the board to send to the
         * client.
         */
        incomingMessageHandler = new Thread(new Runnable() {
            public void run() {
                handleIncomingMessages(socket);
            }
        });

        outgoingMessageHandler = new Thread(new Runnable() {
            public void run() {
                handleOutgoingMessages(socket);
            }
        });

        // TODO: INITIALIZE THIS CONNECTION SUCH THAT THE FIRST THING TO SEND TO
        // THE CLIENT IS THE LIST OF ALL CLIENTS THAT WERE CONNECTED BEFORE HIM
        // THIS HAS TO BE DONE AQUIRING THE LOCK OF THE LIST OF CONNECTIONS, SO
        // IT MAKES SURE THAT WHEN HE IS UPDATING ITSELF WITH THE CURRENT USERS,
        // NO NEW USERS ARE ENTERING.
    }

    /**
     * Handle a single client connection. Returns when client disconnects.
     * 
     * @param socket
     *            socket where the client is connected
     * @throws IOException
     *             if connection has an error or terminates unexpectedly
     */
    private void handleIncomingMessages(Socket socket) {
        try {
            for (String line = inputStream.readLine(); line != null; line = inputStream
                    .readLine()) {
                
                listener.onMessageReceived(this, line);
            }
        } catch(IOException e) {
            // TODO: Treat by closing connection!
        }
    }

    private void handleOutgoingMessages(Socket socket) {
        
        final ConnectionController instance = this;
        
        // Creates the thread to check for updates
        updateWhiteboardHandler = new Thread(new Runnable() {
            public void run() {
                while(true)
                    listener.onCheckForUpdateRequest(instance);
            }
        });
        
        updateWhiteboardHandler.start();

        try {
            // Checks if there is another kind of message to send to the
            // client.
            while(true) {
                String message = ((LinkedBlockingQueue<String>) outputQueue).take();
                outputStream.println(message);
            }
        } catch(InterruptedException e) {
            // TODO: Decide, but probably close the server elegantly
            e.printStackTrace();
        }
    }

    public void startThreads() {
        incomingMessageHandler.start();
        outgoingMessageHandler.start();
    }

    @Override
    public void scheduleMessage(String message) {
        outputQueue.add(message);
    }
}
