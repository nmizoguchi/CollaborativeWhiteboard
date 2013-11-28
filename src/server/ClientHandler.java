package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import client.WhiteboardModel;

public class ClientHandler {

    private final ClientConnection client;
    private final Thread messageHandler;
    private final Thread updateHandler;
    private final Socket socket;
    private Object broadcastLock = new Object();

    public ClientHandler(ClientConnection client, Socket sock) {

        this.client = client;
        this.socket = sock;

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

        // Stream where server receives messages from Clients
        BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));

        // Stream where server sends information to Clients
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        try {
            for (String line = in.readLine(); line != null; line = in
                    .readLine()) {

                // Transform the message in tokens so we can analyze them
                String[] tokens = handleRequest(line);
                String command = tokens[0];

                if (command.equals("changeboard")) {

                    /*
                     * Makes sure that it is not sending updating information to
                     * this client anymore by getting the broadcastLock
                     */
                    synchronized (broadcastLock) {
                        String boardName = tokens[1];
                        client.setActiveModel(boardName);
                    }
                }

                else {

                    // Otherwise, it is a command to be routed to all other
                    // clients,
                    // so updates the server model with it
                    client.getActiveModel().update(line);
                }
            }
        }

        finally {
            out.close();
            in.close();
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

        // Stream where server sends information to Clients
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        try {
            while (true) {
                if (client.getActiveModelVersion() < client.getActiveModel()
                        .getVersion()) {

                    // Get the lock for broadcasting
                    synchronized (broadcastLock) {
                        List<String> toUpdate = client.getActiveModel()
                                .getActionsToUpdate(
                                        client.getActiveModelVersion());
                        client.setActiveModelVersion(client
                                .getActiveModelVersion() + toUpdate.size());
                        for (String action : toUpdate) {
                            out.println(action);
                        }
                    }
                }
                updateHandler.yield();
            }
        } catch (Exception e) {
            System.out.println("Should never print this !!");
            e.printStackTrace();
        }

        finally {
            System.out.println("Should never print this!");
            out.close();
            socket.close();
        }
    }

    public void startThreads() {
        messageHandler.start();
        updateHandler.start();
    }
}
