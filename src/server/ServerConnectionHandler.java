package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Handles incoming client connections. It is responsible for creating a
 * Connection and ConnectionController to handle this new connection. Immutable
 * class.
 * 
 * @author Nicholas M. Mizoguchi
 * 
 */
public class ServerConnectionHandler implements Runnable {

    private final ServerApplication server;
    private final ServerSocket serverSocket;

    /**
     * Constructor
     * 
     * @param server
     *            the server that this handler is working for.
     * @param port
     *            the port in which it will listen.
     * @throws IOException
     *             if problems happens when listening to a new connection.
     */
    public ServerConnectionHandler(ServerApplication server, int port)
            throws IOException {

        this.server = server;
        serverSocket = new ServerSocket(port);
    }

    /**
     * Run the server, listening for client connections. When a new connection
     * is established, it creates a new Connection object and also its listener,
     * passing the handling part of the new connection to them, so it can listen
     * to new connections.
     */
    @Override
    public void run() {
        try {
            while (true) {
                // block until a client connects
                Socket socket = serverSocket.accept();

                // Creates the Connection and its controller.
                Connection connection = new Connection(
                        server.getWhiteboard("Default"),server.getServerUser());
                ConnectionController controller = new ConnectionController(
                        server, socket);
                connection.addConnectionOutputHandler(controller);

                // Add the connection to the collection of connections on the
                // Server
                server.addConnection(controller, connection);

                // Start handling this connection in other threads.
                controller.startThreads();
            }
        } catch (IOException e) {
            // Problem with serving. Stops server. Also, prints the stack for
            // debugging.
            e.printStackTrace();
        }
    }
}
