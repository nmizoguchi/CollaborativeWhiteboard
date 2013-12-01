package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import server.controllers.ConnectionController;
import server.controllers.ConnectionMessageListener;
import server.controllers.NewConnectionHandler;
import shared.models.Whiteboard;
import Protocol.Protocol;

public class ServerApplication implements ConnectionMessageListener {

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
        
        synchronized(whiteboardList) {
            for(Whiteboard board : whiteboardList) {
                if(board.getName().equals(name)){
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
        synchronized(whiteboardList) {
            for(Whiteboard board : whiteboardList){
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
            String broadcast = Protocol.CreateServerMessage("newuser", message.getArguments(0));
            broadcastMessage(broadcast);
            
            // Send the updated list of boards to the new client
            String names = getWhiteboardNames();
            String whiteboards = Protocol.CreateServerMessage("whiteboards", names);
            callerController.scheduleMessage(whiteboards);
        }

        else if (command.equals("changeboard")) {
            /*
             * Makes sure that it is not sending updating information to
             * this client anymore by getting the outputStream lock
             */
            Whiteboard board = getWhiteboard(message.getArguments(0));
            currentConnection.setActiveWhiteboard(board, 0);
        }

        else if (command.equals("whiteboards")) {
            /*
             * sends information to the client about which boards are
             * available. Delegates the action of sending the message to
             * the other thread by using the outputQueue.
             */
            String boardNames = getWhiteboardNames();
            callerController.scheduleMessage(command+boardNames);
        }

        else {
            /*
             * Otherwise, it is a command to be routed to all other
             * clients, so updates the server model with it. The thread
             * that is responsible for running updateClient will handle
             * it.
             */
            // VERIFY IF IT IS A MESSAGE OF PAINTING BOARDS
            currentConnection.updateActiveWhiteboard(message.getPaintAction());
        }
    }
    
    private void broadcastMessage(String message) {
        synchronized(connectionMap) {
            for(ConnectionController controller : connectionMap.keySet()) {
                controller.scheduleMessage(message);
            }
        }
    }

    public static void main(String[] args) {
        int port = 4444; // default port

        ServerApplication server = new ServerApplication();
        
        /*
         * From here starts the connection listening automatically
         */
        try {
            Thread newConnectionHandler = new Thread(new NewConnectionHandler(server, port));
            newConnectionHandler.start();
        } catch (IOException e) {
            e.printStackTrace();
            server.close("Problem creating the Server Socket.");
            return;
        }
        System.out.println("Server started.");
    }

    @Override
    public void onCheckForUpdateRequest(ConnectionController controller) {
        connectionMap.get(controller).sendClientWhiteboardUpdate();
    }
}
