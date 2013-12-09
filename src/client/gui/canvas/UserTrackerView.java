package client.gui.canvas;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserTrackerView {

    private Canvas canvas;
    private int x, y;
    private Color color;
    private Color shadowColor;
    private int alpha;
    private final String username;
    private javax.swing.Timer timer;

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

    public void setTimer() {
        this.alpha = 255;
        timer.start();
    }
    
    public String getUsername() {
        return this.username;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public Color getShadowColor() {
        return this.shadowColor;
    }
}
