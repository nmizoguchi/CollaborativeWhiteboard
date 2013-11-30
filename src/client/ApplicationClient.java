package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Protocol.Protocol;

public class ApplicationClient {

    private final WhiteboardGUI GUI;
    private Whiteboard whiteboard;
    private final Socket socket;
    private final OnlineUserListModel activeUsers;

    public ApplicationClient(String serverAddress, int port)
            throws UnknownHostException, IOException {

        whiteboard = new Whiteboard("Default");
        activeUsers = new OnlineUserListModel();
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
                // TODO: PASSES TO THE GUI TO UPDATE
            }

            else if (tokens[0].equals("changeboard")) {
                // TODO: PASSES TO THE GUI SO IT CAN RESET THE BOARD! IF TREAT
                // HERE, IS NOT GONNA GET ANOTHER MESSAGE. IS CONSISTENT
                whiteboard = new Whiteboard(tokens[1]);
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
}
