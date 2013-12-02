package server;

/**
 * This interface defines a handler that knows how to handle the output stream
 * with the client.
 * 
 * @author Nicholas M. Mizoguchi
 * 
 */
public interface ConnectionOutputHandler {
    /**
     * Schedules a message to be sent to the client.
     * 
     * @param message
     *            a String to be sent.
     */
    public void scheduleMessage(String message);
}