package canvas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;

import javax.swing.JPanel;
import javax.tools.Tool;

import tools.CanvasController;
import tools.EraseController;
import tools.LineController;
import client.WhiteboardClient;

/**
 * Canvas represents a drawing surface that allows the user to draw on it
 * freehand, with the mouse.
 */
public class Canvas extends JPanel {

    public WhiteboardClient mClient;
    private List<Tool> mTools;
    private CanvasController activeController;

    // image where the user's drawing is stored
    private Image drawingBuffer;
    private int brushSize = 3;
    
    private MODE editorMode;
    /**
     * Make a canvas.
     * 
     * @param width
     *            width in pixels
     * @param height
     *            height in pixels
     */
    public Canvas(int width, int height, WhiteboardClient client) {
        this.setPreferredSize(new Dimension(width, height));
        this.mClient = client;
        
      // initializeTools();
      LineController controller = new LineController(this);
      addMouseListener(controller);
      addMouseMotionListener(controller);
        
        setMode(MODE.DRAW_LINE);
        // note: we can't call makeDrawingBuffer here, because it only
        // works *after* this canvas has been added to a window. Have to
        // wait until paintComponent() is first called.
    }

    public void setMode(MODE m) {

        System.out.println("Changing to Mode: " + m);

        removeMouseListener(activeController);
        removeMouseMotionListener(activeController);
        
        editorMode = m;
        switch (editorMode) {
        case DRAW_LINE:
            activeController = new LineController(this);
            break;
        case ERASE:
            activeController = new EraseController(this);
            break;
        default:
            activeController = new LineController(this);
        }
        
        addMouseListener(activeController);
        addMouseMotionListener(activeController);
    }
    
    public enum MODE {
        DRAW_LINE, ERASE
    }
    
//    public void initializeTools() {
//        mTools.add(MODE.DRAW_LINE, new LineTool());
//        mTools.add(MODE.ERASE, new EraseTool());
//    }

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    public void paintComponent(Graphics g) {
        // If this is the first time paintComponent() is being called,
        // make our drawing buffer.
        if (drawingBuffer == null) {
            makeDrawingBuffer();
        }

        // Copy the drawing buffer to the screen.
        g.drawImage(drawingBuffer, 0, 0, null);
    }

    /*
     * Make the drawing buffer and draw some starting content for it.
     */
    private void makeDrawingBuffer() {
        drawingBuffer = createImage(getWidth(), getHeight());
        fillWithWhite();
    }

    /*
     * Make the drawing buffer entirely white.
     */
    private void fillWithWhite() {
        final Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // IMPORTANT! every time we draw on the internal drawing buffer, we
        // have to notify Swing to repaint this component on the screen.
        this.repaint();
    }

    /*
     * Draw a line between two points (x1, y1) and (x2, y2), specified in pixels
     * relative to the upper-left corner of the drawing buffer.
     */
    private void drawLineSegment(int x1, int y1, int x2, int y2) {
        Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(brushSize));
        g.drawLine(x1, y1, x2, y2);

        // IMPORTANT! every time we draw on the internal drawing buffer, we
        // have to notify Swing to repaint this component on the screen.
        this.repaint();
    }

    /*
     * erases
     */
    private void erase(int x1, int y1, int x2, int y2) {
        Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(brushSize));
        g.drawLine(x1, y1, x2, y2);
        this.repaint();
    }

    public void changeMode(MODE m) {
        editorMode = m;
    }

    public void setBrushSize(int b) {
        brushSize = b;
    }

    public void execute(String command) {

        String[] tokens = command.split(" ");

        String cmd = tokens[0];

        if (cmd.equals("drawline")) {

            // Get points
            int lastX = Integer.valueOf(tokens[1]);
            int lastY = Integer.valueOf(tokens[2]);
            int x = Integer.valueOf(tokens[3]);
            int y = Integer.valueOf(tokens[4]);

            // Define Color
            int r = Integer.valueOf(tokens[5]);
            int g = Integer.valueOf(tokens[6]);
            int b = Integer.valueOf(tokens[7]);
            Color color = new Color(r, g, b);

            // Define Brush Size
            int brush = Integer.valueOf(tokens[8]);

            this.drawLineSegment(lastX, lastY, x, y);
        }

        if (cmd.equals("erase")) {
            
            // Get points
            int lastX = Integer.valueOf(tokens[1]);
            int lastY = Integer.valueOf(tokens[2]);
            int x = Integer.valueOf(tokens[3]);
            int y = Integer.valueOf(tokens[4]);

            // Define Brush Size
            int brush = Integer.valueOf(tokens[5]);

            this.erase(lastX, lastY, x, y);
        }
    }
}
