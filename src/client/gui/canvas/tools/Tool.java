package client.gui.canvas.tools;

public interface Tool {
    
	/**
	 * Returns the name of the tool
	 * @return String representation of the tool name
	 */
    public String getName();
    
    /**
     * Returns the controller this tool is associated with
     * @return ToolController
     */
    public ToolController getController();
}
