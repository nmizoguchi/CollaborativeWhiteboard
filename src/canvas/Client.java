package canvas;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	public Client() throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ols = new ObjectInputStream(new FileInputStream("E:\\mitjdw-nickmm-rcha.whiteboard"));
		
		Socket sock = new Socket("localhost", 12345);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);
		
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		
		Canvas temp = (Canvas) ols.readObject();
		temp.isServer = false;
		temp.addDrawingController(); 
		//temp.paintComponent(g);
		temp.makeDrawingBuffer();
		
		int lastX = 0;
		int lastY = 0;
		int x = 0;
		int y = 0;
		
		class DrawingController implements MouseListener, MouseMotionListener {
	        // store the coordinates of the last mouse event, so we can
	        // draw a line segment from that last point to the point of the next mouse event.
	        private int lastX, lastY; 

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
		
		while (true) {
			out.println(temp.drawLineSegment(lastX, lastY, x, y));
		}
	}
}
