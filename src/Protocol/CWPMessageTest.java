package Protocol;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import shared.models.User;

public class CWPMessageTest {

    @Test
    public void CWPMessageTest_Constructor() {
        
        String inputHead = "uid" + CWPMessage.SEPARATOR + "erase"
                + CWPMessage.SEPARATOR;
        
        String inputArgs = "10" + CWPMessage.SEPARATOR + "20"
                + CWPMessage.SEPARATOR + "30" + CWPMessage.SEPARATOR + "40"
                + CWPMessage.SEPARATOR +"50";
        
        CWPMessage message = new CWPMessage(inputHead+inputArgs);
        assertEquals("erase",message.getAction());
        assertEquals(5,message.getArgumentsSize());
        
        Assert.assertArrayEquals(inputArgs.split(CWPMessage.SEPARATOR), message.getArguments());
    }
    
    @Test
    public void encodeTest_valid() {
        
        User user = new User("Nicholas");
        String action = "newline";
        String[] params = new String[] { "10", "20", "30", "40", "50", "0", "5" };

        String message = CWPMessage.Encode(user, action, params);
        
        String[] tokens = message.split(CWPMessage.SEPARATOR);
        
        assertEquals(9,tokens.length);
        assertEquals(user.getUid().toString(), tokens[0]);
        assertEquals(action, tokens[1]);
        
        for(int i = 0; i < params.length; i++) {
            assertEquals(params[i], tokens[2+i]);
        }
    }
    
    @Test
    public void encodeTest_compositeName() {
        
        User user = new User("Rebekah Cha");
        String action = "newuser";
        String[] params = new String[] { user.getName() };

        String message = CWPMessage.Encode(user, action, params);
        String[] tokens = message.split(CWPMessage.SEPARATOR);
        
        assertEquals(3, tokens.length);
        assertEquals(user.getUid().toString(), tokens[0]);
        assertEquals(action, tokens[1]);
        assertEquals(user.getName(), tokens[2]);
    }
}
