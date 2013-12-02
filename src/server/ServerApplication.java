package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import shared.models.User;
import shared.models.Whiteboard;
import Protocol.Protocol;

/**
 * This class represents the Model of the Server. It is also the entry point of
 * the Server application.
 * 
 * @author Nicholas M. Mizoguchi
 * 
 */
public class ServerApplication implements ConnectionListener {

    private final List<Whiteboard> whiteboardList;
    private final Map<ConnectionController, Connection> connectionMap;

    /**
     * Constructor. Creates other models that are part of the server's
     * representation.
     */
    public ServerApplication() {

        whiteboardList = Collections
                .synchronizedList(new ArrayList<Whiteboard>());
        whiteboardList.add(new Whiteboard("Default"));

        connectionMap = Collections
                .synchronizedMap(new HashMap<ConnectionController, Connection>());
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
            String names = getWhiteboardNames();
            String message = Protocol.CreateServerMessage("whiteboards", names);
            broadcastMessage(message);
        }

        return whiteboard;
    }

    /**
     * Add a new Connection to this server.
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
     * @return a list of names of all the active Whiteboards in the server.
     */
    private String getWhiteboardNames() {

        String names = "";

        // Guarantees that it won't break the invariant while iterating over the
        // list by aquiring the lock of the list.
        synchronized (whiteboardList) {
            for (Whiteboard board : whiteboardList) {
                names = names + " " + board.getName();
            }
        }

        return names;
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

        Protocol message = Protocol.ForServer(input);
        String command = message.getAction();

        if (command.equals("initialize")) {
            /*
             * Initializes a connection. Sets username of the user too.
             */

            // Makes sure that won't lose a client that is connecting by...
            synchronized (connectionMap) {
                User newUser;
                if (message.getArgumentsSize() == 2) {
                    newUser = new User(message.getArgument(0),
                            message.getArgument(1));
                } else {
                    // If user selects 'OK' without entering a username
                    // then they will receive an automatically generated
                    // username with the format of:
                    // Anonymous[random int from 0-10000]
                    newUser = new User(message.getArgument(0), "Anonymous"
                            + (int) Math.floor(Math.random() * 10000));
                }
                currentConnection.setUser(newUser);

                // Broadcasts to everyone that this client connected.
                String broadcast = Protocol.CreateServerMessage("newuser",
                        currentConnection.getUser().toString());
                broadcastMessage(broadcast);

                for (Connection conn : connectionMap.values()) {
                    // Get initialized connections and that are not the same as
                    // the caller
                    if (conn.isInitialized()
                            && !conn.getUser().equals(
                                    currentConnection.getUser())) {
                        String sendUser = Protocol.CreateServerMessage(
                                "newuser", conn.getUser().toString());
                        callerController.scheduleMessage(sendUser);
                    }
                }
            }

            // Send the updated list of boards to the new client
            String names = getWhiteboardNames();
            String whiteboards = Protocol.CreateServerMessage("whiteboards",
                    names);
            callerController.scheduleMessage(whiteboards);
        }

        else if (command.equals("changeboard")) {
            /*
             * Makes sure that it is not sending updating information to this
             * client anymore by getting the outputStream lock
             */
            if (message.getArgumentsSize() != 0) {
                // new board will be made if the user enters a board name
                Whiteboard board = getWhiteboard(message.getArgument(0));
                currentConnection.setActiveWhiteboard(board, 0);
            }
        }

        else if (command.equals("whiteboards")) {
            /*
             * sends information to the client about which boards are available.
             * Delegates the action of sending the message to the other thread
             * by using the outputQueue.
             */
            String boardNames = getWhiteboardNames();
            callerController.scheduleMessage(command + boardNames);
        }

        else if (command.equals("chat")) {
            /*
             * Broadcasts the message to all clients, so everyone knows about
             * the message.
             */
            String chatMessage = Protocol.CreateServerMessage(command,
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
            currentConnection.updateActiveWhiteboard(message.getPaintAction());
        }
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
                String message = Protocol.CreateServerMessage(
                        "disconnecteduser", connection.getUser().toString());
                broadcastMessage(message);
            }
        }
    }

    /**
     * Entry point of the server.
     */
    public static void main(String[] args) {
        int port = 4444; // default port

        ServerApplication server = new ServerApplication();

        // Creates a new thread to listen to new connections.
        Thread newConnectionHandler;
        try {
            newConnectionHandler = new Thread(new ServerConnectionHandler(
                    server, port));
            newConnectionHandler.start();
            System.out.println("Server started.");
        } catch (IOException e) {
            server.close("Server Socket stopped listening.");
            e.printStackTrace();
        }
    }
}
