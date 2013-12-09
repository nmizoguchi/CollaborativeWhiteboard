package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import shared.models.User;
import Protocol.CWPMessage;
import client.ClientApplication;
import client.ClientListener;
import client.gui.canvas.Canvas;
import client.gui.canvas.CanvasChangeWhiteboard;
import client.gui.canvas.CanvasPainter;

public class WhiteboardGUI extends JFrame implements ClientListener {
    /**
	 * This is the display portion that a user directly interacts with.
	 * It contains a tool menu on the west side, a canvas in the center and a list of online users and boards
	 * and a chat box on the east side. It also has a File menu at the north. This display updates when the user
	 * makes an action, by receiving messages from the server and calling different runnable classes accordingly.
	 * 
	 * @author rcha
	 */
    private static final long serialVersionUID = 1L;

    private ClientApplication client;
    private final UserListModel activeUsers;
    private final WhiteboardListModel activeWhiteboards;
    private final Canvas canvas;
    private final JScrollPane canvasPane;
    private final MenuWest buttonsMenu;
    private final MenuEast menuEast;
    private final JMenuBar menuBar;
    

    /**
     * Initializes and sets up the display.
     * @param client is a ClientApplication
     */
    public WhiteboardGUI(ClientApplication client) {
        
        this.client = client;
        activeUsers = new UserListModel();
        activeWhiteboards = new WhiteboardListModel();
        
        
        /********** Initialize attributes **********/
        // creates eraser and drawLine buttons with icons
        canvas = new Canvas(800, 600, this);
        
        // this panel is the part that is not drawable
        JPanel panel = new JPanel();
        panel.add(canvas, BorderLayout.CENTER);
        
        canvasPane = new JScrollPane(panel);
        canvasPane.setBackground(Color.gray);
        
        menuEast = new MenuEast(this);
        menuBar = new MenuNorth(this);
        buttonsMenu = new MenuWest(canvas);
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBounds(new Rectangle(1000, 500));
        this.add(menuBar, BorderLayout.NORTH);
        this.add(canvasPane, BorderLayout.CENTER);
        this.add(buttonsMenu, BorderLayout.WEST);
        this.add(menuEast, BorderLayout.EAST);
//        this.setLayout(layout);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
    }
    
    /**
     * Returns a list of active users
     * @return activeUsers is a UserListModel
     */
    public UserListModel getActiveUsers() {
        return activeUsers;
    }

    /**
     * Returns a list of the names of current whiteboards
     * @return activeWhiteboards is a WhiteboardListModel
     */
    public WhiteboardListModel getActiveWhiteboards() {
        return activeWhiteboards;
    }
    
    /**
     * Appends a line of user-input text in the chat area pane
     * @param message is a String that the user inputs in the chatbox
     */
    public void addChatMessage(String message) {
        menuEast.addMessage(message);
    }

    /**
     * Returns the client that is associated with the display
     * @return client is a ClientApplication
     */
    public ClientApplication getClient() {
        return client;
    }

    @Override
    public void onNewuserMessageReceived(CWPMessage message) {
        
        User user = new User(message.getArgument(0),
                message.getArgument(1));
        SwingUtilities.invokeLater(new RunnableNewuser(activeUsers,
                user));
        SwingUtilities.invokeLater(new RunnableChat(this, user.getName() + " has entered the server"));
    }

    @Override
    public void onDisconnecteduserMessageReceived(CWPMessage message) {
        
        User user = new User(message.getArgument(0),
                message.getArgument(1));
        
        SwingUtilities.invokeLater(new RunnableDisconnecteduser(
                activeUsers, user));
        SwingUtilities.invokeLater(new RunnableChat(this, user.getName() + " has disconnected from the server"));
    }

    @Override
    public void onWhiteboardsMessageReceived(CWPMessage message) {
        List<String> activeBoardNames = new ArrayList<String>();
        
        for (int i = 0; i < message.getArgumentsSize(); i++) {
            activeBoardNames.add(message.getArgument(i));
        }
        
        SwingUtilities.invokeLater(new CanvasChangeWhiteboard(
                activeWhiteboards, activeBoardNames));
        
    }

    @Override
    public void onChangeboardMessageReceived(CWPMessage message) {
        SwingUtilities.invokeLater(new RunnableChangeboard(canvas));
    }

    @Override
    public void onChatMessageReceived(CWPMessage message) {
    	StringBuilder chatMessage = new StringBuilder();
    	for (String m : message.getArguments()){
    		chatMessage.append(m);
    	}
        SwingUtilities.invokeLater(new RunnableChat(this, chatMessage.toString()));
    }

    @Override
    public void onPaintMessageReceived(CWPMessage message) {
        SwingUtilities.invokeLater(new CanvasPainter(canvas, message));
    }

    @Override
    public void onInvalidMessageReceived(CWPMessage message) {
        // TODO Auto-generated method stub
        
    }
}
