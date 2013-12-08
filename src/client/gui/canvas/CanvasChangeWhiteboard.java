package client.gui.canvas;

import java.util.List;

import client.gui.WhiteboardListModel;

/**
 * Implements Runnable. It updates the list of whiteboard names.
 * @author rcha
 */
public class CanvasChangeWhiteboard implements Runnable {
    
    private final List<String> names;
    private final WhiteboardListModel whiteboards;
    
    /**
     * Constructor method that saves an instance of List<String> and WhiteboardListModel
     * @param whiteboards is a WhiteboardListModel that contains a list of current whiteboards
     * @param names is a List<String> of names of new whiteboards to add to the WhiteboardListModel
     */
    public CanvasChangeWhiteboard(WhiteboardListModel whiteboards, List<String> names) {
        this.names = names;
        this.whiteboards = whiteboards;
    }

    /**
     * This methods adds each new whiteboard name to the list of names of existing whiteboards.
     */
    @Override
    public void run() {
        for(String name : names) {
            if( !whiteboards.contains(name) ) {
                whiteboards.addElement(name);
            }
        }
    }
}
