package client.gui;


public class RunnableChat implements Runnable {

    private final WhiteboardGUI gui;
    private final String message;
    
    public RunnableChat(WhiteboardGUI gui, String message) {
        this.gui = gui;
        this.message = message;
    }
    
    @Override
    public void run() {
    	gui.addChatMessage(message);
    }

}
