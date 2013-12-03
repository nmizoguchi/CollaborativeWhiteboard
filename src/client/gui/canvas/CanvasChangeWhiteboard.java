package client.gui.canvas;

import java.util.List;

import client.gui.WhiteboardListModel;

public class CanvasChangeWhiteboard implements Runnable {
    
    private final List<String> names;
    private final WhiteboardListModel whiteboards;
    
    public CanvasChangeWhiteboard(WhiteboardListModel whiteboards, List<String> names) {
        this.names = names;
        this.whiteboards = whiteboards;
    }

    @Override
    public void run() {
        for(String name : names) {
            if( !whiteboards.contains(name) ) {
                whiteboards.addElement(name);
            }
        }
    }
}
