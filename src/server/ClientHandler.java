package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import Protocol.Protocol;
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
    private final Socket socket;

    private final PrintWriter outputStream;
    private final BufferedReader inputStream;
    private final Queue<String> outputQueue;

    public ClientHandler(ClientConnection client, Socket sock)
            throws IOException {

        this.client = client;
        this.socket = sock;

        this.outputQueue = new LinkedBlockingQueue<String>();

        // Stream where server receives messages from Clients
        inputStream = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));

        // Stream where server sends information to Clients
        outputStream = new PrintWriter(socket.getOutputStream(), true);

        // start a new thread to handle the connection
        incomingHandler = new Thread(new Runnable() {
            public void run() {
                // the client socket object is now owned by this thread,
                // and mustn't be touched again in the main thread
                try {
                    handleIncomingMessages(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // start a new thread to handle the connection
        outgoingHandler = new Thread(new Runnable() {
            public void run() {
                // the client socket object is now owned by this thread,
                // and mustn't be touched again in the main thread
                try {
                    handleOutgoingMessages(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Handle a single client connection. Returns when client disconnects.
     * 
     * @param socket
     *            socket where the client is connected
     * @throws IOException
     *             if connection has an error or terminates unexpectedly
     */
    private void handleIncomingMessages(Socket socket) throws IOException {

        try {
            for (String line = inputStream.readLine(); line != null; line = inputStream
                    .readLine()) {

                // Transform the message in tokens so we can analyze them
                String[] tokens = Protocol.CheckAndFormat(line);
                String command = tokens[0];

                if (command.equals("changeboard")) {
                    /*
                     * Makes sure that it is not sending updating information to
                     * this client anymore by getting the outputStream lock
                     */
                    synchronized (outputStream) {
                        String boardName = tokens[1];
                        client.setActiveBoard(boardName);
                    }
                }

                if (command.equals("getboards")) {
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
        }

        finally {
            inputStream.close();
            socket.close();
        }
    }

    private void handleOutgoingMessages(Socket socket) throws IOException,
            InterruptedException {

        try {
            while (true) {

                // Sends updates regarding changes in the board.
                if (client.getClientBoardVersion() < client.getActiveBoard()
                        .getVersion()) {
                    Whiteboard active = client.getActiveBoard();
                    outputStream.println(active.getAction(client
                            .getClientBoardVersion()));
                    client.setClientBoardVersion(client.getClientBoardVersion() + 1);
                }

                // Checks if there is another kind of message to send to the
                // client.
                try {
                    String message = outputQueue.remove();
                } catch (NoSuchElementException e) {
                    // Do nothing
                }

                // Let other threads work
                outgoingHandler.yield();
            }
        }

        finally {
            System.out
                    .println("Should never print this, unless is closing the connection!");
            outputStream.close();
        }
    }

    public void startThreads() {
        incomingHandler.start();
        outgoingHandler.start();
    }
}
