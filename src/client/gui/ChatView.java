package client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;
import javax.swing.table.DefaultTableModel;

public class ChatView extends JTextArea {
	private final static String newline = "\n";
//	private final JTextArea chatArea;
	
	public ChatView(){
//		chatArea = new JTextArea();
		this.setLineWrap(true);
		this.setLineWrap(true);
//		this.setLayout(new ScrollPaneLayout());
//		this.add(chatArea);
//		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
	}
	
	public void addMessage(String message){
		this.append(message + newline);
	}
//public class ChatView extends JPanel {
//    private final JTable chatTable;
//    private final DefaultTableModel chatModel;
//    private final JScrollPane chatScroller;
//    
//    public ChatView() {
//        chatModel = new DefaultTableModel();
//        chatModel.addColumn("message");
//        chatTable = new JTable(chatModel);
////        chatTable.setAutoscrolls(true);
//        chatTable.setShowGrid(false);
//        chatScroller = new JScrollPane(chatTable);
//        
//        this.setLayout(new BorderLayout());
////        this.setModel(chatModel);
////        this.setLayout(new ScrollPaneLayout());
//        chatScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
//        chatTable.setFillsViewportHeight(true);
//        chatScroller.set
//        this.add(chatScroller);
//        
//        this.setPreferredSize(new Dimension(200, 50));
//    }
//    
//    public void addMessage(String user, String message) {
//    	chatModel.addRow(new Object[] {user + " " + message});
//    }
}
