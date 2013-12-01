package server.controllers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import server.Connection;
import server.ServerApplication;

public class NewConnectionHandler implements Runnable {

    private final ServerApplication server;
    private final ServerSocket serverSocket;

    public NewConnectionHandler(ServerApplication server, int port) throws IOException {

        this.server = server;
        serverSocket = new ServerSocket(port);
    }

    /**
     * Run the server, listening for client connections and handling them. Never
     * returns unless an exception is thrown.
     * 
     * @throws IOException
     *             if the main server socket is broken (IOExceptions from
     *             individual clients do *not* terminate serve())
     */
    @Override
    public void run() {
        try {
            while (true) {
                // block until a client connects
                final Socket socket = serverSocket.accept();

                ConnectionController controller = new ConnectionController(
                        server, socket);
                Connection connection = new Connection(
                        server.getWhiteboard("Default"), controller);

                // Add the connection to the collection of connections on the
                // Server
                server.addConnection(controller, connection);

                // Start thread
                controller.startThreads();
            }
        } catch (IOException e) {

        }
    }
}
