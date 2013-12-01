package client;

import javax.swing.DefaultListModel;

import shared.models.User;

public class UserListModel extends DefaultListModel<Object> {
    
    @Override
    public Object getElementAt(int index) {
        User current = (User) super.getElementAt(index);
        return current.getName();
    }
}
