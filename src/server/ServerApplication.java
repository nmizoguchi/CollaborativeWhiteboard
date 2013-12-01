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

public class ServerApplication implements ConnectionListener {

    private final List<Whiteboard> whiteboardList;
    private final Map<ConnectionController, Connection> connectionMap;

    public ServerApplication() {

        whiteboardList = Collections
                .synchronizedList(new ArrayList<Whiteboard>());
        whiteboardList.add(new Whiteboard("Default"));

        connectionMap = Collections
                .synchronizedMap(new HashMap<ConnectionController, Connection>());
    }

    public Whiteboard getWhiteboard(String name) {

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

    public void addConnection(ConnectionController controller, Connection conn) {
        connectionMap.put(controller, conn);
    }

    public void close(String error) {
        System.err.println(error);
    }

    private String getWhiteboardNames() {

        String names = "";
        synchronized (whiteboardList) {
            for (Whiteboard board : whiteboardList) {
                names = names + " " + board.getName();
            }
        }

        return names;
    }

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
        		if (message.getArgumentsSize() == 2){
        			newUser = new User(message.getArgument(0),
        					message.getArgument(1));
        		} else {
        			//If user selects 'OK' without entering a username
        			//then they will receive an automatically generated username with the format of:
        			//Anonymous[random double from 0-10000]
        			newUser = new User(message.getArgument(0), "Anonymous" + Math.random()*10000);
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
        	if (message.getArgumentsSize() != 0){
        		//new board will be made if the user enters a board name
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
            String chatMessage = Protocol.CreateServerMessage(command, message.getArguments());
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

    public void broadcastMessage(String message) {
        synchronized (connectionMap) {
            for (ConnectionController controller : connectionMap.keySet()) {
                controller.scheduleMessage(message);
            }
        }
    }

    public void clientDisconnected(String uid, String name) {

    }

    public static void main(String[] args) {
        int port = 4444; // default port

        ServerApplication server = new ServerApplication();

        /*
         * From here starts the connection listening automatically
         */

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

    @Override
    public void onCheckForUpdateRequest(ConnectionController controller) {
        try {
            connectionMap.get(controller).sendClientWhiteboardUpdate();
        } catch (Exception e) {
            // Do nothing. Connection has closed.
        }
    }

    @Override
    public void onClientDisconnected(ConnectionController controller) {
        synchronized (connectionMap) {
            Connection connection = connectionMap.get(controller);
            connectionMap.remove(controller);
            // TODO: Stop threads somehow?
            try{
            if (connection.getUser().toString() != null){
            String message = Protocol.CreateServerMessage("disconnecteduser",
                    connection.getUser().toString());
            broadcastMessage(message);
            
            //TODO: put this message on the chat message in GUI
            System.out.println(connection.getUser().getName()
                    + " has disconnected from the server.");
            }
            }catch (NullPointerException n){}
        }
    }
}
