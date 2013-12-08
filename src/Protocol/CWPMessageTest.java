package Protocol;

import static org.junit.Assert.assertEquals;

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
        String inputHead = "fcebbed5-ae50-46b1-ac2a-e86261aa7ec6" + CWPMessage.SEPARATOR + "erase"
                + CWPMessage.SEPARATOR;

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
        String[] params = new String[] { user.getUid().toString(), user.getName() };

        String message = CWPMessage.Encode(user, action, params);
        String[] tokens = message.split(CWPMessage.SEPARATOR);

        assertEquals(4, tokens.length);
        assertEquals(user.getUid().toString(), tokens[0]);
        assertEquals(action, tokens[1]);
        assertEquals(user.getUid().toString(), tokens[2]);
        assertEquals(user.getName(), tokens[3]);
    }
    
    @Test
    public void validateTest_eraseValid(){
        User user = new User("Nick");
        String action = "erase";
        String[] params = new String[] { "10", "20", "30", "40", "50" };
        String message = CWPMessage.Encode(user, action, params);
    }
    
    @Test
    public void validateTest_whiteboardsValid() {
        User user = new User("Username");
        String action = "whiteboards";
        String[] params = new String[] { "Default" };
        String message = CWPMessage.Encode(user, action, params);
    }
}
