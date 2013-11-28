package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import client.WhiteboardModel;

public class ClientHandler {

    private final WhiteboardModel model;
    private final Thread messageHandler;
    private final Thread updateHandler;
    private final Socket socket;
    private int currentVersion;

    public ClientHandler(WhiteboardModel model, Socket serverSocket) {

        currentVersion = 0;
        this.model = model;
        this.socket = serverSocket;

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

        messageHandler.start();
        updateHandler.start();
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
                String[] outputTokens = handleRequest(line);
                String command = outputTokens[0];

                // Check if it is a request to retrieve updates
                if (command.equals("update")) {

                    int clientVersion = Integer.valueOf(outputTokens[1]);

                    for (int i = clientVersion + 1; i < model.getVersion(); i++) {
                        // Get all shapes, returning them to the user.
                        out.println(model.getAction(i));
                        System.out.println("Sent updates to the client.");
                    }
                }

                else {
                    // Treat correctly the model itself.
                    model.update(line);
                    System.out.println(model.getVersion());
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
        String regex = "(update -?\\d+)|(drawline -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+)|"
                + "(erase -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+)|(help)|(bye)";
        if (!input.matches(regex)) {
            // invalid input
            throw new UnsupportedOperationException();
        }

        String[] tokens = input.split(" ");

        return tokens;
    }

    public void updateClient(Socket socket) throws IOException, InterruptedException {

        // Stream where server sends information to Clients
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        try {
            while (true) {
                if(currentVersion < model.getVersion()) {
                    List<String> toUpdate = model.getActionsToUpdate(currentVersion);
                    for(String action : toUpdate) {
                        out.println(action);
                    }
                }
            }
        }

        finally {
            out.close();
            socket.close();
        }
    }
}
