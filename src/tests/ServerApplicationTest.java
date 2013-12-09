package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import server.Connection;
import server.ServerApplication;
import server.ServerConnectionHandler;
import shared.models.User;
import shared.models.Whiteboard;
import Protocol.CWPMessage;

public class ServerApplicationTest {

    /*
     * The only tests we can perform as unit testing are tests that don't depend
     * on message passing through sockets. This will be tested in a different
     * way, by creating a mock application to talk with the server, and test its
     * behavior.
     */

    ServerApplication server;
    Thread newConnectionHandler;
    User user;
    Connection connection;

    @Before
    public void initialize() throws UnknownHostException, IOException {

        server = new ServerApplication("Server Name");
        user = new User("e760229d-885b-4dc2-8a1c-a707643eb910", "User");

        int port = 4444; // default port

        // Creates a new thread to listen to new connections.
        try {
            newConnectionHandler = new Thread(new ServerConnectionHandler(
                    server, port));
            newConnectionHandler.start();
        } catch (IOException e) {

        }
    }

    @Test
    public void getWhiteboardTest_valid() {

        // Verifies if the board got has the same name.
        assertEquals("Default", server.getWhiteboard("Default").getName());

        Whiteboard whiteboard = server.getWhiteboard("Board name");

        // Creates a new board named "Board name", once it doesn't find one with
        // this name.
        assertNotEquals(null, whiteboard);

        // Should give the same instance of whiteboard
        assertEquals(whiteboard, server.getWhiteboard("Board name"));
    }
}