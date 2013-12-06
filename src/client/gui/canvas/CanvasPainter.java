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
    private final String action;
    private final String[] args;

    public CanvasPainter(Canvas canvas, CWPMessage message) {
        this.canvas = canvas;
        this.action = message.getAction();
        this.args = message.getArguments();
    }

    @Override
    public void run() {
        canvas.execute(action, args);
    }
}
