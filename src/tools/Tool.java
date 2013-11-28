package tools;

public interface Tool {
    
    public String getName();
    public void executeAction(String action);
    public CanvasController getController();
}
