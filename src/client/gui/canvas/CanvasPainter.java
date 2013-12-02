package client.gui.canvas;

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
    private final String command;

    public CanvasPainter(Canvas canvas, String command) {
        this.canvas = canvas;
        this.command = command;
    }

    @Override
    public void run() {
        canvas.execute(command);
    }
}
