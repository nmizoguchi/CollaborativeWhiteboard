package client.gui.canvas.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import Protocol.CWPMessage;
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
        
        Image drawingBuffer = canvas.getDrawingBuffer();
        Graphics2D g2 = (Graphics2D) drawingBuffer.getGraphics();

        // Get points
        int x1 = Integer.valueOf(args[0]); //lastX
        int y1 = Integer.valueOf(args[1]); //lastY
        int x2 = Integer.valueOf(args[2]); //currentX
        int y2 = Integer.valueOf(args[3]); //currentY
        int width = Math.abs(x2-x1);
        int height = Math.abs(y2-y1);
        
        //Define brushColor
        int colorInt = Integer.valueOf(args[4]);

        // Define Brush Size
        int brush = Integer.valueOf(args[5]);

        // Define hasFill
        int hasFillInt = Integer.valueOf(args[6]);
        int fillColorInt = Integer.valueOf(args[7]);
        
        // Draw
        
        g2.setStroke(new BasicStroke(brush));
      //rectangle is anchored at the least x and y values (top left corner)
        Rectangle toDraw = new Rectangle(Math.min(x1, x2), Math.min(y1, y2), width, height);
        
        
        if (hasFillInt == 1){
        	g2.setPaint(new Color(fillColorInt));
        	g2.fill(toDraw);
        }
        g2.setColor(new Color(colorInt));
        g2.draw(toDraw);

        canvas.repaint();
    }

    /*
     * When mouse button is pressed down, start drawing.
     * Sets the initial point from which the rectangle will be drawn from
     */
    public void mousePressed(MouseEvent e) {
        brushColor = canvas.getBrushColor();
        brushSize = canvas.getBrushSize();
        hasFill = canvas.hasFill()? 1 : 0; //1 if it has a fill color, 0 if it doesn't
        fillColor = canvas.getFillColor();

    	lastX = e.getX(); lastY = e.getY();
    	rectangle.setFrame(lastX, lastY, 0, 0);
    	updateSize(e);
    }

    /*
     * Sends information to the server and resets the canvas surface shape from 'rectangle' to 'null'
     */
    public void mouseReleased(MouseEvent e) {
        
        String[] arguments = new String[] {
                String.valueOf(lastX),
                String.valueOf(lastY),
                String.valueOf(e.getX()),
                String.valueOf(e.getY()),
                String.valueOf(brushColor),
                String.valueOf(brushSize),
                String.valueOf(hasFill),
                String.valueOf(fillColor) };
        String message = CWPMessage.Encode(canvas.mClient.getUser(), "drawrect",
                arguments);
        canvas.mClient.scheduleMessage(message);
        
        canvas.setSurfaceShape(null);
    }

    /*
     * Updates the current rectangle as the mouse is being dragged
     */
    public void mouseDragged(MouseEvent e) {
        updateSize(e);
    }
    
    /*
     * Updates the current rectangle
     */
    private void updateSize(MouseEvent e){
    	int x = e.getX();
    	int y = e.getY();
    	
    	int width = (int) Math.abs(x - lastX);
    	int height = (int) Math.abs(y - lastY);
    	rectangle.setFrame(Math.min(x, lastX), Math.min(y, lastY), width, height);
    	canvas.setSurfaceShape(rectangle);
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