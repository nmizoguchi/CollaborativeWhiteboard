package Protocol;

import java.util.Arrays;

import shared.models.User;

/**
 * 
 * @author
 *
 */
public class CWPMessage {

    public final static String SEPARATOR = String.valueOf('\0');

    private final String senderUID;
    private final String action;
    private final String[] arguments;

    public CWPMessage(String message) {
        
        // TODO: Check if it is a valid message
        String[] tokens = message.split(SEPARATOR);

        this.senderUID = tokens[0];
        this.action = tokens[1];
    
        if (tokens.length >= 2) {
            arguments = Arrays.copyOfRange(tokens, 2, tokens.length);
        } else {
            arguments = new String[]{};
        }
    }

    public static String Encode(User user, String action, String[] params) {
        String message = user.getUid() + SEPARATOR + action;

        for (int i = 0; i < params.length; i++) {
            message = message + SEPARATOR + params[i];
        }

        // TODO: Check if it is a valid message
        return message;
    }
    
    public static String EncodePaintAction(User user, String paintAction) {
        String message = user.getUid() + SEPARATOR + paintAction;

        // TODO: Check if it is a valid message
        return message;
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
        // drawline x1 y1 x2 y2 color brushSize fillColor hasFill
        // freehand x1 y1 x2 y2 color brushSize
        // erase x1 y1 x2 y2 brushSize
        // drawrect x1 y1 x2 y2 brushColor brushSize fillColor hasFill
        // changeboard boardName
        // cleanboard boardName
        // newuser username
        // updateuser oldUsername newUsername
        //
        // Commands sent from the client (to the server):
        //
        // initialize username
        //
        // Commands sent from the server (to the client):
        // whiteboards name1 name2 name3 ...
        // disconnecteduser username
        String regex = ""
                + "(drawline -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+)|"
                + "(erase -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+)|"
                + "(drawrect -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+)|"
                + "(changeboard [^\\s]+)|" + "(cleanboard [^\\s]+)|"
                + "(whiteboards( [^\\s]+)*)|" + "(initialize [^\\s]+)|"
                + "(newuser [^\\s]+)|" + "(updateuser [^\\s]+ [^\\s]+)|"
                + "(disconnecteduser [^\\s]+)|" + "(help)|(bye)";

        if (!input.matches(regex)) {
            // invalid input
            System.out.println("Unsupported operation: " + input);
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

    public String[] getArguments() {
        return arguments;
    }

    public String getPaintAction() {
        String args = "";
        for (String arg : arguments) {
            args = args + SEPARATOR + arg;
        }
        return action + args;
    }

    public int getArgumentsSize() {
        return arguments.length;
    }
}
