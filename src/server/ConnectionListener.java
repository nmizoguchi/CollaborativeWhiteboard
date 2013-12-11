package server;

/**
 * This interface defines callbacks called when specific actions from the
 * connection occur.
 * 
 * @author Nicholas M. Mizoguchi
 */
public interface ConnectionListener {

    /**
     * Callback method called when the ConnectionController receives a message
     * from the client.
     * 
     * @param callerController
     *            the connectionController that called this method.
     * @param message
     *            the message received.
     */
    public void onMessageReceived(ConnectionController callerController,
            String message);

    /**
     * Callback method called when the ConnectionController requests for updates
     * from the server. This method should already send updates to the client
     * through the callerController.
     * 
     * @param controller
     *            the connectionController that called this method.
     */
    public void onCheckForUpdateRequest(ConnectionController controller);

    /**
     * Callback method called when the client that the ConnectionController was
     * handling disconnects.
     * 
     * @param controller
     *            the connectionController that called this method.
     */
    public void onClientDisconnected(ConnectionController controller);
}
