package client;

import Protocol.CWPMessage;

/**
 * 
 * @author 
 *
 */
public interface ClientListener {
	/**
	 * 
	 * @param message
	 */
    public void onNewuserMessageReceived(CWPMessage message);
    /**
     * 
     * @param message
     */
    public void onDisconnecteduserMessageReceived(CWPMessage message);
    /**
     * 
     * @param message
     */
    public void onWhiteboardsMessageReceived(CWPMessage message);
    /**
     * 
     * @param message
     */
    public void onChangeboardMessageReceived(CWPMessage message);
    /**
     * 
     * @param message
     */
    public void onChatMessageReceived(CWPMessage message);
    /**
     * 
     * @param message
     */
    public void onPaintMessageReceived(CWPMessage message);
    /**
     * 
     * @param message
     */
    public void onInvalidMessageReceived(CWPMessage message);
}
