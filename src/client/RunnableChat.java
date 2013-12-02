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
//        String[] tokens = arguments.split(":");
//        String user = tokens[0];
//        StringBuilder message = new StringBuilder();
//        for(int i = 1; i < tokens.length; i++)
//            message.append(" ").append(tokens[i]);
//        gui.addChatMessage(user, message.toString().trim());

    }

}
