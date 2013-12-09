package client.gui.canvas.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.NoSuchElementException;

import client.gui.canvas.Canvas;
import client.gui.canvas.UserTrackerView;
import Protocol.CWPMessage;

/**
 * Implements ToolController. This class is in charge of drawing on the canvas when
 * the user selects either the ERASE tool or FREEHAND tool.
 * @see ToolController
 * @author rcha
 *
 */
public class DrawController implements ToolController{

	private final Canvas canvas;
    private int brushSize, brushColor, lastX, lastY;
    private String editorMode;
    
    /**
     * Takes in the canvas that the user is painting on
     * @param c is the Canvas that user sees
     */
	public DrawController(Canvas c) {
		canvas = c;
	}
	

	@Override
	public void paint(CWPMessage message) {
		/*
         * Draw in a line format between two points (x1, y1) and (x2, y2), specified in
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
        Color color = new Color(Integer.valueOf(args[4]));

        // Define Brush Size
        int brushSizeInt = Integer.valueOf(args[5]);

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

        canvas.repaint();
		
	}

	/**
	 * When the mouse is pressed, this sets the brush color, size and coordinates 
	 * and sends information to the server to draw
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		lastX = e.getX();
        lastY = e.getY();
        editorMode = canvas.getMode().name();
        //if the mode is "ERASE", then set the color to white
        //else, the mode is "LINE" and set the color to the canvas brush color
        brushColor = editorMode.equals("ERASE")? Color.WHITE.getRGB() : canvas.getBrushColor();
        brushSize = canvas.getBrushSize();
        sendInfo(lastX, lastY);
		
	}

	/**
	 * When the mouse is dragged, this continues to get updates of the brush size
	 * and gets the coordinates of the mouse. Then it sends the information to the server.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
        int y = e.getY();
        brushSize = canvas.getBrushSize();
        sendInfo(x, y);
        lastX = x;
        lastY = y;
		
	}
	/**
	 * This takes in the current mouse coordinates and puts together information needed to draw in the form of:
	 * {int lastX, int lastY, int x, int y, int brushColor, int brushSize}
	 * Then it sends the information to the server
	 * @param x is the current X coordinate of the mouse
	 * @param y is the current Y coordinate of the mouse
	 */
	 private void sendInfo(int x, int y){
	    	// Sends info to the server
	        String[] arguments = new String[] {
	        		String.valueOf(lastX),
	                String.valueOf(lastY), 
	                String.valueOf(x), 
	                String.valueOf(y),
	                String.valueOf(brushColor),
	                String.valueOf(brushSize)};
	        String message = CWPMessage.Encode(
	        		canvas.GUI.getClient().getUser(), 
	        		"erase",
	                arguments);
	        
	        canvas.GUI.getClient().scheduleMessage(message);
	    }

	 //Ignore these mouse events
	@Override
	public void mouseMoved(MouseEvent e) {
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}
	

}
