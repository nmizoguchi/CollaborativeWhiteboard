package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import server.Connection;
import server.ConnectionOutputHandler;
import shared.models.User;
import shared.models.Whiteboard;
import Protocol.CWPMessage;

/**
 * This JUnit Test class tests the Connection class.
 * The tests cover the different methods of Connection:
 * -Tests the getUser and setUser methods
 * -Tests the isInitialized method before and after the setUser method is used
 * -Tests the getActiveWhiteboard and setActiveWhiteboard methods
 * 		and the ConnectionOutputHandler that is affected by them
 * -Tests the updateActiveWhiteboard method
 * @author rcha
 *
 */
public class ConnectionTest {

    @Test
    public void getSetUserTest_valid() {
    	/*
    	 * Tests the get and setUser method in Connection
    	 */
        Whiteboard whiteboard = new Whiteboard("Board");
        User user = new User("Hercules");
        Connection connection = new Connection(whiteboard, user);

        connection.setUser(user);
        assertEquals(user, connection.getUser());
    }

    @Test
    public void isInitializedTest_valid() {
    	/*
    	 * Verifies the isInitialized method of Connection
    	 * The Connection should initialize when the setUser is used
    	 */
        Whiteboard whiteboard = new Whiteboard("Board");
        User user = new User("Hercules");
        Connection connection = new Connection(whiteboard, user);

        assertFalse(connection.isInitialized());

        // Setting the user should initialize the connection
        connection.setUser(user);
        assertTrue(connection.isInitialized());
    }

    @Test
    public void getActiveWhiteboardAndAddConnectionOutputHandlerTest_valid() {

    	/*
    	 * Tests the get and setActiveWhiteboard methods 
    	 * and the ConnectionOutputHandler that is affected by these methods
    	 */
        Whiteboard whiteboard = new Whiteboard("Board");
        final User user = new User("Hercules");
        Connection connection = new Connection(whiteboard, user);

        assertEquals(whiteboard, connection.getActiveWhiteboard());

        whiteboard = new Whiteboard("Another Board");

        // Add a custom handler to test if the handler receives what is expect.
        connection.addConnectionOutputHandler(new ConnectionOutputHandler() {

            @Override
            public void scheduleMessage(String message) {
                String expected = CWPMessage.Encode(user, "changeboard",
                        new String[] { "Another Board" });
                assertTrue(message.equals(expected));
            }
        });

        // Sets the new whiteboard, and this results in the handler callback
        // call.
        connection.setActiveWhiteboard(whiteboard, 0);
        
        // Verifies if the board was changed
        assertEquals(whiteboard, connection.getActiveWhiteboard());
    }

    @Test
    public void updateActiveWhiteboardTest_valid() {
        /*
         * Tests the updateActiveWhiteboard method
         */
        Whiteboard whiteboard = new Whiteboard("Board");
        User user = new User("Hercules");
        Connection connection = new Connection(whiteboard, user);

        connection.updateActiveWhiteboard("erase");
        assertTrue(whiteboard.getAction(0).equals("erase"));
    }
}
