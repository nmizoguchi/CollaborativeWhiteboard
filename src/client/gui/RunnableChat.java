package client.gui;


public class RunnableChat implements Runnable {

    private final WhiteboardGUI gui;
    private final String arguments;
    
    public RunnableChat(WhiteboardGUI gui, String arguments) {
        this.gui = gui;
        this.arguments = arguments;
    }
    
    @Override
    public void run() {
    	gui.addChatMessage(gui.getClient().getUser().getName() + arguments);
    }

}
