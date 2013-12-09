package client.gui;

import java.util.Enumeration;
import java.util.NoSuchElementException;

import javax.swing.DefaultListModel;

import shared.models.User;

public class UserListModel extends DefaultListModel<Object> {
    
    @Override
    public Object getElementAt(int index) {
        User current = (User) super.getElementAt(index);
        return current.getName();
    }
    
    public User getUser(String uid) {
        Enumeration<Object> enumeration = elements();
        while(enumeration.hasMoreElements()){
            User user = (User)enumeration.nextElement();
            if(uid.equals(user.getUid().toString())) {
                return user;
            }
        }
        throw new NoSuchElementException();
    }
}
