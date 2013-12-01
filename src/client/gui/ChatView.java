package client.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ChatView extends JPanel {
    private final JTable chatTable;
    private final DefaultTableModel chatModel;
    private final JScrollPane chatScroller;
    
    public ChatView() {
        chatModel = new DefaultTableModel();
        chatModel.addColumn("username");
        chatModel.addColumn("message");
        chatTable = new JTable(chatModel);
        chatScroller = new JScrollPane(chatTable);
        
        this.setLayout(new BorderLayout());
        this.add(chatScroller);
    }
    
    public void addMessage(String user, String message) {
        chatModel.addRow(new String[] {user,message});
    }
}
