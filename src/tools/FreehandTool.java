package tools;

import client.gui.canvas.Canvas;

public class FreehandTool implements Tool {

    public final String name = "Freehand Drawing";
    public final ToolController controller;

    public FreehandTool(Canvas canvas) {
        this.controller = new FreehandController(canvas);
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
