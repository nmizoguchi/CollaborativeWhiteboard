package client.gui.canvas.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.NoSuchElementException;

import Protocol.CWPMessage;
import client.gui.canvas.Canvas;
import client.gui.canvas.UserTrackerView;

/**
 * DrawingController handles the user's freehand drawing.
 */
public class EraseController implements ToolController {
    
    private final Canvas canvas;
    private int brushSize, lastX, lastY;

    /* Stores the coordinates of the last mouse event, so we can 
     * draw a line segment from that last point to the point of the next mouse event.
     * 
     */
    public EraseController(Canvas c) {
        this.canvas = c;
    }

    @Override
    public void paint(CWPMessage message) {
        /*
         * Erase in a line format between two points (x1, y1) and (x2, y2), specified in
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

        // Define Color
        Color color = Color.WHITE;

        // Define Brush Size
        int brushSizeInt = Integer.valueOf(args[4]);

        g2.setColor(color);
        g2.setStroke(new BasicStroke(brushSizeInt));
        g2.drawLine(lastX, lastY, x, y);

        lastX = x;
        lastY = y;
        
        try {
            UserTrackerView tracker = canvas.getUserTracker(message.getSenderUID());
            tracker.setX(lastX);
            tracker.setY(lastY);
            tracker.setTimer();
        } catch (NoSuchElementException e) {
            // The user that painted has already disconnected!
        }

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
        String[] arguments = new String[] { String.valueOf(lastX),
                String.valueOf(lastY), String.valueOf(x), String.valueOf(y),
                String.valueOf(brushSize) };
        String message = CWPMessage.Encode(canvas.GUI.getClient().getUser(), "erase",
                arguments);
        
        canvas.GUI.getClient().scheduleMessage(message);

        lastX = x;
        lastY = y;
    }

    // Ignore all these other mouse events.
    public void mouseMoved(MouseEvent e) {    }

    public void mouseClicked(MouseEvent e) {    }

    public void mouseReleased(MouseEvent e) {    }

    public void mouseEntered(MouseEvent e) {    }

    public void mouseExited(MouseEvent e) {   }
}