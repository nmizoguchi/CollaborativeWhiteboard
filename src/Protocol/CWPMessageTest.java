package Protocol;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import shared.models.User;

public class CWPMessageTest {

    @Test
    public void CWPMessageTest_separator() {

        /*
         * Creates a CWPMessage object and tests if the CWPMessage.SEPARATOR is
         * separating the message correctly. This is made by creating a message,
         * and then reading the info that should be parsed correctly.
         */

        // Header is the UID and action.
        String inputHead = "fcebbed5-ae50-46b1-ac2a-e86261aa7ec6"
                + CWPMessage.SEPARATOR + "erase" + CWPMessage.SEPARATOR;

        // Arguments
        String inputArgs = "10" + CWPMessage.SEPARATOR + "20"
                + CWPMessage.SEPARATOR + "30" + CWPMessage.SEPARATOR + "40"
                + CWPMessage.SEPARATOR + "50";

        CWPMessage message = new CWPMessage(inputHead + inputArgs);

        // Checks if was parsed correctly
        assertEquals("erase", message.getAction());
        assertEquals(5, message.getArgumentsSize());

        // Checks if the parsed arguments are all equal.
        Assert.assertArrayEquals(inputArgs.split(CWPMessage.SEPARATOR),
                message.getArguments());
    }

    @Test
    public void encodeTest_valid() {

        /*
         * Creates a valid message, and checks if the constructor parses the
         * message and creates a consistent CWPMessage object.
         */

        User user = new User("Nicholas");
        String action = "drawline";
        String[] params = new String[] { "10", "20", "30", "40", "50", "0" };

        // Encodes a message using the objects we just created
        String message = CWPMessage.Encode(user, action, params);

        // Parses this message manually, and compares it to the CWPMessage.
        String[] tokens = message.split(CWPMessage.SEPARATOR);

        assertEquals(8, tokens.length);
        assertEquals(user.getUid().toString(), tokens[0]);
        assertEquals(action, tokens[1]);

        for (int i = 0; i < params.length; i++) {
            assertEquals(params[i], tokens[2 + i]);
        }
    }

    @Test
    public void encodeTest_compositeName() {
        /*
         * Encodes a message with composite name. This test verifies if the
         * parser doesn't have a problem with white spaces.
         */

        User user = new User("Rebekah Cha");
        String action = "newuser";
        String[] params = new String[] { user.getUid().toString(),
                user.getName() };

        String message = CWPMessage.Encode(user, action, params);
        String[] tokens = message.split(CWPMessage.SEPARATOR);

        assertEquals(4, tokens.length);
        assertEquals(user.getUid().toString(), tokens[0]);
        assertEquals(action, tokens[1]);
        assertEquals(user.getUid().toString(), tokens[2]);
        assertEquals(user.getName(), tokens[3]);
    }

    /*
     * Validation tests: Tests if the regex is finding valid and invalid
     * messages
     */
    @Test
    public void validateTest_eraseValid() {
        User user = new User("Nick");
        String action = "erase";
        String[] params = new String[] { "10", "20", "30", "40", "50" };
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void validateTest_eraseInvalidArgument() {
        User user = new User("Nick");
        String action = "erase";
        String[] params = new String[] { "10", "20", "30", "40", "thick" };
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void validateTest_eraseInvalidNumberOfArguments() {
        User user = new User("Nick");
        String action = "erase";
        String[] params = new String[] { "10", "20", "30", "40" };
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test
    public void validateTest_drawlineValid() {
        User user = new User("Nick");
        String action = "drawline";
        String[] params = new String[] { "10", "20", "30", "40", "50", "60" };
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void validateTest_drawlineInvalidArgument() {
        User user = new User("Nick");
        String action = "drawline";
        String[] params = new String[] { "10", "20", "30", "40", "thick" };
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void validateTest_drawlineInvalidNumberOfArguments() {
        User user = new User("Nick");
        String action = "drawline";
        String[] params = new String[] { "10", "20", "30", "40" };
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test
    public void validateTest_drawrectValid() {
        User user = new User("Nick");
        String action = "drawrect";
        String[] params = new String[] { "10", "20", "30", "40", "50", "60",
                "70", "80" };
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void validateTest_drawrectInvalidArgument() {
        User user = new User("Nick");
        String action = "drawrect";
        String[] params = new String[] { "10", "20", "30", "40", "50", "60",
                "70", "white" };
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void validateTest_drawrectInvalidNumberOfArguments() {
        User user = new User("Nick");
        String action = "drawrect";
        String[] params = new String[] { "10", "20", "30", "40", "50", "60",
                "70" };
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test
    public void validateTest_whiteboardsValid() {

        User user = new User("Username");
        String action = "whiteboards";

        // One board
        String[] params = new String[] { "Default" };
        String message = CWPMessage.Encode(user, action, params);

        // More boards
        params = new String[] { "Default", "One two", " what ?" };
        message = CWPMessage.Encode(user, action, params);

        // Request for whiteboard names (from client)
        params = new String[] {};
        message = CWPMessage.Encode(user, action, params);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void validateTest_whiteboardsInvalid() {

        User user = new User("Username");
        String action = "whiteboards";
        String[] params = new String[] { "" };
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test
    public void validateTest_initializeValid() {

        // Message format: uuid initialize uuid username
        User user = new User("Username");
        String action = "initialize";

        String[] params = new String[] { user.getUid().toString(), "Someone" };
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void validateTest_initializeInvalidNumberOfArguments() {

        // Test with invalid number of arguments
        User user = new User("Username");
        String action = "initialize";
        String[] params = new String[] {};
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test
    public void validateTest_disconnecteduserValid() {

        // Message format: uuid disconnecteduser uuid username
        User user = new User("Username");
        String action = "disconnecteduser";

        String[] params = new String[] { user.getUid().toString(), "Someone" };
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void validateTest_disconnecteduserInvalidNumberOfArguments() {

        // Test with invalid number of arguments
        User user = new User("Username");
        String action = "disconnecteduser";
        String[] params = new String[] { "Someone" };
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test
    public void validateTest_newuserValid() {
        // Message format: uuid newuser uuid username
        User user = new User("Username");
        String action = "newuser";

        String[] params = new String[] { user.getUid().toString(), "Someone" };
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void validateTest_newuserInvalidNumberOfArguments() {

        // Test with invalid number of arguments
        User user = new User("Username");
        String action = "newuser";
        String[] params = new String[] { "Someone" };
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test
    public void validateTest_changeboardValid() {
        // Message format: uuid newuser uuid username
        User user = new User("Username");
        String action = "changeboard";

        String[] params = new String[] { "Some board" };
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void validateTest_changeboardInvalidNumberOfArguments() {

        // Test with invalid number of arguments
        User user = new User("Username");
        String action = "newuser";
        String[] params = new String[] {};
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test
    public void validateTest_chatValid() {
        // Message format: uuid chat uuid username
        User user = new User("Username");
        String action = "chat";

        String[] params = new String[] { "This is a message to someone! Or not?" };
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void validateTest_chatInvalidNumberOfArguments() {

        // Test with invalid number of arguments
        User user = new User("Username");
        String action = "chat";
        String[] params = new String[] {};
        String message = CWPMessage.Encode(user, action, params);
    }

    @Test
    public void getPaintAction_valid() {

        User user = new User("Nick");
        String action = "drawline";
        String[] params = new String[] { "10", "20", "30", "40", "50", "60" };
        String message = CWPMessage.Encode(user, action, params);

        // Encode the message and create a CWPMessage with it.
        CWPMessage cwp = new CWPMessage(message);
        assertEquals("drawline" + CWPMessage.SEPARATOR + "10"
                + CWPMessage.SEPARATOR + "20" + CWPMessage.SEPARATOR + "30"
                + CWPMessage.SEPARATOR + "40" + CWPMessage.SEPARATOR + "50"
                + CWPMessage.SEPARATOR + "60", cwp.getPaintAction());
    }

    @Test
    public void immutabilityTest() {
        // The only place that might break immutability considering what we get,
        // is the mutable array of Strings got from getArguments. Constructor
        // receives a String, so no need to test the constructor.

        User user = new User("Nick");
        String action = "drawline";
        String[] params = new String[] { "10", "20", "30", "40", "50", "60" };
        String message = CWPMessage.Encode(user, action, params);

        CWPMessage cwp = new CWPMessage(message);

        // Get the arguments, change it, and then see if it changed inside the
        // object too.
        String[] args = cwp.getArguments();
        args[0] = "Hello";
        args = cwp.getArguments();

        assertFalse(args[0].equals("Hello"));
    }
}
