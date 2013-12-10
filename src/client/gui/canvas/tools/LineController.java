package client.gui.canvas.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.NoSuchElementException;

import Protocol.CWPMessage;
import client.gui.canvas.Canvas;
import client.gui.canvas.UserTrackerView;

/**
 * DrawingController handles the user's freehand drawing.
 */
public class LineController implements ToolController {
    // store the coordinates of the last mouse event, so we can
    // draw a line segment from that last point to the point of the next mouse
    // event.
	private Canvas canvas;
    private int lastX, lastY;

    private int brushSize, brushColor;

    private Line2D line;

    public LineController(Canvas c) {
    	canvas = c;
        this.line = new Line2D.Double();
    }

    @Override
    public void paint(CWPMessage message) {
        /*
         * Draw a line between two points (x1, y1) and (x2, y2), specified in
         * pixels relative to the upper-left corner of the drawing buffer.
         */
        
        String[] args = message.getArguments();

        Image drawingBuffer = canvas.getDrawingBuffer();
        Graphics2D g2 = (Graphics2D) drawingBuffer.getGraphics();

        // Get points
        int lastX = Integer.valueOf(args[0]);
        int lastY = Integer.valueOf(args[1]);
        int x = Integer.valueOf(args[2]);
        int y = Integer.valueOf(args[3]);

        Color color = new Color(Integer.valueOf(args[4]));

        // Define Brush Size
        int brush = Integer.valueOf(args[5]);
        
        g2.setStroke(new BasicStroke(brush));
        g2.setColor(color);
        g2.drawLine(lastX, lastY, x, y);
        
        try {
            UserTrackerView tracker = canvas.getUserTracker(message.getSenderUID());
            tracker.setX(x);
            tracker.setY(y);
            tracker.setTimer();
        } catch (NoSuchElementException e) {
            // The user that painted has already disconnected!
        }
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
        String[] arguments = new String[] {
                String.valueOf(lastX),
                String.valueOf(lastY),
                String.valueOf(x),
                String.valueOf(y),
                String.valueOf(brushColor),
                String.valueOf(brushSize)};
        String message = CWPMessage.Encode(canvas.GUI.getClient().getUser(), "drawline",
                arguments);
        canvas.GUI.getClient().scheduleMessage(message);

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