package server.controllers;


public interface ConnectionMessageListener {
    public void onMessageReceived(ConnectionController callerController, String message);
    public void onCheckForUpdateRequest(ConnectionController controller);
}
