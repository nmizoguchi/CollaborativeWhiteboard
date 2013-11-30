package tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import canvas.Canvas;

public class EraseTool implements Tool {

    public final String name = "Erase";
    public final ToolController controller;

    public EraseTool(Canvas canvas) {
        this.controller = new EraseController(canvas);
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
