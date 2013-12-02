package client;

import client.gui.WhiteboardGUI;

public class RunnableChat implements Runnable {

    private final WhiteboardGUI gui;
    private final String arguments;
    
    public RunnableChat(WhiteboardGUI gui, String arguments) {
        this.gui = gui;
        this.arguments = arguments;
    }
    
    @Override
    public void run() {
    	gui.addChatMessage(arguments);
    }

}
