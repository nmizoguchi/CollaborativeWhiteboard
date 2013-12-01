package tools;

import client.gui.canvas.Canvas;

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
