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
                listener.onNewuserMessageReceived(message);
            }

            else if (action.equals("disconnecteduser")) {
                listener.onDisconnecteduserMessageReceived(message);
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
            // TODO: error handling
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

    public void send() throws IOException {
        PrintWriter outputStream;
        // Checks if there is a message to be sent. If it has, send it. Block
        // otherwise.
        try {
            outputStream = new PrintWriter(socket.getOutputStream(), true);
            while (socket.isConnected()) {
                String message = ((LinkedBlockingQueue<String>) outputQueue)
                        .take();
                outputStream.println(message);
            }

            // Checks for checked exceptions that could be raised.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void changeWhiteboard(String name) {
        this.whiteboard = new Whiteboard(name);
    }

    public User getUser() {
        return user;
    }

    /**
     * Initializes the client by sending an initialization request to the
     * server.
     * 
     * @param username
     *            the desired username of this client.
     */
    public void initialize(ClientListener listener, String username) {
        
        // Defines the listener
        this.listener = listener;
        
        // Sets the initial username
        getUser().setName(username);
        scheduleMessage(Protocol.CreateMessage(getUser(), "initialize",
                getUser().toString()));

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
