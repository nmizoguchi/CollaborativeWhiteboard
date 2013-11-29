package client;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class WhiteboardTest {

    @Test
    public void whiteboardTest_getName() {

        /*
         * Tests if getName is working correctly, and if the constructor is
         * setting the name.
         */
        Whiteboard board = new Whiteboard("Default");
        assertEquals("Default", board.getName());
        assertEquals(0, board.getVersion());

        board = new Whiteboard("Home");
        assertEquals("Home", board.getName());
        assertEquals(0, board.getVersion());
    }

    @Test
    public void whiteboardTest_updateAndVersion() {
        
        /* Tests update, getActionsToUpdate and Versions by
         * executing supported actions
         */

        Whiteboard board = new Whiteboard("Default");
        board.update("erase 0 1 2 3 4");
        assertEquals(1, board.getVersion());
        assertEquals("erase 0 1 2 3 4", board.getAction(0));

        board.update("erase 1 2 3 4 5");
        assertEquals(2, board.getVersion());
        assertEquals("erase 1 2 3 4 5", board.getAction(1));

        List<String> updates = board.getActionsToUpdate(0);
        assertEquals(2, updates.size());
        assertEquals("erase 0 1 2 3 4", updates.get(0));
        assertEquals("erase 1 2 3 4 5", updates.get(1));

        updates = board.getActionsToUpdate(1);
        assertEquals(1, updates.size());
        assertEquals("erase 1 2 3 4 5", updates.get(0));
    }
}
