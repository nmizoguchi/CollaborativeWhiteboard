package client.gui;

import client.gui.canvas.Canvas;


public class RunnableChangeboard implements Runnable {
    
    private final String boardName;
    private final Canvas canvas;
    
    public RunnableChangeboard(Canvas canvas, String boardName) {
        this.canvas = canvas;
        this.boardName = boardName;
    }

    @Override
    public void run() {
        canvas.clearScreen();
    }
}
