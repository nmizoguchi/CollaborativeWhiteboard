package Protocol;

import java.util.Arrays;
import java.util.UUID;

import shared.models.User;

public class Protocol {

    private final String senderUID;
    private final String action;
    private final String[] arguments;
    
    private Protocol(String message, boolean hasUID) {

        String[] tokens = message.split(" ");
        if(hasUID) {
            senderUID = tokens[0];
            action = tokens[1];
            arguments = Arrays.copyOfRange(tokens, 2, tokens.length);
        } else {
            senderUID = "";
            action = tokens[0];
            arguments = Arrays.copyOfRange(tokens, 1, tokens.length);
        }
    }
    
    public static Protocol ForServer(String message) {
        return new Protocol(message, true);
    }
    
    public static Protocol ForClient(String message) {
        return new Protocol(message, false);
    }
    
    public static String CreateMessage(User user, String action, String arguments) {
        return user.getUid() + " "+action+" "+arguments;
    }
    
    public static String CreateServerMessage(String action, String arguments) {
        return action+" "+arguments;
    }
    
    /**
     * Checks if the input is a valid input of the protocol.
     * 
     * @param input
     *            The command desired.
     * @return message to client
     */
    private String[] CheckAndFormat(String input) {

        // Commands sent and received both sides:
//         drawline x1 y1 x2 y2 color brushSize fillColor hasFill
//		   freehand x1 y1 x2 y2 color brushSize
//         erase x1 y1 x2 y2 brushSize
//         drawrect x1 y1 x2 y2 brushColor brushSize fillColor hasFill
//         changeboard boardName
//         cleanboard boardName
//         newuser username
//         updateuser oldUsername newUsername
//        
//         Commands sent from the client (to the server):
//        
//         initialize username
//         
//         Commands sent from the server (to the client):
//         whiteboards name1 name2 name3 ...
//         disconnecteduser username
        String regex = ""
                + "(drawline -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+)|"
                + "(erase -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+)|"
                + "(drawrect -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+)|"
                + "(changeboard [^\\s]+)|"
                + "(cleanboard [^\\s]+)|"
                + "(whiteboards( [^\\s]+)*)|"
                + "(initialize [^\\s]+)|"
                + "(newuser [^\\s]+)|"
                + "(updateuser [^\\s]+ [^\\s]+)|"
                + "(disconnecteduser [^\\s]+)|"
                + "(help)|(bye)";

        if (!input.matches(regex)) {
            // invalid input
            System.out.println("Unsupported operation: "+input);
            throw new UnsupportedOperationException();
        }

        String[] tokens = input.split(" ");

        return tokens;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public String getAction() {
        return action;
    }

    public String getArgument(int index) {
        return arguments[index];
    }
    
    public String getArguments() {
        String args = "";
        for(String arg : arguments) {
            args = args + " " + arg;
        }
        
        return args.trim();
    }
    
    public String getPaintAction() {
        String args = "";
        for(String arg : arguments) {
            args = args + " " + arg;
        }
        return action + args;
    }
    
    public int getArgumentsSize() {
        return arguments.length;
    }
}
