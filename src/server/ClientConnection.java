package server;

import client.Whiteboard;

public class ClientConnection {
    private final ApplicationServer server;
    private Whiteboard activeBoard;
    private int clientBoardVersion;
    private String name;
    
    public ClientConnection(ApplicationServer server, Whiteboard model) {
        this.server = server;
        this.activeBoard = model;
    }

    public Whiteboard getActiveBoard() {
        return activeBoard;
    }

    public void setActiveBoard(String name) throws NoSuchFieldException {
        this.activeBoard = server.getWhiteboard(name);
        this.clientBoardVersion = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getClientBoardVersion() {
        return clientBoardVersion;
    }
    
    public void setClientBoardVersion(int version) {
        this.clientBoardVersion = version;
    }
    
    public String getWhiteboardNames() {
        return server.getWhiteboardNames();
    }
}
