package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import client.Whiteboard;

public class ClientHandler {

    private final ClientConnection client;
    private final Thread messageHandler;
    private final Thread updateHandler;
    private final Socket socket;

    private final PrintWriter outputStream;
    private final BufferedReader inputStream;

    public ClientHandler(ClientConnection client, Socket sock)
            throws IOException {

        this.client = client;
        this.socket = sock;

        // Stream where server receives messages from Clients
        inputStream = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));

        // Stream where server sends information to Clients
        outputStream = new PrintWriter(socket.getOutputStream(), true);

        // start a new thread to handle the connection
        messageHandler = new Thread(new Runnable() {
            public void run() {
                // the client socket object is now owned by this thread,
                // and mustn't be touched again in the main thread
                try {
                    handleConnection(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // start a new thread to handle the connection
        updateHandler = new Thread(new Runnable() {
            public void run() {
                // the client socket object is now owned by this thread,
                // and mustn't be touched again in the main thread
                try {
                    updateClient(socket);
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
    private void handleConnection(Socket socket) throws IOException {

        try {
            for (String line = inputStream.readLine(); line != null; line = inputStream
                    .readLine()) {

                // Transform the message in tokens so we can analyze them
                String[] tokens = handleRequest(line);
                String command = tokens[0];

                if (command.equals("changeboard")) {

                    /*
                     * Makes sure that it is not sending updating information to
                     * this client anymore by getting the outputStream lock
                     */
                    synchronized (outputStream) {
                        String boardName = tokens[1];
                        try {
                            client.setActiveBoard(boardName);
                        } catch (NoSuchFieldException e) {
                            // Treat case where don't find a board!
                            e.printStackTrace();
                        }
                    }
                }

                if (command.equals("getboards")) {
                    /*
                     * sends information to the client about which boards are
                     * available. Requires the output stream to send information
                     * to the client
                     */
                    synchronized (outputStream) {
                        String boardNames = client.getWhiteboardNames();
                        outputStream.println("getboards " + boardNames);
                    }
                }

                else {

                    // Otherwise, it is a command to be routed to all other
                    // clients,
                    // so updates the server model with it
                    client.getActiveBoard().update(line);
                }
            }
        }

        finally {
            inputStream.close();
            socket.close();
        }
    }

    /**
     * Handler for client input, performing requested operations and returning
     * an output message.
     * 
     * @param input
     *            message from client
     * @return message to client
     */
    private String[] handleRequest(String input) {
        String regex = "(update -?\\d+)|"
                + "(drawline -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+)|"
                + "(erase -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+)|"
                + "(drawrect -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+)|"
                + "(help)|(bye)";
        if (!input.matches(regex)) {
            // invalid input
            System.out.println(input);
            throw new UnsupportedOperationException();
        }

        String[] tokens = input.split(" ");

        return tokens;
    }

    public void updateClient(Socket socket) throws IOException,
            InterruptedException {

        try {
            while (true) {
                if (client.getClientBoardVersion() < client.getActiveBoard()
                        .getVersion()) {

                    // Get the lock for broadcasting
                    synchronized (outputStream) {
                        Whiteboard active = client.getActiveBoard();
                        outputStream.println(active.getAction(client.getClientBoardVersion()));
                        client.setClientBoardVersion(client.getClientBoardVersion()+1);
                    }
                }
                updateHandler.yield();
            }
        }

        finally {
            System.out
                    .println("Should never print this, unless is closing the connection!");
            outputStream.close();
        }
    }

    public void startThreads() {
        messageHandler.start();
        updateHandler.start();
    }
}
