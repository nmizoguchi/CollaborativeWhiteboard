package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import Protocol.Protocol;
import client.OnlineUser;
import client.Whiteboard;

/**
 * Handles the connection of the client. Each ClientHandler handles exactly one
 * ClientConnection. It has two threads: One responsible for receiving and
 * handling the client's messages, and other responsible for sending information
 * to the clients.
 * 
 * @author Nicholas M. Mizoguchi
 * 
 */
public class ClientHandler {

    /*
     * 
     */

    private final ClientConnection client;
    private final Thread incomingHandler;
    private final Thread outgoingHandler;
    private final Thread whiteboardUpdater;
    private final Socket socket;

    private final PrintWriter outputStream;
    private final BufferedReader inputStream;
    private final Queue<String> outputQueue;

    public ClientHandler(ClientConnection client, Socket sock,
            List<OnlineUser> users) throws IOException {

        this.client = client;
        this.socket = sock;

        this.outputQueue = new LinkedBlockingQueue<String>();

        // Stream where server receives messages from Clients
        inputStream = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));

        // Stream where server sends information to Clients
        outputStream = new PrintWriter(socket.getOutputStream(), true);

        incomingHandler = new Thread(new Runnable() {
            public void run() {
                // Producer thread.
                handleIncomingMessages(socket);
            }
        });

        outgoingHandler = new Thread(new Runnable() {
            public void run() {
                // Consumer thread. Keeps sending all messages in outputQueue to
                // the client.
                handleOutgoingMessages(socket);
            }
        });

        // start a new thread to handle the connection
        whiteboardUpdater = new Thread(new Runnable() {
            public void run() {
                // Check for changes in the server-side whiteboard, if versions
                // differ, then put updates to be sent through outputQueue
                handleWhiteboardUpdates();
            }
        });

        // Puts initial information to be sent to the client.
        for (OnlineUser newUser : users) {
            outputQueue.add("newuser " + newUser.getName());
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
    private void handleIncomingMessages(Socket socket) {

        try {
            for (String line = inputStream.readLine(); line != null; line = inputStream
                    .readLine()) {

                // Transform the message in tokens so we can analyze them
                String[] tokens = Protocol.CheckAndFormat(line);
                String command = tokens[0];

                if (command.equals("initialize")) {
                    /*
                     * Initializes a connection. Sets username of the user too.
                     */
                    client.initialize(tokens[1]);
                }

                else if (command.equals("changeboard")) {
                    /*
                     * Makes sure that it is not sending updating information to
                     * this client anymore by getting the outputStream lock
                     */
                    synchronized (outputStream) {
                        String boardName = tokens[1];
                        client.setActiveBoard(boardName);
                    }
                }

                else if (command.equals("getboards")) {
                    /*
                     * sends information to the client about which boards are
                     * available. Delegates the action of sending the message to
                     * the other thread by using the outputQueue.
                     */
                    String boardNames = client.getWhiteboardNames();
                    outputQueue.add("getboards " + boardNames);
                }

                else {
                    /*
                     * Otherwise, it is a command to be routed to all other
                     * clients, so updates the server model with it. The thread
                     * that is responsible for running updateClient will handle
                     * it.
                     */
                    client.getActiveBoard().update(line);
                }
            }
        } catch (IOException e) {
            client.closingConnection();
        }

        finally {
            try {
                inputStream.close();
                socket.close();
                client.closingConnection();
            } catch (IOException e) {
                client.closingConnection();
            }
        }
    }

    private void handleOutgoingMessages(Socket socket) {

            while (true) {
                // Checks if there is another kind of message to send to the
                // client.
                try {
                    String message = outputQueue.remove();
                    outputStream.println(message);
                } catch (NoSuchElementException e) {
                    // Do nothing
                }

                // Let other threads work
                outgoingHandler.yield();
            }
    }

    private void handleWhiteboardUpdates() {

        while (true) {
            // Sends updates regarding changes in the board.
            if (client.getClientBoardVersion() < client.getActiveBoard()
                    .getVersion()) {
                Whiteboard active = client.getActiveBoard();
                outputQueue
                        .add(active.getAction(client.getClientBoardVersion()));
                client.setClientBoardVersion(client.getClientBoardVersion() + 1);
            }
        }
    }

    public void startThreads() {
        incomingHandler.start();
        outgoingHandler.start();
        whiteboardUpdater.start();
    }

    public void invokeLater(String message) {
        outputQueue.add(message);
    }
}
