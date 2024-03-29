package client;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import client.gui.WhiteboardGUI;

public class Main {
	/**
	 * Main program. Make a window containing a Canvas.
	 */
	public static void main(String[] args) throws UnknownHostException,
			IOException {

		// Request for the server IP
		String server = JOptionPane.showInputDialog("Server IP:");
		final ClientApplication client = new ClientApplication(server, 4444);

		// Username dialog will continue to pop-up if the user clicks cancel
		String username;
		do {
			username = JOptionPane.showInputDialog("Username:");
		} while (username == null);
		if (username.equals("")) {
			username = "Anonymous" + (int) Math.floor(Math.random() * 10000);
		}

		/*
		 * Set the look and feel to the Windows Vista layout, so everyone sees
		 * the same layout
		 */
		try {
			// Set cross-platform Java L&F (also called "Metal")
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
		
		WhiteboardGUI GUI = new WhiteboardGUI(client);

		// Important, otherwise don't create the image buffer.
		GUI.setVisible(true);

		// Initialize the client. To initialize a client, we need a username.
		client.initialize(GUI, username);

		GUI.setVisible(true);

	}
}
