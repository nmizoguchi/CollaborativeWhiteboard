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

import Protocol.Protocol;
import client.UserListModel;
import client.WhiteboardListModel;

public class MenuEast extends JPanel{

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
    
	public MenuEast(WhiteboardGUI userInterface) {
		layout = new GroupLayout(this);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		this.gui = userInterface;
        
		chatArea = new JTextArea();
		chatAreaPane = new JScrollPane(chatArea);
		userChat = new JTextField();
		chatHereText = new JLabel("Chat Here: ");
        

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

        JLabel online = new JLabel("Online Users");
        JLabel boardsAvail = new JLabel("Available Boards");
        
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

        userChat.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		String message = Protocol.CreateMessage(gui.getClient().getUser(),"chat", userChat.getText());
        		gui.getClient().send(message);
        		userChat.setText("");
        	}
        });
	
	
        /****************Layout********************/
        ParallelGroup horizGroup = layout.createParallelGroup();
        horizGroup.addComponent(online).addComponent(onlineUserScroller)
        		.addComponent(boardsAvail).addComponent(whiteboardsScroller)
        		.addComponent(createBoardButton).addComponent(chatAreaPane).addGroup(layout.createSequentialGroup()
        				.addComponent(chatHereText).addComponent(userChat));
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(horizGroup));
        
        ParallelGroup vertGroup = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
        vertGroup.addComponent(chatHereText).addComponent(userChat);
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(online).addComponent(onlineUserScroller)
        		.addComponent(boardsAvail).addComponent(whiteboardsScroller).addComponent(createBoardButton)
        		.addComponent(chatAreaPane).addGroup(vertGroup));
        this.setLayout(layout);
        this.setPreferredSize(new Dimension(200, 600));
	}
	//TODO: not thread-safe
	public void addMessage(String message) {
        chatArea.append(message + "\n");
    }

}
