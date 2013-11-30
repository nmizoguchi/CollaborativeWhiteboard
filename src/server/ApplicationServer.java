package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import client.Whiteboard;

public class ApplicationServer {
    // It should have a model of the whiteboard attached to it.

    private final ServerSocket serverSocket;
    private final List<Whiteboard> whiteboards;
    private final List<ClientConnection> clients;

    public ApplicationServer(int port) throws IOException {

        serverSocket = new ServerSocket(port);
        clients = Collections
                .synchronizedList(new ArrayList<ClientConnection>());
        whiteboards = Collections.synchronizedList(new ArrayList<Whiteboard>());
        // Add only one board as default
        whiteboards.add(new Whiteboard("Default"));
    }

    public static void main(String[] args) {
        int port = 4444; // default port

        ApplicationServer server;
        try {
            server = new ApplicationServer(port);
            server.serve();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

            // Start thread
            ClientConnection client = new ClientConnection(this);
            ClientHandler clientHandler = new ClientHandler(client, socket);
            clients.add(client);
            clientHandler.startThreads();
        }
    }

    public Whiteboard getWhiteboard(String name) {
        
        // name should not contain spaces.
        if(name.split(" ").length > 1) {
            throw new IllegalArgumentException("Should not contain spaces");
        }
        
        // Make a copy of the synchronized list, so we don't have problems
        List<Whiteboard> copy = new ArrayList<Whiteboard>(whiteboards);

        for (Whiteboard board : copy) {
            if (name.equals(board.getName())) {
                return board;
            }
        }

        // Not found! Create a new Whiteboard.
        Whiteboard board = new Whiteboard(name);
        whiteboards.add(board);

        return board;
    }

    public synchronized String getWhiteboardNames() {

        String names = "";
        List<Whiteboard> copy = new ArrayList<Whiteboard>(whiteboards);

        for (Whiteboard board : copy) {
            names = names + " " + board.getName();
        }

        return names.trim();
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}
