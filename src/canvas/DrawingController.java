package canvas;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import client.WhiteboardGUI;
import canvas.Canvas.MODE;

/*
 * DrawingController handles the user's freehand drawing.
 */
public class DrawingController implements CanvasController {
    // store the coordinates of the last mouse event, so we can
    // draw a line segment from that last point to the point of the next mouse event.
    private final Canvas canvas;
    private int lastX, lastY; 

    public DrawingController(Canvas canvas) {
        this.canvas = canvas;
    }
    
    /*
     * When mouse button is pressed down, start drawing.
     */
    public void mousePressed(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
    }

    /*
     * When mouse moves while a button is pressed down,
     * draw a line segment.
     */
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // Sends info to the server
        canvas.client.send("drawline "+lastX+" "+lastY+" "+x+" "+y+" 10 20 30 5");
        
        lastX = x;
        lastY = y;
    }

    // Ignore all these other mouse events.
    public void mouseMoved(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
}