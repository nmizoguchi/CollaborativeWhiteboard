package Protocol;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProtocolTest {

//    CreateMessage(User, String, String)
//    CheckAndFormat(String)
//    getSenderUID()
//    getAction()
//    getArguments(int)
//    getPaintAction()
//    getArgumentsSize()
    
    @Test
    public void testGetPaintAction_valid() {
        Protocol p = Protocol.ForClient("erase 10 20 30 40 50");
        assertEquals("erase 10 20 30 40 50", p.getPaintAction());
    }

}
