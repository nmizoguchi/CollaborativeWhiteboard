package client.gui;

import java.util.Enumeration;
import java.util.NoSuchElementException;

import javax.swing.DefaultListModel;

import shared.models.User;

/**
 * This class inherits from DefaultListModel<Obejct> and contains
 * the list of usernames of currently active users in the server.
 * 
 * @author rcha
 *
 */
public class UserListModel extends DefaultListModel<Object> {
    
	private static final long serialVersionUID = 1L;

	@Override
    public Object getElementAt(int index) {
        User current = (User) super.getElementAt(index);
        return current.getName();
    }
    
	/**
	 * Returns the user from this list that is associated with the UID
	 * @param uid is a String that identifies a user
	 * @return user
	 * @throws NoSuchElementException when the UID is not found in the list
	 */
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
