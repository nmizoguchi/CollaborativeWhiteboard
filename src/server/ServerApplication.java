package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shared.models.User;
import shared.models.Whiteboard;
import Protocol.CWPMessage;

/**
 * This class represents the Model of the Server. It has the representation of
 * all connected clients and also all active whiteboards. The server's
 * whiteboards are the official ones, and all clients are always updating
 * themselves to keep consistency with the server's representation. It is also
 * the entry point of the Server application. It is a mutable class.
 * 
 * Expected interactions from commands coming from clients: // Valid messages:
 * 1. initialize -> 2. uuid disconnecteduser uuid username 3. uuid newuser uuid
 * username 4. uuid drawline arg0 ... arg5 5. uuid erase arg0 ... arg5 6. uuid
 * drawrect arg0 ... arg5 7. uuid changeboard boardname 8. uuid cleanboard
 * boardname -> supported but not implemented yet 9. uuid whiteboards arg n*
 * (may our not have arguments) 10. uuid updateuser uuid username -> supported
 * but not implemented yet 11. uuid chat message
 * 
 * @author Nicholas M. Mizoguchi
 * 
 */
public class ServerApplication implements ConnectionListener {

    /*
     * Representation Invariant: connectionMap has connections that represents
     * only active connections. Methods that require informing or updating
     * information in one of these collections cannot have the collection
     * modified in the middle, otherwise they might not be performing the action
     * correctly.
     * 
     * Thread-safe argument: whiteboardList and connectionMap uses thread-safe
     * datatypes. All methods that require inform or update all elements in one
     * of these collections cannot have the collection modified until the method
     * performs what it says it should do. To achieve this we acquires the lock
     * of the collection, so no one can modify it until it completes all
     * actions.
     */

    private final List<Whiteboard> whiteboardList;
    private final Map<ConnectionController, Connection> connectionMap;
    private final User serverUser;

    /**
     * Constructor. Creates other models that are part of the server's
     * representation.
     */
    public ServerApplication(String serverName) {

        // Server has an UUID too.
        serverUser = new User(serverName);

        whiteboardList = Collections
                .synchronizedList(new ArrayList<Whiteboard>());
        whiteboardList.add(new Whiteboard("Default"));

        connectionMap = Collections
                .synchronizedMap(new HashMap<ConnectionController, Connection>());
    }

    /**
     * Starts listening to client connections.
     */
    public void start(int port) {
        // Creates a new thread to listen to new connections.
        Thread newConnectionHandler;
        try {
            newConnectionHandler = new Thread(new ServerConnectionHandler(this,
                    port));
            newConnectionHandler.start();
            System.out.println("Server started.");
        } catch (IOException e) {
            close("Problem opening the server. Check if the server isn't already running.");
            return;
        }

        // Gives the server external IP
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine(); // you get the IP as a String
            System.out.println("Public IP: " + ip
                    + " (Given by Amazon Web Services)");

        } catch (IOException e) {
            System.out.println("You may not be connected to the internet.");
        }

        // Gives the server local IP
        String localIpAddress = "127.0.0.1";
        try {
            InetAddress thisIp = InetAddress.getLocalHost();
            localIpAddress = thisIp.getHostAddress().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Public IP: " + localIpAddress
                + " Given by Java InetAddress library)");
    }

    /**
     * Return the Whiteboard with the name passed as a parameter. If a
     * Whiteboard with the passed name doesn't exist, creates a new one, and
     * also sends a broadcast to everyone connected that a new board was
     * created.
     * 
     * @param name
     *            String with no spaces.
     * @return a Whiteboard instance with name <code>name</code>.
     */
    public Whiteboard getWhiteboard(String name) {

        /*
         * Thread safe argument: Keeps consistency of the whiteboardList by
         * asking for its lock. This way it can iterate over it, and put a new
         * one on it, guaranteeing the rep.invariant of the whiteboardList.
         */

        Whiteboard whiteboard;

        synchronized (whiteboardList) {
            for (Whiteboard board : whiteboardList) {
                if (board.getName().equals(name)) {
                    return board;
                }
            }

            // If not found, create a new board
            whiteboard = new Whiteboard(name);
            whiteboardList.add(whiteboard);

            // Let all clients know that a new board was created
            String[] names = getWhiteboardNames();
            String message = CWPMessage
                    .Encode(serverUser, "whiteboards", names);
            broadcastMessage(message);
        }

        return whiteboard;
    }

    /**
     * Adds a new Connection to this server.
     * 
     * @param controller
     *            the controller of the connection.
     * @param conn
     *            the connection itself.
     */
    public void addConnection(ConnectionController controller, Connection conn) {
        connectionMap.put(controller, conn);
    }

    /**
     * Closes the server.
     * 
     * @param error
     *            the error message that occurred when closing this server.
     */
    public void close(String error) {
        System.err.println(error);
    }

    /**
     * Broadcasts a message to all connected clients.
     * 
     * @param message
     */
    public void broadcastMessage(String message) {
        synchronized (connectionMap) {
            for (ConnectionController controller : connectionMap.keySet()) {
                controller.scheduleMessage(message);
            }
        }
    }

    /**
     * @return a list of names of all the active Whiteboards in the server.
     */
    private String[] getWhiteboardNames() {

        String[] names;

        // Guarantees that it won't break the invariant while iterating over the
        // list by acquiring the lock of the list.
        synchronized (whiteboardList) {

            names = new String[whiteboardList.size()];
            for (int i = 0; i < whiteboardList.size(); i++) {
                names[i] = whiteboardList.get(i).getName();
            }
        }

        return names;
    }

    public User getServerUser() {
        return this.serverUser;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * server.ConnectionListener#onMessageReceived(server.ConnectionController,
     * java.lang.String)
     */
    @Override
    public void onMessageReceived(ConnectionController callerController,
            String input) {

        Connection currentConnection = connectionMap.get(callerController);

        CWPMessage message = new CWPMessage(input);
        String command = message.getAction();

        if (command.equals("initialize")) {
            /*
             * Initializes a connection. Sets username of the user too. Send a
             * list of all connected users on the Default board to all users
             * connected to it. Then, sends a broadcast to everyone saying that
             * this client has connected. Finally, sends to this client the list
             * of available whiteboards.
             */

            // Makes sure that won't lose a client that is connecting by...
            synchronized (connectionMap) {

                // Sets username
                User newUser;
                newUser = new User(message.getArgument(0),
                        message.getArgument(1));
                currentConnection.setUser(newUser);

                // Sends to everyone that is connected to the "Default" board
                // that this client is active
                Whiteboard board = getWhiteboard("Default");
                String[] boardConnectedUsers = getConnectedUsersOnBoard(board);

                for (ConnectionController controller : connectionMap.keySet()) {
                    if (board.equals(connectionMap.get(controller)
                            .getActiveWhiteboard())) {
                        controller
                                .scheduleMessage(CWPMessage.Encode(serverUser,
                                        "updateusers", boardConnectedUsers));
                    }
                }

                // Broadcasts to everyone that this client connected.
                String[] args = new String[] {
                        currentConnection.getUser().getUid().toString(),
                        currentConnection.getUser().getName() };
                String broadcast = CWPMessage.Encode(serverUser, "newuser",
                        args);
                broadcastMessage(broadcast);
            }

            // Send the updated list of boards to the new client
            String[] names = getWhiteboardNames();
            String whiteboards = CWPMessage.Encode(serverUser, "whiteboards",
                    names);
            callerController.scheduleMessage(whiteboards);
        }

        else if (command.equals("changeboard")) {
            /*
             * Changes the board of the client that sended the request. Creates
             * a new board if the requested board doesn't exist yet.
             */
            if (message.getArgumentsSize() != 0) {
                // new board will be made if the user enters a board name

                Whiteboard previousBoard = currentConnection
                        .getActiveWhiteboard();

                // Change the board as requested by the client
                Whiteboard board = getWhiteboard(message.getArgument(0));
                currentConnection.setActiveWhiteboard(board, 0);

                // Notifies all users connected to the previous board and the
                // current board of the user that sent the message, by sending
                // the updated list of users in the board.
                synchronized (connectionMap) {
                    String[] boardConnectedUsers = getConnectedUsersOnBoard(board);
                    String[] previousConnectedUsers = getConnectedUsersOnBoard(previousBoard);

                    for (ConnectionController controller : connectionMap
                            .keySet()) {
                        if (board.equals(connectionMap.get(controller)
                                .getActiveWhiteboard())) {
                            controller.scheduleMessage(CWPMessage.Encode(
                                    serverUser, "updateusers",
                                    boardConnectedUsers));
                        }
                        if (previousBoard.equals(connectionMap.get(controller)
                                .getActiveWhiteboard())) {
                            controller.scheduleMessage(CWPMessage.Encode(
                                    serverUser, "updateusers",
                                    previousConnectedUsers));
                        }
                    }
                }
            }
        }

        else if (command.equals("whiteboards")) {
            /*
             * sends information to the client about which boards are available.
             * Delegates the action of sending the message to the other thread
             * by using the outputQueue.
             */
            String[] boardNames = getWhiteboardNames();
            String whiteboardsMessage = CWPMessage.Encode(serverUser, command,
                    boardNames);
            callerController.scheduleMessage(whiteboardsMessage);
        }

        else if (command.equals("chat")) {
            /*
             * Broadcasts the message to all clients, so everyone knows about
             * the message.
             */
            String chatMessage = CWPMessage.Encode(serverUser, command,
                    message.getArguments());
            broadcastMessage(chatMessage);
        }

        else {
            /*
             * Otherwise, it is a command to be routed to all other clients, so
             * updates the server model with it. The thread that is responsible
             * for running updateClient will handle it.
             */
            // VERIFY IF IT IS A MESSAGE OF PAINTING BOARDS
            currentConnection.updateActiveWhiteboard(input);
        }
    }

    private String[] getConnectedUsersOnBoard(Whiteboard board) {

        String[] users;
        List<User> userList = new ArrayList<User>();

        synchronized (connectionMap) {
            for (Connection c : connectionMap.values()) {
                if (c.getActiveWhiteboard().equals(board)) {
                    userList.add(c.getUser());
                }
            }
        }

        users = new String[userList.size() * 2];

        for (int i = 0; i < userList.size(); i++) {
            users[i * 2] = userList.get(i).getUid().toString();
            users[i * 2 + 1] = userList.get(i).getName();
        }

        return users;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * server.ConnectionListener#onCheckForUpdateRequest(server.ConnectionController
     * )
     */
    @Override
    public void onCheckForUpdateRequest(ConnectionController controller) {

        // Get the connection that is controller by the controller, and asks to
        // send an update to the client, if any available.
        try {
            connectionMap.get(controller).sendClientWhiteboardUpdate();
        } catch (Exception e) {
            // Do nothing. Connection has closed.
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * server.ConnectionListener#onClientDisconnected(server.ConnectionController
     * )
     */
    @Override
    public void onClientDisconnected(ConnectionController controller) {

        synchronized (connectionMap) {

            // Remove the connection from the map of connections.
            Connection connection = connectionMap.get(controller);
            connectionMap.remove(controller);

            // If the connection was initialized, broadcasts that the client has
            // disconnected.
            if (connection.isInitialized()) {
                String[] args = new String[] {
                        connection.getUser().getUid().toString(),
                        connection.getUser().getName() };
                String message = CWPMessage.Encode(serverUser,
                        "disconnecteduser", args);
                broadcastMessage(message);
            }
        }
    }

    /*
     * Entry point of the server.
     */
    public static void main(String[] args) {

        int port = 4444; // default port

        ServerApplication server = new ServerApplication("Server");
        server.start(port);
    }
}
