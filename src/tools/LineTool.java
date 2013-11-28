package tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import canvas.Canvas;

public class LineTool implements Tool {

    public final String name = "Draw Line";
    public final CanvasController controller;

    public LineTool(Canvas canvas) {
        this.controller = new LineController(canvas);
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
