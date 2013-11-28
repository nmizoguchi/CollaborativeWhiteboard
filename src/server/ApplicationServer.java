package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import client.WhiteboardModel;

public class ApplicationServer {
    // It should have a model of the whiteboard attached to it.

    private final ServerSocket serverSocket;
    private final List<WhiteboardModel> whiteboards;
    private final List<ClientConnection> clients; 

    public ApplicationServer(int port) throws IOException {
        
        serverSocket = new ServerSocket(port);
        clients = Collections.synchronizedList(new ArrayList<ClientConnection>());
        whiteboards = Collections.synchronizedList(new ArrayList<WhiteboardModel>());
        whiteboards.add(new WhiteboardModel("Default"));
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
            ClientConnection client = new ClientConnection(this,whiteboards.get(0));
            ClientHandler clientHandler = new ClientHandler(client,socket);
            clients.add(client);
            clientHandler.startThreads();
        }
    }
    
    public WhiteboardModel getWhiteboard(String name) throws NoSuchFieldException {
        // Make a copy of the synchronized list, so we don't have problems
        List<WhiteboardModel> copy = new ArrayList<WhiteboardModel>(whiteboards);
        
        for(WhiteboardModel board : copy) {
            if(name.equals(board.getName())) {
                return board;
            }
        }
        
        // Not found!
        throw new NoSuchFieldException();
    }

    public static void main(String[] args) {
        int port = 4444; // default port

        ApplicationServer server;
        try {
            server = new ApplicationServer(port);
            server.serve();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
