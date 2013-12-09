package client.gui.canvas;

import Protocol.CWPMessage;

/**
 * Implements Runnable. It is responsible for painting an action of the
 * Whiteboard over the Canvas. It exists so it keeps the GUI single threaded, by
 * putting an instance of this object in the queue of actions to be done by the
 * GUI.
 * 
 * @author Nicholas M. Mizoguchi
 * 
 */
public class CanvasPainter implements Runnable {

    private final Canvas canvas;
    private final CWPMessage message;
    /**
     * Constructor method
     * @param canvas is the canvas used to draw on
     * @param message is a non-null CWPMessage that contains an action to perform 
     * 			and arguments needed for the canvas tools to draw on the canvas
     */
    public CanvasPainter(Canvas canvas, CWPMessage message) {
        this.canvas = canvas;
        this.message = message;
    }

    /**
     * Performs the action on the canvas, using the given arguments
     */
    @Override
    public void run() {
        canvas.execute(message);
    }
}
