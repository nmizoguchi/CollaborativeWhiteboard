package client;

import javax.swing.DefaultListModel;

public class OnlineUserListModel extends DefaultListModel<Object> {
    
    @Override
    public Object getElementAt(int index) {
        OnlineUser current = (OnlineUser) super.getElementAt(index);
        return current.getName();
    }
}
