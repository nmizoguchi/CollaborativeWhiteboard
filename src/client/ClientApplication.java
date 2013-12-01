package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import shared.models.User;
import shared.models.Whiteboard;
import Protocol.Protocol;
import client.gui.WhiteboardGUI;
import client.gui.canvas.CanvasChangeWhiteboard;

public class ClientApplication {

    private final WhiteboardGUI GUI;
    private Whiteboard whiteboard;
    private final User user;
    private final Socket socket;
    private final UserListModel activeUsers;
    private final WhiteboardListModel activeWhiteboards;

    public WhiteboardListModel getActiveWhiteboards() {
        return activeWhiteboards;
    }

    public ClientApplication(String serverAddress, int port)
            throws UnknownHostException, IOException {

        user = new User("");
        whiteboard = new Whiteboard("Default");
        activeUsers = new UserListModel();
        activeWhiteboards = new WhiteboardListModel();
//        activeBoardNames.add("Default");
        socket = new Socket(serverAddress, port);
        GUI = new WhiteboardGUI(this);
        GUI.setVisible(true);
    }

    public void listen() throws IOException {

        String command;
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));

        
        while ((command = inputStream.readLine()) != null) {

            Protocol message = Protocol.ForClient(command);

            String action = message.getAction();

            if (action.equals("newuser")) {
                User user = new User(message.getArgument(0), message.getArgument(1));
                SwingUtilities.invokeLater(new RunnableNewuser(activeUsers, user));
            }

            else if (action.equals("disconnecteduser")) {
                User user = new User(message.getArgument(0), message.getArgument(1));
                SwingUtilities.invokeLater(new RunnableDisconnecteduser(
                        activeUsers, user));
            }

            else if (action.equals("whiteboards")) {
                List<String> activeBoardNames = new ArrayList<String>();
                for(int i = 0; i < message.getArgumentsSize(); i++) {
                    activeBoardNames.add(message.getArgument(i));
                }
                SwingUtilities.invokeLater(new CanvasChangeWhiteboard(
                        activeWhiteboards, activeBoardNames));
            }

            else if (action.equals("changeboard")) {
                GUI.changeWhiteboard(message.getArgument(0));   
            }
            
            else if (action.equals("chat")) {
                SwingUtilities.invokeLater(new RunnableChat(GUI,message.getArguments()));
            }

            else {
                GUI.updateModelView(command);
                whiteboard.update(command);
            }
        }
    }

    public void send(String message) {

        PrintWriter outputStream;

        try {
            outputStream = new PrintWriter(socket.getOutputStream(), true);
            outputStream.println(message);
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        socket.close();
    }

    /*
     * Main program. Make a window containing a Canvas.
     */
    public static void main(String[] args) throws UnknownHostException,
    IOException {

    	String server = JOptionPane.showInputDialog("Server IP:");
    	final ClientApplication client = new ClientApplication(server, 4444);

    	// Need to initialize username before running listen method.
    	String username = JOptionPane.showInputDialog("Username:");
    	//Username dialog will continue to pop-up if the user clicks cancel
    	while (username == null){
    		username = JOptionPane.showInputDialog("Username:");
    	}
    	client.getUser().setName(username);
    	client.send(Protocol.CreateMessage(client.getUser(), "initialize", client.getUser().toString()));

    	// Creates the listening thread, that receives messages from updates of
    	// the model
    	Thread listenerThread = new Thread(new Runnable() {

    		@Override
    		public void run() {
    			try {
    				client.listen();
    				client.close();
    			} catch (IOException e) {
    				e.printStackTrace();
                }
            }
        });

        listenerThread.start();
    }

    public User getUser() {
        return user;
    }

    public UserListModel getActiveUsers() {
        return activeUsers;
    }
    
    public void changeWhiteboard(String name) {
        this.whiteboard = new Whiteboard(name);
    }
}
