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

/**
 * Represents a client of our collaborative whiteboard application. It is the
 * main entry point of the application, managing the connection with the server.
 * It is responsible for sending and receiving messages written in our protocol
 * to the server. Also, the client should be able to select which board he wants
 * to work on, and also know who is connected to the server. Each
 * ApplicationClient has its own username too. The client connects to the
 * ApplicationServer through a socket.
 * 
 * @author Nicholas M. Mizoguchi
 * 
 */
public class ClientApplication {

    /*
     * 
     */

    private WhiteboardGUI GUI;
    private Whiteboard whiteboard;
    private final User user;
    private final Socket socket;
    private final UserListModel activeUsers;
    private final WhiteboardListModel activeWhiteboards;

    public ClientApplication(String serverAddress, int port)
            throws UnknownHostException, IOException {

        user = new User("");
        whiteboard = new Whiteboard("Default");
        activeUsers = new UserListModel();
        activeWhiteboards = new WhiteboardListModel();
        socket = new Socket(serverAddress, port);
        GUI = new WhiteboardGUI(this);
        GUI.setVisible(true);
    }

    /**
     * Listen to messages sent from the server. Also, routes the message to the
     * right object depending on its content (for instance, the Whiteboard).
     * 
     * @throws IOException
     */
    public void listen() throws IOException {

        String command;
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));

        while ((command = inputStream.readLine()) != null) {

            Protocol message = Protocol.ForClient(command);

            String action = message.getAction();

            if (action.equals("newuser")) {
                User user = new User(message.getArgument(0),
                        message.getArgument(1));
                SwingUtilities.invokeLater(new RunnableNewuser(activeUsers,
                        user));
                SwingUtilities.invokeLater(new RunnableChat(GUI, message
                        .getArgument(1) + "has entered the server"));
            }

            else if (action.equals("disconnecteduser")) {
                User user = new User(message.getArgument(0),
                        message.getArgument(1));
                SwingUtilities.invokeLater(new RunnableDisconnecteduser(
                        activeUsers, user));
                // TODO: message.getArgument(1)
                SwingUtilities.invokeLater(new RunnableChat(GUI, message
                        .getArgument(1) + "has disconnected from the server"));
            }

            else if (action.equals("whiteboards")) {
                List<String> activeBoardNames = new ArrayList<String>();
                for (int i = 0; i < message.getArgumentsSize(); i++) {
                    activeBoardNames.add(message.getArgument(i));
                }
                SwingUtilities.invokeLater(new CanvasChangeWhiteboard(
                        activeWhiteboards, activeBoardNames));
            }

            else if (action.equals("changeboard")) {
                changeWhiteboard(message.getArgument(0));
                GUI.changeWhiteboard(message.getArgument(0));
            }

            else if (action.equals("chat")) {
                SwingUtilities.invokeLater(new RunnableChat(GUI, message
                        .getArguments()));
            }

            else {
                GUI.updateModelView(command);
                whiteboard.update(command);
            }
        }
    }

    /**
     * Sends a message to the server. The message content is passed as an
     * argument.
     * 
     * @param message
     */
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

    public void changeWhiteboard(String name) {
        this.whiteboard = new Whiteboard(name);
    }

    public User getUser() {
        return user;
    }

    public UserListModel getActiveUsers() {
        return activeUsers;
    }

    public WhiteboardListModel getActiveWhiteboards() {
        return activeWhiteboards;
    }

    /**
     * Initializes the client by sending an initialization request to the
     * server.
     * 
     * @param username
     *            the desired username of this client.
     */
    private void initialize(String username) {
        getUser().setName(username);
        send(Protocol.CreateMessage(getUser(), "initialize", getUser()
                .toString()));
    }

    /**
     * Closes the connection with the server.
     * 
     * @throws IOException
     */
    private void close() throws IOException {
        socket.close();
    }

    /*
     * Main program. Make a window containing a Canvas.
     */
    public static void main(String[] args) throws UnknownHostException,
            IOException {

        // Request for the server IP
        // TODO: Check for invalid IP's and problems with the connection!
        String server = JOptionPane.showInputDialog("Server IP:");
        final ClientApplication client = new ClientApplication(server, 4444);

        // Need to initialize username before running listen method.
        String username = JOptionPane.showInputDialog("Username:");

        // Username dialog will continue to pop-up if the user clicks cancel
        while (username == null) {
            username = JOptionPane.showInputDialog("Username:");
        }

        // Initialize client by setting its username.
        client.initialize(username);

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
}
