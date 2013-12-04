package client.gui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import shared.models.User;
import Protocol.Protocol;
import client.ClientApplication;
import client.ClientListener;
import client.gui.canvas.Canvas;
import client.gui.canvas.CanvasChangeWhiteboard;
import client.gui.canvas.CanvasPainter;

public class WhiteboardGUI extends JFrame implements ClientListener {
    /**
	 * This is the display portion that a user directly interacts with.
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
    

    public WhiteboardGUI(ClientApplication client) {
        
        this.client = client;
        activeUsers = new UserListModel();
        activeWhiteboards = new WhiteboardListModel();
        
        
        /********** Initialize attributes **********/
        // creates eraser and drawLine buttons with icons
        canvas = new Canvas(800, 600, client);
        canvasPane = new JScrollPane(canvas);
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
    
    public UserListModel getActiveUsers() {
        return activeUsers;
    }

    public WhiteboardListModel getActiveWhiteboards() {
        return activeWhiteboards;
    }
    
    public void addChatMessage(String message) {
        menuEast.addMessage(message);
    }

    public ClientApplication getClient() {
        return client;
    }

    @Override
    public void onNewuserMessageReceived(Protocol message) {
        
        User user = new User(message.getArgument(0),
                message.getArgument(1));
        SwingUtilities.invokeLater(new RunnableNewuser(activeUsers,
                user));
        SwingUtilities.invokeLater(new RunnableChat(this, " has entered the server"));
    }

    @Override
    public void onDisconnecteduserMessageReceived(Protocol message) {
        
        User user = new User(message.getArgument(0),
                message.getArgument(1));
        
        SwingUtilities.invokeLater(new RunnableDisconnecteduser(
                activeUsers, user));

        SwingUtilities.invokeLater(new RunnableChat(this, " has disconnected from the server"));
    }

    @Override
    public void onWhiteboardsMessageReceived(Protocol message) {
        List<String> activeBoardNames = new ArrayList<String>();
        
        for (int i = 0; i < message.getArgumentsSize(); i++) {
            activeBoardNames.add(message.getArgument(i));
        }
        
        SwingUtilities.invokeLater(new CanvasChangeWhiteboard(
                activeWhiteboards, activeBoardNames));
        
    }

    @Override
    public void onChangeboardMessageReceived(Protocol message) {
        SwingUtilities.invokeLater(new RunnableChangeboard(canvas, message.getArgument(0)));
        
    }

    @Override
    public void onChatMessageReceived(Protocol message) {
        SwingUtilities.invokeLater(new RunnableChat(this, message
                .getArguments()));
    }

    @Override
    public void onPaintMessageReceived(Protocol message) {
        String action = message.getPaintAction();
        SwingUtilities.invokeLater(new CanvasPainter(canvas, action));
    }

    @Override
    public void onInvalidMessageReceived(Protocol message) {
        // TODO Auto-generated method stub
        
    }
}
