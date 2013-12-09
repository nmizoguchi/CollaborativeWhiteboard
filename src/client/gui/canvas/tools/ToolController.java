package client.gui.canvas.tools;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import Protocol.CWPMessage;

/**
 * This takes the user's actions of drawing on the canvas and then sends a message to the server.
 * The server then sends the message to the rest of the clients and all of the canvases are updated 
 * with the user's drawing.
 * @author rcha
 *
 */
public interface ToolController extends MouseListener, MouseMotionListener {
    /**
     * Makes use of Graphics2D to paint on the canvas using the information received from the server
     * @param message is a CWPMessage that the server has sent, containing information needed to draw
     */
	public void paint(CWPMessage message);
}
