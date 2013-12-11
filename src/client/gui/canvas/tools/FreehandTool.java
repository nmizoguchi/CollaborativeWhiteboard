package client.gui.canvas.tools;

import client.gui.canvas.Canvas;

/**
 * Implements Tool. This is used when the user selects the freehand tool.
 * This is associated with the draw controller.
 * @author rcha
 *
 */
public class FreehandTool implements Tool {

    public final String name = "Freehand Drawing";
    public final ToolController controller;

    public FreehandTool(Canvas canvas) {
        this.controller = new DrawController(canvas);
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
