package client.gui.canvas.tools;

import client.gui.canvas.Canvas;

/**
 * Implements Tool. This is used when the user selects the rectangle tool.
 * This is associated with the rectangle controller.
 * @author rcha
 *
 */
public class RectangleTool implements Tool {

    public final String name = "Draw Rectangle";
    public final ToolController controller;

    public RectangleTool(Canvas canvas) {
        this.controller = new RectangleController(canvas);
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
