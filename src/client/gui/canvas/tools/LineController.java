package client.gui.canvas.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

import Protocol.Protocol;
import client.gui.canvas.Canvas;

/*
 * DrawingController handles the user's freehand drawing.
 */
public class LineController implements ToolController {
    // store the coordinates of the last mouse event, so we can
    // draw a line segment from that last point to the point of the next mouse
    // event.
    private final Canvas canvas;
    private int lastX, lastY;

    private int brushSize, brushColor, hasFill, fillColor;

    private Line2D line;

    public LineController(Canvas canvas) {
        this.canvas = canvas;
        this.line = new Line2D.Double();
    }

    @Override
    public void paint(String[] args) {
        /*
         * Draw a line between two points (x1, y1) and (x2, y2), specified in
         * pixels relative to the upper-left corner of the drawing buffer.
         */

        Image drawingBuffer = canvas.getDrawingBuffer();
        Graphics2D g2 = (Graphics2D) drawingBuffer.getGraphics();

        // Get points
        int lastX = Integer.valueOf(args[1]);
        int lastY = Integer.valueOf(args[2]);
        int x = Integer.valueOf(args[3]);
        int y = Integer.valueOf(args[4]);

        Color color = new Color(Integer.valueOf(args[5]));

        // Define Brush Size
        int brush = Integer.valueOf(args[6]);
        
        g2.setStroke(new BasicStroke(brush));
        g2.setColor(color);
        g2.drawLine(lastX, lastY, x, y);

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
        
        // Get color
        brushColor = canvas.getBrushColor();

        // Get brushSize

        brushSize = canvas.getBrushSize();

        line.setLine(lastX, lastY, lastX, lastY);
        canvas.setSurfaceShape(line);
    }

    public void mouseReleased(MouseEvent e) {
        // Get position
        int x = e.getX();
        int y = e.getY();

        // Sends info to the server
        String arguments = lastX + " " + lastY + " " + x + " " + y + " "
                + brushColor + " " + brushSize;
        String message = Protocol.CreateMessage(canvas.mClient.getUser(),
                "drawline", arguments);
        canvas.mClient.scheduleMessage(message);

        canvas.setSurfaceShape(null);
    }

    public void mouseDragged(MouseEvent e) {
        line.setLine(lastX, lastY, e.getX(), e.getY());
        canvas.repaint();
    }

    // Ignore all these other mouse events.
    public void mouseMoved(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
    
}