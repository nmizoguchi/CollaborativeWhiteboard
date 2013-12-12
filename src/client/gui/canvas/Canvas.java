package client.gui.canvas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import Protocol.CWPMessage;
import shared.models.User;
import client.gui.UserListModel;
import client.gui.WhiteboardGUI;
import client.gui.canvas.tools.EraseTool;
import client.gui.canvas.tools.FreehandTool;
import client.gui.canvas.tools.LineTool;
import client.gui.canvas.tools.RectangleTool;
import client.gui.canvas.tools.Tool;
import client.gui.canvas.tools.ToolController;

/**
 * Canvas represents a drawing surface that allows the user to draw on it with the mouse
 */
public class Canvas extends JPanel {

    public WhiteboardGUI GUI;
    private ToolController activeController;
    private List<Tool> mTools;

    // image where the user's drawing is stored
    private Shape surfaceShape;
    private Image drawingBuffer;
    private int brushSize = 3;
    private int brushColor = Color.BLACK.getRGB();
    private boolean hasFill = false;
    private int fillColor = Color.BLACK.getRGB();
    private Map<String, UserTrackerView> userTrackers;

    private MODE editorMode;

    /**
     * Make a canvas.
     * 
     * @param width
     *            width in pixels
     * @param height
     *            height in pixels
     */
    public Canvas(int width, int height, WhiteboardGUI gui) {

        this.userTrackers = new HashMap<String, UserTrackerView>();

        this.setPreferredSize(new Dimension(width, height));
        this.GUI = gui;

        this.mTools = new ArrayList<Tool>();
        initializeTools();

        // Set mode to FREEHAND by default
        editorMode = MODE.FREEHAND;
        activeController = mTools.get(MODE.FREEHAND.ordinal()).getController();
        addMouseListener(activeController);
        addMouseMotionListener(activeController);

    }

    /**
     * These are the different drawing modes
     */
    public enum MODE {
        FREEHAND, ERASE, LINE, RECTANGLE
    }

    
    private void initializeTools() {
        mTools.add(MODE.FREEHAND.ordinal(), new FreehandTool(this));
        mTools.add(MODE.ERASE.ordinal(), new EraseTool(this));
        mTools.add(MODE.LINE.ordinal(), new LineTool(this));
        mTools.add(MODE.RECTANGLE.ordinal(), new RectangleTool(this));
    }

    /**
     * Receives a message and sets the drawing mode depending on the action in the message.
     * These actions include: "freehand", "erase", "drawline", "drawrect".
     * The default action mode is FREEHAND. After determining the mode, the tool selected
     * then paints on the canvas using the information provided by the message
     * @param message is a CWPMessage
     */
    public void execute(CWPMessage message) {
        String action = message.getAction();
        MODE actionMode = MODE.FREEHAND;

        if (action.equals("freehand"))
            actionMode = MODE.FREEHAND;

        else if (action.equals("erase"))
            actionMode = MODE.ERASE;

        else if (action.equals("drawline"))
            actionMode = MODE.LINE;

        else if (action.equals("drawrect"))
            actionMode = MODE.RECTANGLE;

        mTools.get(actionMode.ordinal()).getController().paint(message);
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

        Graphics2D g2 = (Graphics2D) g;

        // If there is some shape being drawn only on client-side, show it
        if (surfaceShape != null) {
            g2.setStroke(new BasicStroke(brushSize));
            if (hasFill) {
                g2.setColor(new Color(fillColor));
                g2.fill(surfaceShape);
            }
            g2.setColor(new Color(brushColor));
            g2.draw(surfaceShape);
        }

        // Draw the user trackers
        for (UserTrackerView userTracker : userTrackers.values()) {
            
            // Draws a shadow
            g2.setColor(userTracker.getShadowColor());
            for (int i = -1; i <= 0; i++) {
                for (int j = -1; j <= 0; j++) {
                    g2.drawString(userTracker.getUsername(), userTracker.getX()
                            - i, userTracker.getY() - j);
                }
            }
            
            // Draw the name itself
            g2.setColor(userTracker.getColor());
            for (int i = -1; i <= 0; i++) {
                for (int j = -1; j <= 0; j++) {
                    g2.drawString(userTracker.getUsername(), userTracker.getX()
                            + i, userTracker.getY() + j);
                }
            }
        }
    }
    /**
     * Returns whether or not the rectangle to be drawn has a fill color
     * @return hasFill is a boolean
     */
    public boolean hasFill() {
        return hasFill;
    }

    /**
     * Sets whether or not the rectangle to be drawn will have a fill color
     * @param hasFill is a boolean
     */
    public void setHasFill(boolean hasFill) {
        this.hasFill = hasFill;
    }

    /**
     * Returns the RGB integer representation of the fill color
     * @return fillColor is an integer
     */
    public int getFillColor() {
        return fillColor;
    }

    /**
     * Sets the fill color
     * @param fillColor is an RGB integer
     */
    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * Sets the drawing mode
     * @param m is a MODE that must be one of the following:
     * 			FREEHAND, ERASE, LINE, RECTANGLE
     */
    public void setMode(MODE m) {

        removeMouseListener(activeController);
        removeMouseMotionListener(activeController);

        editorMode = m;
        Tool currentTool = mTools.get(m.ordinal());
        activeController = currentTool.getController();

        addMouseListener(activeController);
        addMouseMotionListener(activeController);
    }

    /**
     * Make the drawing buffer and draws a starting content for it.
     */
    private void makeDrawingBuffer() {
        drawingBuffer = createImage(getWidth(), getHeight());
        clearScreen();
    }

    /**
     * Make the drawing buffer entirely white, essentially "clearing" the screen
     */
    public void clearScreen() {
        final Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        this.repaint();
    }

    /**
     * Returns the image that we are drawing on
     * @return drawingBuffer is an Image
     */
    public Image getDrawingBuffer() {
        return drawingBuffer;
    }

    /**
     * Returns the drawing mode: FREEHAND, ERASE, LINE, RECTANGLE
     * @return editorMode is one of the four options of MODE
     */
    public MODE getMode() {
        return editorMode;
    }

    /**
     * Sets the shape of the graphics to be drawn
     * @param surfaceShape is a graphics2D primitive shape
     */
    public void setSurfaceShape(Shape surfaceShape) {
        this.surfaceShape = surfaceShape;
    }

    /**
     * Sets the size of the brush for erase or freehand,
     * or sets the thickness of the line or the border of a rectangle
     * @param b is a nonnegative integer
     */
    public void setBrushSize(int b) {
        brushSize = b;
    }

    /**
     * Returns the size or thickness of the brush
     * @return brushSize is an integer
     */
    public int getBrushSize() {
        return brushSize;
    }

    /**
     * Returns the color of the brush
     * @return brushColor is an RGB integer representation of a color
     */
    public int getBrushColor() {
        return brushColor;
    }

    /**
     * Sets the color of the brush
     * @param brushColor is an RGB integer representation of a color
     */
    public void setBrushColor(int brushColor) {
        this.brushColor = brushColor;
    }

    /**
     * Displays the username when the user draws
     * @param uuid is a UUID String representation of the user
     * @return view is an instance of UserTrackerView
     */
    public UserTrackerView getUserTracker(String uuid) {
        if (userTrackers.containsKey(uuid)) {
            return userTrackers.get(uuid);
        }

        else {
            UserListModel users = GUI.getActiveUsers();
            User user = users.getUser(uuid);
            UserTrackerView view = new UserTrackerView(this, user.getName());
            userTrackers.put(uuid, view);
            return view;
        }
    }
}
