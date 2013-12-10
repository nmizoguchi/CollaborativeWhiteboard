package client.gui.canvas;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * UserTrackerView is a label that displays the username of the user that is drawing.
 * This label has a foreground color and a shadow color.
 * It contains a timer that allows it to fade away after the user stops drawing.
 * @author rcha
 *
 */
public class UserTrackerView {

    private Canvas canvas;
    private int x, y;
    private Color color;
    private Color shadowColor;
    private int alpha;
    private final String username;
    private javax.swing.Timer timer;

    /**
     * Initializes UserTrackerView, taking in a canvas and a username
     * Also sets the timer of the label
     * @param currentCanvas is a Canvas that is associated to the user
     * @param username is the String representation of the User that is drawing
     */
    public UserTrackerView(Canvas currentCanvas, String username) {
        this.canvas = currentCanvas;
        this.username = username;
        this.shadowColor = Color.BLACK;
        this.color = new Color((int) (Math.random()*Integer.MAX_VALUE)).brighter();
        this.alpha = 255;
        
        timer = new javax.swing.Timer(20, new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                
                color = new Color(color.getRed(), color.getGreen(),
                        color.getBlue(), alpha);
                
                shadowColor = new Color(shadowColor.getRed(), shadowColor.getGreen(),
                        shadowColor.getBlue(), alpha);
                
                canvas.repaint();
                
                if (alpha == 0)
                    timer.stop();
                alpha -= 5;
            }
        });
    }

    /**
     * Starts the timer
     */
    public void setTimer() {
        this.alpha = 255;
        timer.start();
    }
    
    /**
     * Returns the username associated with this UserTrackerView
     * @return username is a non-null String
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns the x coordinate of the UserTrackerView
     * @return x is an integer
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the x coordinate of the UserTrackerView
     * @param x is an integer
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Returns the y coordinate of the UserTrackerView
     * @return y is an integer
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the y coordinate of the UserTrackerView
     * @param y is an integer
     */
    public void setY(int y) {
        this.y = y;
    }
    
    /**
     * Returns the color of the UserTrackerView
     * @return color is an instance of Color
     */
    public Color getColor() {
        return this.color;
    }
    
    /**
     * Returns the shadow color of UserTrackerView
     * @return shadowColor is an instance of Color
     */
    public Color getShadowColor() {
        return this.shadowColor;
    }
}
