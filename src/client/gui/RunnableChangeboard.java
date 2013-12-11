package client.gui;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import client.gui.canvas.Canvas;

/**
 * Implements Runnable. When called, the user's canvas will become blank
 * @author rcha
 *
 */
public class RunnableChangeboard implements Runnable {
    
    private final Canvas canvas;
    
    
    /**
     * Takes in a canvas that is associated to the user
     * @param canvas
     */
    public RunnableChangeboard(Canvas canvas) {
        this.canvas = canvas;
    }

    /**
     * Clears the user's canvas
     */
    @Override
    public void run() {
    	canvas.clearScreen();
    }
}
