package tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import canvas.Canvas;

/*
 * DrawingController handles the user's freehand drawing.
 */
public class RectangleController implements CanvasController {
    // store the coordinates of the last mouse event, so we can
    // draw a line segment from that last point to the point of the next mouse
    // event.
    private final Canvas canvas;
    private int lastX, lastY;

    private int brushSize, rBrush, gBrush, bBrush;
    private int hasFill, rFill, gFill, bFill;

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
        int width = Integer.valueOf(args[3]);
        int height = Integer.valueOf(args[4]);

        // Define Color
        int r = Integer.valueOf(args[5]);
        int g = Integer.valueOf(args[6]);
        int b = Integer.valueOf(args[7]);

        Color brushColor = new Color(r, g, b);

        // Define Brush Size
        int brush = Integer.valueOf(args[8]);
        
        // Define Color
        r = Integer.valueOf(args[9]);
        g = Integer.valueOf(args[10]);
        b = Integer.valueOf(args[11]);

        Color fillColor = new Color(r,g,b);
        
        // Define hasFill
        int hasFill = Integer.valueOf(args[12]);
        
        // Draw
        g2.setColor(brushColor);
        g2.setStroke(new BasicStroke(brush));
        if(hasFill == 1) {
            g2.setColor(fillColor);
//            g2.fill();
        }
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
        Color color = canvas.getBrushColor();

        rBrush = color.getRed();
        gBrush = color.getGreen();
        bBrush = color.getBlue();

        // Get brushSize

        brushSize = canvas.getBrushSize();

        // Get brush color
        color = canvas.getFillColor();

        rFill = color.getRed();
        gFill = color.getGreen();
        bFill = color.getBlue();

        // Check if it has fill
        if (canvas.hasFill()) {
            hasFill = 1;
        } else {
            hasFill = 0;
        }

        rectangle.setRect(lastX, lastY, 0, 0);
        canvas.setSurfaceShape(rectangle);
    }

    public void mouseReleased(MouseEvent e) {
        // Get position
        int x = e.getX();
        int y = e.getY();

        // Sends info to the server
        canvas.mClient.send("drawrect " + lastX + " " + lastY + " " + x + " "
                + y + " " + rBrush + " " + gBrush + " " + bBrush + " "
                + brushSize + " " + rFill + " " + gFill + " " + bFill + " "
                + hasFill);

        canvas.setSurfaceShape(null);
    }

    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        int width = x - lastX;
        int height = y - lastY;
        rectangle.setRect(lastX, lastY, width, height);
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