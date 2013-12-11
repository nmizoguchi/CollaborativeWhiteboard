package client.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Protocol.CWPMessage;

/**
 * This panel is found on the east side of the GUI. It contains a list of users
 * currently connected to the server, a list of the whiteboards, a button that
 * creates new whiteboards, and a chat box. The chat box is updated whenever a
 * user types in a message, or connects/disconnects from the server.
 * 
 * @author rcha
 * 
 */
public class MenuEast extends JPanel {

    private final GroupLayout layout;
    private final WhiteboardGUI gui;

    private final JList onlineUserList;
    private final JList whiteboardsList;
    private final JScrollPane onlineUserScroller;
    private final JScrollPane whiteboardsScroller;
    private final UserListModel activeUsersData;
    private final WhiteboardListModel activeWhiteboardsData;
    private final JButton createBoardButton;
    private final JTextArea chatArea;
    private final JScrollPane chatAreaPane;
    private final JTextField userChat;
    private final JLabel chatHereText;
    private final JLabel online;
    private final JLabel boardsAvail;

    public MenuEast(WhiteboardGUI userInterface) {
        layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        this.gui = userInterface;

        /******** Initialize Components ****************/
        // Initializes the chatbox-related components
        chatArea = new JTextArea();
        chatArea.setWrapStyleWord(true);
        chatArea.setLineWrap(true);
        chatArea.setEditable(false);
        chatAreaPane = new JScrollPane(chatArea);
        userChat = new JTextField();
        chatHereText = new JLabel("Chat Here: ");

        // Initializes the list of users and boards -related components
        activeUsersData = gui.getActiveUsers();
        activeWhiteboardsData = gui.getActiveWhiteboards();

        onlineUserList = new JList(activeUsersData);
        whiteboardsList = new JList(activeWhiteboardsData);
        createBoardButton = new JButton("New Board");
        createBoardButton
                .setSize(new Dimension(this.getWidth(), JButton.HEIGHT));

        onlineUserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        onlineUserList.setLayoutOrientation(JList.VERTICAL);
        onlineUserList.setVisibleRowCount(-1);
        onlineUserScroller = new JScrollPane(onlineUserList);
        // onlineUserScroller.setPreferredSize(new Dimension(50, 80));

        whiteboardsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        whiteboardsList.setLayoutOrientation(JList.VERTICAL);
        whiteboardsList.setVisibleRowCount(-1);
        whiteboardsScroller = new JScrollPane(whiteboardsList);
        // whiteboardsScroller.setPreferredSize(new Dimension(50, 80));

        online = new JLabel("Online Users");
        boardsAvail = new JLabel("Available Boards");

        /*
         * When the user selects a whiteboard in the list, the gui sends a
         * message to the server to switch boards
         */
        whiteboardsList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent le) {
                int idx = whiteboardsList.getSelectedIndex();
                if (idx != -1 && !le.getValueIsAdjusting()) {
                    String[] args = new String[] { activeWhiteboardsData
                            .getElementAt(idx) };
                    String message = CWPMessage.Encode(gui.getClient()
                            .getUser(), "changeboard", args);
                    gui.getClient().scheduleMessage(message);
                }
            }
        });

        /*
         * When the user selects the button to make a new board, a dialog
         * appears. If the user enters a board name, the gui sends a message to
         * the server to make a new board. If the user does not and selects
         * either OK or CANCEL, the dialog closes and nothing happens.
         */
        createBoardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog("Board name:");
                // if user selects cancel or does not enter a name, nothing
                // will happen and the dialog will close
                if (name != null && !name.equals("")) {
                    String[] params = new String[] { name };
                    String message = CWPMessage.Encode(gui.getClient()
                            .getUser(), "changeboard", params);

                    gui.getClient().scheduleMessage(message);
                }
            }
        });

        /*
         * When the user types a message in the chatbox, the gui sends a message
         * to the server to update the chatbox, adding a new row with text of
         * the form [username] + ": " + [typed message]
         */
        userChat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] args = new String[] { gui.getClient().getUser()
                        .getName()
                        + ": " + userChat.getText() };
                String message = CWPMessage.Encode(gui.getClient().getUser(),
                        "chat", args);
                gui.getClient().scheduleMessage(message);
                userChat.setText("");
            }
        });

        /**************** Layout ********************/
        ParallelGroup horizGroup = layout.createParallelGroup();
        horizGroup
                .addComponent(online)
                .addComponent(onlineUserScroller)
                .addComponent(boardsAvail)
                .addComponent(whiteboardsScroller)
                .addComponent(createBoardButton)
                .addComponent(chatAreaPane)
                .addGroup(
                        layout.createSequentialGroup()
                                .addComponent(chatHereText)
                                .addComponent(userChat));
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(
                horizGroup));

        ParallelGroup vertGroup = layout
                .createParallelGroup(GroupLayout.Alignment.BASELINE);
        vertGroup.addComponent(chatHereText).addComponent(userChat);
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(online).addComponent(onlineUserScroller)
                .addComponent(boardsAvail).addComponent(whiteboardsScroller)
                .addComponent(createBoardButton).addComponent(chatAreaPane)
                .addGroup(vertGroup));
        this.setLayout(layout);
        this.setPreferredSize(new Dimension(200, 600));
    }

    public void addMessage(String message) {
    	synchronized(chatArea){
    		chatArea.append(message + "\n");
    	}
    }

}
