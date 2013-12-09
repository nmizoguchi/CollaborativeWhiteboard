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

public class ConnectionTest {

    @Test
    public void getSetUserTest_valid() {

        Whiteboard whiteboard = new Whiteboard("Board");
        User user = new User("Hercules");
        Connection connection = new Connection(whiteboard, user);

        connection.setUser(user);
        assertEquals(user, connection.getUser());
    }

    @Test
    public void isInitializedTest_valid() {

        Whiteboard whiteboard = new Whiteboard("Board");
        User user = new User("Hercules");
        Connection connection = new Connection(whiteboard, user);

        assertFalse(connection.isInitialized());

        // Setting the user initialize the connection
        connection.setUser(user);
        assertTrue(connection.isInitialized());
    }

    @Test
    public void getActiveWhiteboardAndAddConnectionOutputHandlerTest_valid() {

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
                System.out.println(expected);
                System.out.println(message);
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
        
        Whiteboard whiteboard = new Whiteboard("Board");
        User user = new User("Hercules");
        Connection connection = new Connection(whiteboard, user);

        connection.updateActiveWhiteboard("erase");
        assertTrue(whiteboard.getAction(0).equals("erase"));
    }
}
