package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import client.OnlineUser;
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

            ClientConnection client = new ClientConnection(this);

            List<OnlineUser> onlineUsers = new ArrayList<OnlineUser>();

            // synchronized(clients) { There is no need! But to be safe maybe
            // it's good
            for (ClientConnection connection : clients) {
                onlineUsers.add(connection.getUser());
            }

            // This initialization was made so all threads can start after
            // everything was set up
            ClientHandler clientHandler = new ClientHandler(client, socket,
                    onlineUsers);
            client.addHandler(clientHandler);
            clients.add(client);

            // Start thread
            clientHandler.startThreads();
        }
    }

    public List<ClientConnection> getClients() {
        return clients;
    }

    public Whiteboard getWhiteboard(String name) {

        // name should not contain spaces.
        if (name.split(" ").length > 1) {
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
        Whiteboard board = createWhiteboard(name);

        return board;
    }

    private Whiteboard createWhiteboard(String name) {

        Whiteboard board = new Whiteboard(name);
        whiteboards.add(board);
        synchronized (whiteboards) {
            
            String names = getWhiteboardNames();
            synchronized (clients) {
                
                for (ClientConnection client : clients) {
                    client.invokeLater("whiteboards " + names);
                }
            }
        }

        return board;
    }

    public synchronized String getWhiteboardNames() {

        String names = "";

        synchronized (whiteboards) {
            for (Whiteboard board : whiteboards) {
                names = names + " " + board.getName();
            }
        }

        return names.trim();
    }

    public void clientHasConnected(ClientConnection connection) {
        String currentUsername = connection.getUsername();
        synchronized (clients) {
            for (ClientConnection client : clients) {
                client.invokeLater("newuser " + currentUsername);
            }
        }
    }

    public void clientHasDisconnected(ClientConnection connection) {

        String currentUsername = connection.getUsername();        
        clients.remove(connection);
        
        synchronized (clients) {
            for (ClientConnection client : clients) {
                client.invokeLater("disconnecteduser " + currentUsername);
            }
        }
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}

// ONLY THIS CLASS USES InvokeLater!
