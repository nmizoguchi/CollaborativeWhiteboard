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

import client.gui.WhiteboardGUI;
import client.gui.canvas.CanvasChangeWhiteboard;
import Protocol.Protocol;

public class ApplicationClient {

    private final WhiteboardGUI GUI;
    private Whiteboard whiteboard;
    private final Socket socket;
    private final OnlineUserListModel activeUsers;
    private final WhiteboardListModel activeWhiteboards;

    public WhiteboardListModel getActiveWhiteboards() {
        return activeWhiteboards;
    }

    public ApplicationClient(String serverAddress, int port)
            throws UnknownHostException, IOException {

        whiteboard = new Whiteboard("Default");
        activeUsers = new OnlineUserListModel();
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

            String[] tokens = Protocol.CheckAndFormat(command);

            if (tokens[0].equals("newuser")) {
                SwingUtilities.invokeLater(new ExecuteNewuser(activeUsers,
                        tokens[1]));
            }

            else if (tokens[0].equals("disconnecteduser")) {
                OnlineUser user = new OnlineUser(tokens[1]);
                SwingUtilities.invokeLater(new ExecuteDisconnecteduser(
                        activeUsers, user));
            }

            else if (tokens[0].equals("whiteboards")) {
                List<String> activeBoardNames = new ArrayList<String>();
                for(int i = 1; i < tokens.length; i++) {
                    activeBoardNames.add(tokens[i]);
                }
                SwingUtilities.invokeLater(new CanvasChangeWhiteboard(
                        activeWhiteboards, activeBoardNames));
            }

            else if (tokens[0].equals("changeboard")) {
                // TODO: PASSES TO THE GUI SO IT CAN RESET THE BOARD! IF TREAT
                // HERE, IS NOT GONNA GET ANOTHER MESSAGE. IS CONSISTENT
                GUI.changeWhiteboard(tokens[1]);
                
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
        final ApplicationClient client = new ApplicationClient(server, 4444);

        // Need to initialize username before running listen method.

        String username = JOptionPane.showInputDialog("Username:");

        client.send("initialize " + username);

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

    public OnlineUserListModel getActiveUsers() {
        return activeUsers;
    }
    
    public void changeWhiteboard(String name) {
        this.whiteboard = new Whiteboard(name);
    }
}
