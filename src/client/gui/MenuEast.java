package client.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Protocol.Protocol;
import client.UserListModel;
import client.WhiteboardListModel;

public class MenuEast extends JPanel {

    private final WhiteboardGUI gui;

    private final JList onlineUserList;
    private final JList whiteboardsList;
    private final JScrollPane onlineUserScroller;
    private final JScrollPane whiteboardsScroller;
    private final UserListModel activeUsersData;
    private final WhiteboardListModel activeWhiteboardsData;
    private final JButton createBoardButton;
    private final ChatView chatView;
    
    public MenuEast(final WhiteboardGUI userInterface) {

        this.gui = userInterface;
        
        chatView = new ChatView();

        activeUsersData = gui.getClient().getActiveUsers();
        activeWhiteboardsData = gui.getClient().getActiveWhiteboards();

        onlineUserList = new JList(activeUsersData);
        whiteboardsList = new JList(activeWhiteboardsData);
        createBoardButton = new JButton("New Board");

        onlineUserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        onlineUserList.setLayoutOrientation(JList.VERTICAL);
        onlineUserList.setVisibleRowCount(-1);
        onlineUserScroller = new JScrollPane(onlineUserList);
//        onlineUserScroller.setPreferredSize(new Dimension(50, 80));

        whiteboardsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        whiteboardsList.setLayoutOrientation(JList.VERTICAL);
        whiteboardsList.setVisibleRowCount(-1);
        whiteboardsScroller = new JScrollPane(whiteboardsList);
//        whiteboardsScroller.setPreferredSize(new Dimension(50, 80));

        whiteboardsList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent le) {
                int idx = whiteboardsList.getSelectedIndex();
                if (idx != -1 && !le.getValueIsAdjusting()) {
                    String message = Protocol.CreateMessage(gui.getClient()
                            .getUser(), "changeboard", activeWhiteboardsData
                            .getElementAt(idx));
                    gui.getClient().send(message);
                }
            }
        });

        createBoardButton.addActionListener(new ActionListener() {

        	@Override
        	public void actionPerformed(ActionEvent e) {
        		try{
        			String name = JOptionPane.showInputDialog("Board name:");
        			//if user selects cancel or does not enter a name, nothing will happen and the dialog will close
        			if (name != null){
        				String message = Protocol.CreateMessage(gui.getClient()
        						.getUser(), "changeboard", name);
        				gui.getClient().send(message);
        			} 
        		}catch (NullPointerException n) {}

        	}
        });

        this.setLayout(new GridLayout(10, 1));
//        layout.addLayoutComponent("userlabel", new JLabel("Online users"));
//        layout.addLayoutComponent("users", onlineUserScroller);
//        layout.addLayoutComponent("boardlabel", whiteboardsScroller);
//        layout.addLayoutComponent("boardbutton", createBoardButton);
//        layout.addLayoutComponent("chat", new JScrollPane(chatView));
//        this.setLayout(layout);
//        this.add
        JScrollPane chatPane = new JScrollPane(chatView);
        this.add(new JLabel("Online users"));
        this.add(onlineUserScroller);
        this.add(new JLabel("Available Boards"));
        this.add(whiteboardsScroller);
        this.add(createBoardButton);
        this.add(chatPane);
        this.setPreferredSize(new Dimension(250, 80));
    }
    
    public void addMessage(String message) {
        chatView.addMessage(message);
    }
}
