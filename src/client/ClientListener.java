package client;

import Protocol.CWPMessage;


public interface ClientListener {
    public void onNewuserMessageReceived(CWPMessage message);
    public void onDisconnecteduserMessageReceived(CWPMessage message);
    public void onWhiteboardsMessageReceived(CWPMessage message);
    public void onChangeboardMessageReceived(CWPMessage message);
    public void onChatMessageReceived(CWPMessage message);
    public void onPaintMessageReceived(CWPMessage message);
    public void onInvalidMessageReceived(CWPMessage message);
}