package server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClientConnectionTest {

    private ApplicationServer server;

    /*
     * Creates a server so it is possible to test a ClientConnection. The test
     * strategy consists of performing unit tests on all public methods of the
     * class. Methods are tested according to the description of each test.
     */
    @Before
    public void initializeServer() {
        try {
            server = new ApplicationServer(4444);
        } catch (IOException e) {
            fail("Problem creating the ApplicationServer");
            e.printStackTrace();
            try {
                server.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Test
    public void getSetActiveBoardTest_consistency() {

        /*
         * Tests functionalities for getActiveBoard, setActiveBoard and
         * getWhiteboardNames. Tests the idea of creating a new board when there
         * is no board with the given name on the server.
         */

        ClientConnection client = new ClientConnection(server);

        // Test getters for initial state (right after creating a client)
        assertEquals("Default", client.getActiveBoard().getName());
        assertEquals("Default", client.getWhiteboardNames());

        /*
         * Test retrieve ability and creating of whiteboards. "New Board" and
         * "Other New Board" are not in the server yet.
         */
        client.setActiveBoard("NewBoard");
        client.setActiveBoard("OtherNewBoard");
        assertEquals("OtherNewBoard", client.getActiveBoard().getName());

        // Tests setting an existing board.
        client.setActiveBoard("NewBoard");
        assertEquals("NewBoard", client.getActiveBoard().getName());

        /*
         * Test for getWhiteBoardNames(). Since the spec doesn't say nothing
         * about the order of the board names, we check if at least they have
         * all the names it should have.
         */
        String[] namesArray = client.getWhiteboardNames().split(" ");
        List<String> names = Arrays.asList(namesArray);

        System.out.println(names);

        assertTrue(names.contains("Default"));
        assertTrue(names.contains("NewBoard"));
        assertTrue(names.contains("OtherNewBoard"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setActiveBoardTest_invalid() {
        /* 
         * setActiveBoard() cannot receive a spaced name as argument.
         * Raises an exception.
         */
        ClientConnection client = new ClientConnection(server);
        client.setActiveBoard("New Board");
    }

    @Test
    public void getSetUsername_consistency() {

        /*
         * Checks for basic consistency in usernames
         */
        ClientConnection client = new ClientConnection(server);

        // Tests first state, where there is no username yet.
        assertEquals("", client.getUserame());

        // Sets username
        client.setUsername("6.005-Student");
        assertEquals("6.005-Student", client.getUserame());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void setUsername_invalid() {

        /*
         * usernames cannot have spaces
         */
        ClientConnection client = new ClientConnection(server);

        // Tests first state, where there is no username yet.
        assertEquals("", client.getUserame());

        // Sets username
        client.setUsername("6.005 Student");
    }

    @Test
    public void getSetClientBoardVersion_consistency() {

        ClientConnection client = new ClientConnection(server);

        // Initial version should be zero, since the server was just created
        assertEquals(0, client.getClientBoardVersion());

        // After setting to 100, should be the same number.
        client.setClientBoardVersion(100);
        assertEquals(100, client.getClientBoardVersion());
    }

    @After
    public void finalizeServer() {
        try {
            server.close();
        } catch (IOException e) {
            fail("The socket was closed before the end of the server execution.");
        }
    }

}
