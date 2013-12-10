package Protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import shared.models.User;

/**
 * Represents messages of the custom protocol defined for this application. CWP
 * stands for Collaborative Whiteboard Protocol. It is an immutable class.
 * 
 * @author Nicholas M. Mizoguchi
 * 
 */
public class CWPMessage {

    public final static String SEPARATOR = String.valueOf('\0');

    private final String senderUID;
    private final String action;
    private final String[] arguments;

    /**
     * Constructor method.
     * 
     * @param message
     *            A string that represents the message to be parsed.
     * @throws UnsupportedOperationException
     *             if the message is invalid according to the CWProtocol.
     */
    public CWPMessage(String message) {

        // Check if it is a valid message
        validate(message);

        String[] tokens = message.split(SEPARATOR);

        this.senderUID = tokens[0];
        this.action = tokens[1];

        if (tokens.length >= 2) {
            arguments = Arrays.copyOfRange(tokens, 2, tokens.length);
        } else {
            arguments = new String[] {};
        }
    }

    /**
     * Static method. Defines a message as a String.
     * 
     * @param user
     *            The User object that represents the sender.
     * @param action
     *            The name of the action to be encoded.
     * @param params
     *            The parameters of the action.
     * @return a String that represents the message to be sent through the
     *         socket.
     */
    public static String Encode(User user, String action, String[] params) {
        String message = user.getUid() + SEPARATOR + action;

        for (int i = 0; i < params.length; i++) {
            message = message + SEPARATOR + params[i];
        }

        // Check if it is a valid message
        validate(message);

        return message;
    }

    /**
     * Static method. Defines a message as a String.
     * 
     * @param user
     *            The User object that represents the sender.
     * @param paintAction
     *            The paint action, used by Whiteboard objects.
     * @return a String that represents the message to be sent through the
     *         socket.
     */
    public static String EncodePaintAction(User user, String paintAction) {
        String message = user.getUid() + SEPARATOR + paintAction;

        // Check if it is a valid message
        validate(message);

        return message;
    }

    /**
     * Validates a message. It checks if the message has a valid syntax. defined
     * by the Protocol.
     * 
     * @param input
     *            A string that represents the message to be sent through the
     *            socket.
     * @throws UnsupportedOperationException
     *             If the message is not defined in the protocol.
     */
    private static void validate(String input) {

        // Valid messages:
        // uuid initialize uuid username
        // uuid disconnecteduser uuid username
        // uuid newuser uuid username
        // uuid drawline arg0 ... arg5
        // uuid erase arg0 ... arg5
        // uuid drawrect arg0 ... arg5
        // uuid changeboard boardname
        // uuid cleanboard boardname -> supported but not implemented yet
        // uuid whiteboards arg n* (may our not have arguments)
        // uuid updateuser uuid username -> supported but not implemented yet
        // uuid chat message

        String div = "[" + SEPARATOR + "]";
        String any = "[^" + SEPARATOR + "]+";
        String uuid = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

        List<String> regexes = new ArrayList<String>();

        // uuid initialize uuid 1 argument
        regexes.add("(" + uuid + div + "initialize" + div + uuid + div + any
                + ")");

        // uuid disconnecteduser uuid 1 argument
        regexes.add("(" + uuid + div + "disconnecteduser" + div + uuid + div
                + any + ")");

        // uuid newuser uuid argument
        regexes.add("(" + uuid + div + "newuser" + div + uuid + div + any + ")");

        // uuid updateusers (uuid argument)+
        regexes.add("(" + uuid + div + "updateusers(" + div + uuid + div + any + ")+)");

        // uuid drawline 6 arguments
        regexes.add("(" + uuid + div + "drawline" + div + "-?\\d+" + div
                + "-?\\d+" + div + "-?\\d+" + div + "-?\\d+" + div + "-?\\d+"
                + div + "-?\\d+)");

        // uuid erase 6 arguments
        regexes.add("(" + uuid + div + "erase" + div + "-?\\d+" + div
                + "-?\\d+" + div + "-?\\d+" + div + "-?\\d+" + div + "-?\\d+"
                + div + "-?\\d+)");

        // uuid drawrect 8 arguments
        regexes.add("(" + uuid + div + "drawrect" + div + "-?\\d+" + div
                + "-?\\d+" + div + "-?\\d+" + div + "-?\\d+" + div + "-?\\d+"
                + div + "-?\\d+" + div + "-?\\d+" + div + "-?\\d+)");

        // uuid changeboard 1 argument
        regexes.add("(" + uuid + div + "changeboard" + div + any + ")");

        // uuid cleanboard 1 argument
        regexes.add("(" + uuid + div + "cleanboard" + div + any + ")");

        // uuid whiteboards optional arguments
        regexes.add("(" + uuid + div + "whiteboards(" + div + any + ")*)");

        // uuid updateuser uuid + argument
        regexes.add("(" + uuid + div + "updateuser" + div + uuid + div + any
                + ")");

        // uuid chat 1 argument
        regexes.add("(" + uuid + div + "chat" + div + any + ")");

        // Creates an or of all regexes
        String regex = "";
        for (String x : regexes) {
            regex = regex + x + "|";
        }

        if (!input.matches(regex)) {
            // invalid input
            System.out.println("Unsupported operation: " + input);
            throw new UnsupportedOperationException();
        }
    }

    /**
     * @return the UID from the Sender.
     */
    public String getSenderUID() {
        return senderUID;
    }

    /**
     * @return the action represented by this message.
     */
    public String getAction() {
        return action;
    }

    /**
     * Return an argument of the message.
     * 
     * @param index
     *            The index of the argument. The index must be a non-zero
     *            integer less then the number of arguments. index 0 represents
     *            the first argument, and index n represents the n-1 argument.
     * @return the argument in the position represented by the index.
     */
    public String getArgument(int index) {
        return arguments[index];
    }

    /**
     * @return an array of all arguments in the current message.
     */
    public String[] getArguments() {
        // returns a copy of the arguments for immutability
        return Arrays.copyOf(arguments, arguments.length);
    }

    /**
     * Requires that the message represents a paint action.
     * 
     * @return the portion of the message that represents the paint action. This
     *         means removing the header of the message (such as sender UID).
     */
    public String getPaintAction() {
        String args = "";
        for (String arg : arguments) {
            args = args + SEPARATOR + arg;
        }
        return action + args;
    }

    /**
     * @return the number of arguments present in the current message.
     */
    public int getArgumentsSize() {
        return arguments.length;
    }
}
