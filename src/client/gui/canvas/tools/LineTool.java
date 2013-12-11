package client.gui.canvas.tools;


import client.gui.canvas.Canvas;

/**
 * Implements Tool. This is used when the user selects the line tool.
 * This is associated with the line controller
 * @author rcha
 *
 */
public class LineTool implements Tool {

    public final String name = "Draw Line";
    public final ToolController controller;

    public LineTool(Canvas canvas) {
        this.controller = new LineController(canvas);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ToolController getController() {
        return controller;
    }

}
