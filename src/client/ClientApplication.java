package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import shared.models.User;
import shared.models.Whiteboard;
import Protocol.CWPMessage;
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
    private ClientListener listener;
    private Whiteboard whiteboard;
    private final User user;
    private final Socket socket;
    private final Queue<String> outputQueue;

    public ClientApplication(String serverAddress, int port)
            throws UnknownHostException, IOException {

        user = new User("");
        whiteboard = new Whiteboard("Default");
        socket = new Socket(Inet4Address.getByName(serverAddress), port);
        this.outputQueue = new LinkedBlockingQueue<String>();
    }

    /**
     * Listen to messages sent from the server. Also, routes the message to the
     * right object depending on the action found in the message. Possible
     * actions include "newuser", "disconnecteduser", "whiteboards",
     * "changeboard", and "chat".
     * 
     * @throws IOException
     */
    public void listen() throws IOException {

        String command;
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));

        while ((command = inputStream.readLine()) != null) {

            try {
                CWPMessage message = new CWPMessage(command);

                String action = message.getAction();

                if (action.equals("newuser")) {
                    listener.onNewuserMessageReceived(message);
                }

                else if (action.equals("disconnecteduser")) {
                    listener.onDisconnecteduserMessageReceived(message);
                }

                else if (action.equals("updateusers")) {
                    listener.onReceiveUpdatedUsersOnBoard(message);
                }

                else if (action.equals("whiteboards")) {

                    listener.onWhiteboardsMessageReceived(message);
                }

                else if (action.equals("changeboard")) {
                    changeWhiteboard(message.getArgument(0));
                    listener.onChangeboardMessageReceived(message);
                }

                else if (action.equals("chat")) {
                    listener.onChatMessageReceived(message);
                }

                // TODO: Check for paint message using Protocol
                else {
                    whiteboard.update(command);
                    listener.onPaintMessageReceived(message);
                }
            } catch (UnsupportedOperationException e) {
                listener.onInvalidMessageReceived(e.getMessage());
            }
        }
    }

    /**
     * Sends a message to the server. The message content is passed as an
     * argument.
     * 
     * @param message
     */
    public void scheduleMessage(String message) {

        outputQueue.add(message);
    }

    /**
     * Checks if there is a message to be sent. If there is a message, then it
     * sends it. If there isn't, it blocks the stream
     * 
     * @throws IOException
     */
    public void send() throws IOException {
        PrintWriter outputStream;
        try {
            outputStream = new PrintWriter(socket.getOutputStream(), true);
            while (socket.isConnected()) {
                String message = ((LinkedBlockingQueue<String>) outputQueue)
                        .take();
                // If there is a poison pill, stops listening
                if(message == "Poison Pill") {
                    return;
                }
                outputStream.println(message);
            }

            // Checks for checked exceptions that could be raised.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets this class's whiteboard to a new instance of Whiteboard
     * 
     * @param name
     *            is a non-null name of a board
     */
    public void changeWhiteboard(String name) {
        this.whiteboard = new Whiteboard(name);
    }

    /**
     * Returns this class's user
     * 
     * @return user
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets this class's user's name
     * 
     * @param username
     *            is a non-null String for the username
     */
    public void setUserName(String username) {
        getUser().setName(username);
    }

    /**
     * Initializes the client by sending an initialization request to the
     * server.
     * 
     * @param listener
     *            A ClientListener, which has callbacks to each type of message
     *            received.
     * @param username the desired username of the client.
     */
    public void initialize(ClientListener listener, String username) {

        // Defines the listener
        this.listener = listener;
        this.user.setName(username);
        // Sets the initial username
        String[] args = new String[] { getUser().getUid().toString(),
                getUser().getName() };
        scheduleMessage(CWPMessage.Encode(getUser(), "initialize", args));

        // Creates the listening thread, that receives messages from updates of
        // the model
        Thread incomingMessageHandler = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    listen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Creates the listening thread, that receives messages from updates of
        // the model
        Thread outgoingMessageHandler = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        incomingMessageHandler.start();
        outgoingMessageHandler.start();
    }

    /**
     * Closes the connection with the server.
     * 
     * @throws IOException
     */
    public void close() throws IOException {
        socket.close();
    }
}
