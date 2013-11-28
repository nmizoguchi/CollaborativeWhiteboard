package canvas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import tools.CanvasController;
import tools.EraseTool;
import tools.FreehandTool;
import tools.LineTool;
import tools.Tool;
import client.WhiteboardClient;

/**
 * Canvas represents a drawing surface that allows the user to draw on it
 * freehand, with the mouse.
 */
public class Canvas extends JPanel {

    public WhiteboardClient mClient;
    private CanvasController activeController;
    private List<Tool> mTools;

    // image where the user's drawing is stored
    private Shape surfaceShape;
    private Image drawingBuffer;
    private int brushSize = 3;
    private Color brushColor = Color.BLACK;

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

        this.mTools = new ArrayList<Tool>();
        initializeTools();

        // Set mode to FREEHAND by default
        editorMode = MODE.FREEHAND;
        activeController = mTools.get(MODE.FREEHAND.ordinal()).getController();
        addMouseListener(activeController);
        addMouseMotionListener(activeController);
    }

    public enum MODE {
        FREEHAND, ERASE, LINE
    }

    public void initializeTools() {
        mTools.add(MODE.FREEHAND.ordinal(), new FreehandTool(this));
        mTools.add(MODE.ERASE.ordinal(), new EraseTool(this));
        mTools.add(MODE.LINE.ordinal(), new LineTool(this));
    }

    public void execute(String command) {

        String[] tokens = command.split(" ");
        String cmd = tokens[0];

        MODE action = MODE.FREEHAND;

        if (cmd.equals("freehand"))
            action = MODE.FREEHAND;

        if (cmd.equals("erase"))
            action = MODE.ERASE;

        if (cmd.equals("drawline"))
            action = MODE.LINE;

        mTools.get(action.ordinal()).getController().paint(tokens);
    }

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

        // If there is some shape being drawn only on client-side, show it
        if (surfaceShape != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(brushColor);
            g2.setStroke(new BasicStroke(brushSize));
            g2.draw(surfaceShape);
        }
    }

    public void setMode(MODE m) {

        removeMouseListener(activeController);
        removeMouseMotionListener(activeController);

        editorMode = m;
        Tool currentTool = mTools.get(m.ordinal());
        activeController = currentTool.getController();

        addMouseListener(activeController);
        addMouseMotionListener(activeController);
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

    public Image getBuffer() {
        return drawingBuffer;
    }

    public void changeMode(MODE m) {
        editorMode = m;
    }

    public Shape getSurfaceShape() {
        return surfaceShape;
    }

    public void setSurfaceShape(Shape surfaceShape) {
        this.surfaceShape = surfaceShape;
    }

    public void setBrushSize(int b) {
        brushSize = b;
    }

    public int getBrushSize() {
        return brushSize;
    }

    public Color getBrushColor() {
        return brushColor;
    }

    public void setBrushColor(Color brushColor) {
        this.brushColor = brushColor;
    }
}
