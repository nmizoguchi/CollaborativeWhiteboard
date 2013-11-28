package client;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WhiteboardModel {
    
    private int version;
    private List<String> actions;
    
    public WhiteboardModel() {
        version = 0;
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
        List<String> updates;
        
        if(clientVersion < version) {
            updates = actions.subList(clientVersion+1, version);
            
            assert(version == clientVersion + updates.size());
            
            return updates;
        }
        
        return new ArrayList();
    }
}
