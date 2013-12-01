package tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;

import client.gui.canvas.Canvas;

/*
 * DrawingController handles the user's freehand drawing.
 */
public class EraseController implements ToolController {
    // store the coordinates of the last mouse event, so we can
    // draw a line segment from that last point to the point of the next mouse
    // event.
    private final Canvas canvas;
    private int brushSize, lastX, lastY;

    public EraseController(Canvas canvas) {
        this.canvas = canvas;
    }
    

    @Override
    public void paint(String[] args) {
        /*
         * Draw a line between two points (x1, y1) and (x2, y2), specified in
         * pixels relative to the upper-left corner of the drawing buffer.
         */

        Image drawingBuffer = canvas.getBuffer();
        Graphics2D g2 = (Graphics2D) drawingBuffer.getGraphics();

        // Get points
        int lastX = Integer.valueOf(args[1]);
        int lastY = Integer.valueOf(args[2]);
        int x = Integer.valueOf(args[3]);
        int y = Integer.valueOf(args[4]);

        // Define Color
        Color color = Color.WHITE;

        // Define Brush Size
        int brushSizeInt = Integer.valueOf(args[5]);

        g2.setColor(color);
        g2.setStroke(new BasicStroke(brushSizeInt));
        g2.drawLine(lastX, lastY, x, y);

        lastX = x;
        lastY = y;

        // IMPORTANT! every time we draw on the internal drawing buffer, we
        // have to notify Swing to repaint this component on the screen.
        canvas.repaint();
    }

    /*
     * When mouse button is pressed down, start drawing.
     */
    public void mousePressed(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
        brushSize = canvas.getBrushSize();
    }

    /*
     * When mouse moves while a button is pressed down, draw a line segment.
     */
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // Sends info to the server
        canvas.mClient.send("erase " + lastX + " " + lastY + " " + x + " " + y + " "
                + brushSize);

        lastX = x;
        lastY = y;
    }

    // Ignore all these other mouse events.
    public void mouseMoved(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}