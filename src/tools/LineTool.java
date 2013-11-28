package tools;

import canvas.Canvas;

public class LineTool implements Tool {
    
    public final String name = "Draw Line";
    public final Canvas canvas;
    
    public LineTool(Canvas canvas) {
        this.canvas = canvas;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void executeAction(String action) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public CanvasController getController() {
        // TODO Auto-generated method stub
        return null;
    }

}
