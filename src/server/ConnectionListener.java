package server;


public interface ConnectionListener {
    public void onMessageReceived(ConnectionController callerController, String message);
    public void onCheckForUpdateRequest(ConnectionController controller);
    public void onClientDisconnected(ConnectionController controller);
}
