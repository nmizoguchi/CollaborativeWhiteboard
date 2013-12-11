package tests.unit;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import shared.models.Whiteboard;

/**
 * Tests the Whiteboard class.
 * 	-Tests the constructor using board names that have whitespaces and special characters
 * 	-Tests the update, getVersion and getAction methods
 * @author rcha
 *
 */
public class WhiteboardTest {

    @Test
    public void getNameTest_valid() {

        /*
         * Tests if constructor accepts what is considered to be a valid name,
         * and also the getName() method.
         */
        Whiteboard board = new Whiteboard("Default");
        assertEquals("Default", board.getName());

        // Name with spaces
        board = new Whiteboard("Home Board");
        assertEquals("Home Board", board.getName());
        
        //Name with special characters and numbers
        board = new Whiteboard("abk52*?");
        assertEquals("abk52*?", board.getName());
    }

    @Test
    public void whiteboardTest_updateGetVersionGetAction() {

        /*
         * Tests if update adds the new action and update the version correctly.
         * (For each action added, the version should go up one number). For
         * this, also tests the getVersion and getAction methods.
         */

        Whiteboard board = new Whiteboard("Default");

        // Update and version
        board.update("erase 0 1 2 3 4");
        assertEquals(1, board.getVersion());

        // Update and version
        board.update("erase 1 2 3 4 5");
        assertEquals(2, board.getVersion());

        // getAction
        assertEquals("erase 0 1 2 3 4", board.getAction(0));
        assertEquals("erase 1 2 3 4 5", board.getAction(1));
    }
}
