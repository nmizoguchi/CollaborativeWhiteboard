package tests.unit;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import server.Connection;
import server.ServerApplication;
import server.ServerConnectionHandler;
import shared.models.User;
import shared.models.Whiteboard;

/**
 * Tests the ServerApplication class.
 * 	-Tests initializing a Thread
 * 	-Tests getWhiteboard method and also creating a 
 * 		whiteboard if the board name is not already existing
 * @author rcha
 *
 */
public class ServerApplicationTest {

    /*
     * The only tests we can perform as unit testing are tests that don't depend
     * on message passing through sockets. This will be tested in a different
     * way, by creating a mock application to talk with the server, and test its
     * behavior.
     */

    ServerApplication server;

    @Before
    public void initialize() {

        server = new ServerApplication("Server Name");
        server.start(4444);
    }

    @Test
    public void getWhiteboardTest_valid() {

        // Verifies if the board got has the same name.
        assertEquals("Default", server.getWhiteboard("Default").getName());

        Whiteboard whiteboard = server.getWhiteboard("Board name");

        // Creates a new board named "Board name", once it doesn't find one with
        // this name.
        assertNotSame(null, whiteboard);

        // Should give the same instance of whiteboard
        assertEquals(whiteboard, server.getWhiteboard("Board name"));
    }
}
