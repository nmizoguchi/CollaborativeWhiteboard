package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class WhiteboardClient {

    private static WhiteboardGUI GUI;
    private final WhiteboardModel whiteboard;
    private final Socket socket;
    public final int FPS = 25;
    
    public WhiteboardClient(String serverAddress, int port)
            throws UnknownHostException, IOException {

        whiteboard = new WhiteboardModel();
        socket = new Socket(serverAddress, port);
        GUI = new WhiteboardGUI(this);
        GUI.setVisible(true);
    }

    public void listen() throws IOException {

        String command;
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));

        while ((command = inputStream.readLine()) != null) {
            GUI.updateModelView(command);
            whiteboard.update(command);
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

    public WhiteboardModel getWhiteboardModel() {
        return this.whiteboard;
    }

    /*
     * Main program. Make a window containing a Canvas.
     */
    public static void main(String[] args) throws UnknownHostException,
            IOException {

        String server = JOptionPane.showInputDialog("Server IP:");
        
        final WhiteboardClient client = new WhiteboardClient(server, 4444);

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
