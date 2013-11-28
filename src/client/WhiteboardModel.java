package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WhiteboardModel {

    private List<String> actions;
    
    public WhiteboardModel() {
        actions = Collections.synchronizedList(new ArrayList<String>());
    }
    
    public int getVersion() {
        return actions.size();
    }
    
    public String getAction(int id) {
        return actions.get(id);
    }
    
    public synchronized void update(String action) {
        
        actions.add(action);
        assert(actions.size() == getVersion());
    }
    
    public synchronized void update(List<String> newActions) {
        
        actions.addAll(newActions);
        assert(actions.size() == getVersion());
    }
    
    /**
     * Server actions. We could subclass it!
     * @param clientVersion
     * @return
     */
    public synchronized List<String> getActionsToUpdate(int clientVersion) {
        
        List<String> updates = new ArrayList<String>();
        
        if(clientVersion < getVersion()) {
            
            updates = new ArrayList<String>(actions.subList(clientVersion, getVersion()));
            
            assert(getVersion() == clientVersion + updates.size());
        }
        
        return updates;
    }
}
