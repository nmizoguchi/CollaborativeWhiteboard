package tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import client.gui.canvas.Canvas;

/*
 * DrawingController handles the user's freehand drawing.
 */
public class RectangleController implements ToolController {
    // store the coordinates of the last mouse event, so we can
    // draw a line segment from that last point to the point of the next mouse
    // event.
    private final Canvas canvas;
    private int lastX, lastY;

    private int brushSize, brushColor;
    private int hasFill, fillColor;

    private Rectangle2D rectangle;

    public RectangleController(Canvas canvas) {
        this.canvas = canvas;
        this.rectangle = new Rectangle2D.Double();
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
        int x = Integer.valueOf(args[1]);
        int y = Integer.valueOf(args[2]);
        int width = Integer.valueOf(args[3]) - x;
        int height = Integer.valueOf(args[4]) - y;
        
        //Define brushColor
        int colorInt = Integer.valueOf(args[5]);

        // Define Brush Size
        int brush = Integer.valueOf(args[6]);

        // Define hasFill
        int hasFillInt = Integer.valueOf(args[7]);
        
        // Draw
        g2.setColor(new Color(colorInt));
        g2.setStroke(new BasicStroke(brush));
        g2.drawRect(x, y, width, height);

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

        // Get brush color
        brushColor = canvas.getBrushColor();


        // Get brushSize

        brushSize = canvas.getBrushSize();

        // Get brush color
//        color = canvas.getFillColor();

        // Check if it has fill
        if (canvas.hasFill()) {
            hasFill = 1;
        } else {
            hasFill = 0;
        }

        rectangle.setFrame(lastX, lastY, 0, 0);
        canvas.setSurfaceShape(rectangle);
    }

    public void mouseReleased(MouseEvent e) {
        // Get position
        int x = e.getX();
        int y = e.getY();
        
        // Sends info to the server
        canvas.mClient.send("drawrect " + lastX + " " + lastY + " " + x + " "
                + y + " " + brushColor + " "
                + brushSize + " " + hasFill + " " + fillColor);
        canvas.setSurfaceShape(null);
    }

    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        int width = x - lastX;
        int height = y - lastY;
        rectangle.setFrame(lastX, lastY, width, height);
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