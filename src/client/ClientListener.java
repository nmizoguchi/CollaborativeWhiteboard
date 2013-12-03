package client;

import Protocol.Protocol;


public interface ClientListener {
    public void onNewuserMessageReceived(Protocol message);
    public void onDisconnecteduserMessageReceived(Protocol message);
    public void onWhiteboardsMessageReceived(Protocol message);
    public void onChangeboardMessageReceived(Protocol message);
    public void onChatMessageReceived(Protocol message);
    public void onPaintMessageReceived(Protocol message);
    public void onInvalidMessageReceived(Protocol message);
}
