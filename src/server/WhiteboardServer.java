package server;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import client.WhiteboardModel;

public class WhiteboardServer {
    // It should have a model of the whiteboard attached to it.

    private final ServerSocket serverSocket;
    private final WhiteboardModel model;

    public WhiteboardServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        model = new WhiteboardModel();
    }

    /**
     * Run the server, listening for client connections and handling them. Never
     * returns unless an exception is thrown.
     * 
     * @throws IOException
     *             if the main server socket is broken (IOExceptions from
     *             individual clients do *not* terminate serve())
     */
    public void serve() throws IOException {
        while (true) {
            // block until a client connects
            final Socket socket = serverSocket.accept();

            // start a new thread to handle the connection
            Thread thread = new Thread(new Runnable() {
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

            // Start thread
            thread.start();
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

    public static void main(String[] args) {
        int port = 4444; // default port

        WhiteboardServer server;
        try {
            server = new WhiteboardServer(port);
            server.serve();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
