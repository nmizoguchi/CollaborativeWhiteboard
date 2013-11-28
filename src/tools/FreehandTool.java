package tools;

import canvas.Canvas;

public class FreehandTool implements Tool {

    public final String name = "Freehand Drawing";
    public final CanvasController controller;

    public FreehandTool(Canvas canvas) {
        this.controller = new FreehandController(canvas);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CanvasController getController() {
        return controller;
    }

}
