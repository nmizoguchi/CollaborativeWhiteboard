package client.gui;


/**
 * Implements Runnable. When called, the user's chat area pane is 
 * updated with a new line of message that is received from the server
 * 
 * @author rcha
 *
 */
public class RunnableChat implements Runnable {

    private final WhiteboardGUI gui;
    private final String message;
    
    /**
     * Takes in a WhiteboardGUI and a message
     * @param gui is a WhiteboardGUI associated with the user
     * @param message is a String of a message tha is added to chat area pane
     * 				indicating whether a new user entered the server,
     * 				a user disconnected from the server,
     * 				or a message that a user has entered into the chatbox
     */
    public RunnableChat(WhiteboardGUI gui, String message) {
        this.gui = gui;
        this.message = message;
    }
    
    /**
     * Adds the message to the chat area pane
     */
    @Override
    public void run() {
    	gui.addChatMessage(message);
    }

}
