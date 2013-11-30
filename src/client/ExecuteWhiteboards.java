package client;

import java.util.List;

public class ExecuteWhiteboards implements Runnable {
    
    private final List<String> names;
    private final WhiteboardListModel whiteboards;
    
    public ExecuteWhiteboards(WhiteboardListModel whiteboards, List<String> names) {
        this.names = names;
        this.whiteboards = whiteboards;
    }

    @Override
    public void run() {
        for(String name : names) {
            if( !whiteboards.contains(name) ) {
                whiteboards.addElement(name);
            }
        }
    }
}
