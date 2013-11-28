package server;

import client.WhiteboardModel;

public class ClientConnection {
    private final ApplicationServer server;
    private WhiteboardModel activeModel;
    private int activeModelVersion;
    private String name;
    
    public ClientConnection(ApplicationServer server, WhiteboardModel model) {
        this.server = server;
        this.activeModel = model;
    }

    public WhiteboardModel getActiveModel() {
        return activeModel;
    }

    public void setActiveModel(String name) {
        this.activeModel = activeModel;
        this.activeModelVersion = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getActiveModelVersion() {
        return activeModelVersion;
    }

    public void setActiveModelVersion(int activeModelVersion) {
        this.activeModelVersion = activeModelVersion;
    }
    
}
