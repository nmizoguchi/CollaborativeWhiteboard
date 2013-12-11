package client;

import Protocol.CWPMessage;

/**
 * 
 * @author
 * 
 */
public interface ClientListener {
    /**
     * Receives a message of the announcement of a new user Calls
     * RunnableNewuser and RunnableChat to update the chatbox and userlist
     * 
     * @param message CWPMessage object that represents the message received from the server.
     */
    public void onNewuserMessageReceived(CWPMessage message);

    /**
     * Receives a message of the announcement of a user that disconnected. Calls
     * RunnableDisconnecteduser and RunnableChat to update the chatbox and
     * userlist
     * 
     * @param message CWPMessage object that represents the message received from the server.
     */
    public void onDisconnecteduserMessageReceived(CWPMessage message);

    /**
     * Receives a message that a new whiteboard was created and updates the list
     * of existing whiteboards
     * 
     * @param message CWPMessage object that represents the message received from the server.
     */
    public void onWhiteboardsMessageReceived(CWPMessage message);

    /**
     * Receives a message that a user is switching whiteboards Calls
     * RunnableChangeboard
     * 
     * @param message CWPMessage object that represents the message received from the server.
     */
    public void onChangeboardMessageReceived(CWPMessage message);

    /**
     * Receives a message that contains all online users in the same board as this client.
     * @param message CWPMessage object that represents the message received from the server.
     */
    public void onReceiveUpdatedUsersOnBoard(CWPMessage message);
    /**
     * Receives a chat message from a user and calls RunnableChat to update the
     * chatbox
     * 
     * @param message CWPMessage object that represents the message received from the server.
     */
    public void onChatMessageReceived(CWPMessage message);

    /**
     * Receives a message that the user is painting on the canvas and calls
     * CanvasPainter to paint
     * 
     *@param message CWPMessage object that represents the message received from the server.
     */
    public void onPaintMessageReceived(CWPMessage message);

    /**
     * Callback called when the server attempts to send an invalid message.
     * @param message The String sent from the server, which is invalid.
     */
    public void onInvalidMessageReceived(String invalidMessage);
}
