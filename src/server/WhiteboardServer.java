package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import client.WhiteboardModel;

public class WhiteboardServer {
    // It should have a model of the whiteboard attached to it.

    private final ServerSocket serverSocket;
    private final WhiteboardModel model;
    private final List<ClientHandler> clients; 

    public WhiteboardServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clients = Collections.synchronizedList(new ArrayList<ClientHandler>());
        
        model = new WhiteboardModel();
    }

    /**
     * Run the server, listening for client connections and handling them. Never
     * returns unless an exception is thrown.
     * 
     * @throws IOException
     *             if the main server socket is broken (IOExceptions from
     *             individual clients do *not* terminate serve())
     */
    public void serve() throws IOException {
        while (true) {
            // block until a client connects
            final Socket socket = serverSocket.accept();

            // Start thread
            ClientHandler client = new ClientHandler(model, socket);
            clients.add(client);
        }
    }

    public static void main(String[] args) {
        int port = 4444; // default port

        WhiteboardServer server;
        try {
            server = new WhiteboardServer(port);
            server.serve();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
