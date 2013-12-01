package client.gui.canvas;


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
