package client;

import Protocol.CWPMessage;

/**
 * 
 * @author 
 *
 */
public interface ClientListener {
	/**
	 * Receives a message of the announcement of a new user
	 * Calls RunnableNewuser and RunnableChat to update the chatbox and userlist
	 * @param message
	 */
    public void onNewuserMessageReceived(CWPMessage message);
    /**
     * Receives a message of the announcement of a user that disconnected.
     * Calls RunnableDisconnecteduser and RunnableChat to update the chatbox and userlist 
     * @param message
     */
    public void onDisconnecteduserMessageReceived(CWPMessage message);
    /**
     * Receives a message that a new whiteboard was created
     * and updates the list of existing whiteboards
     * @param message
     */
    public void onWhiteboardsMessageReceived(CWPMessage message);
    /**
     * Receives a message that a user is switching whiteboards
     * Calls RunnableChangeboard
     * @param message
     */
    public void onChangeboardMessageReceived(CWPMessage message);
    /**
     * Receives a chat message from a user and calls RunnableChat to update the chatbox
     * @param message
     */
    public void onChatMessageReceived(CWPMessage message);
    /**
     * Receivs a message that the user is painting on the canvas
     * and calls CanvasPainter to paint
     * @param message
     */
    public void onPaintMessageReceived(CWPMessage message);
    /**
     * 
     * @param message
     */
    public void onInvalidMessageReceived(CWPMessage message);
}
