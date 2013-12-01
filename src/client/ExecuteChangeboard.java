package client;

import client.gui.canvas.Canvas;


public class ExecuteChangeboard implements Runnable {
    
    private final String boardName;
    private final Canvas canvas;
    
    public ExecuteChangeboard(Canvas canvas, String boardName) {
        this.canvas = canvas;
        this.boardName = boardName;
    }

    @Override
    public void run() {
        canvas.clearScreen();
        canvas.mClient.changeWhiteboard(boardName);
    }
}
