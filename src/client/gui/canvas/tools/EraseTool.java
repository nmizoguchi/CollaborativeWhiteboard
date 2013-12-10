package client.gui.canvas.tools;

import client.gui.canvas.Canvas;

/**
 * Implements Tool. This is used when the user selects the eraser tool.
 * This is associated with the draw controller.
 * @author rcha
 *
 */
public class EraseTool implements Tool {

    public final String name = "Erase";
    public final ToolController controller;

    public EraseTool(Canvas canvas) {
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
