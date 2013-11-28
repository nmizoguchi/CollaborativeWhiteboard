package server;

import client.WhiteboardModel;

public class ClientConnection {
    private final ApplicationServer server;
    private WhiteboardModel activeBoard;
    private int activeBoardVersion;
    private String name;
    
    public ClientConnection(ApplicationServer server, WhiteboardModel model) {
        this.server = server;
        this.activeBoard = model;
    }

    public WhiteboardModel getActiveBoard() {
        return activeBoard;
    }

    public void setActiveBoard(String name) throws NoSuchFieldException {
        this.activeBoard = server.getWhiteboard(name);
        this.activeBoardVersion = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getActiveBoardVersion() {
        return activeBoardVersion;
    }
    
    public void setActiveBoardVersion(int version) {
        this.activeBoardVersion = version;
    }
    
    public String getWhiteboardNames() {
        return server.getWhiteboardNames();
    }
}
