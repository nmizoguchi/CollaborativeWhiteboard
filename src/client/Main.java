package client;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import client.gui.WhiteboardGUI;

public class Main {
    /*
     * Main program. Make a window containing a Canvas.
     */
    public static void main(String[] args) throws UnknownHostException,
            IOException {

        // Request for the server IP
        // TODO: Check for invalid IP's and problems with the connection!
        String server = JOptionPane.showInputDialog("Server IP:");
        final ClientApplication client = new ClientApplication(server, 4444);

        WhiteboardGUI GUI = new WhiteboardGUI(client);
        GUI.setVisible(true);

        // Username dialog will continue to pop-up if the user clicks cancel
        String username;
        do {
            username = JOptionPane.showInputDialog("Username:");
        } while (username == null);

     // Initialize client by setting its username.
        client.initialize(GUI, username);
    }
}
