package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A model of a whiteboard. It represents both clientside and serverside models.
 * Each instance of Whiteboard has its own name and version. The drawing in a
 * whiteboard is simply a collection of actions, defined by our protocol.
 * 
 * @author Nicholas M. Mizoguchi
 * 
 */
public class Whiteboard {

    /*
     * Rep. Invariant: - The version has the same value of the number of
     * actions.
     */

    private String name;
    protected List<String> actions;

    /**
     * Constructor method.
     * 
     * @param name
     *            The name of the Whiteboard.
     */
    public Whiteboard(String name) {
        this.name = name;
        actions = Collections.synchronizedList(new ArrayList<String>());
    }

    /**
     * @return the Name of the Whiteboard
     */
    public String getName() {
        return name;
    }

    /**
     * @return The current version of the Whiteboard
     */
    public int getVersion() {
        return actions.size();
    }

    /**
     * Gets a specific action inside this Whiteboard represented by id. For
     * instance, if the client requests for the action with ID = 0, it will
     * return the first action performed in this whiteboard.
     * 
     * @param id
     *            The position in which the action was executed. id is
     *            non-negative and less than the version number
     * @return The id-th action performed in this Whiteboard
     */
    public String getAction(int id) {

        return actions.get(id);
    }

    /**
     * Updates this Whiteboard with the following action. This method is thread
     * safe since we are using a thread safe datatype, and the invariant don't
     * break at any moment.
     * 
     * @param action
     *            a Valid action defined by the project protocol.
     */
    public void update(String action) {
        actions.add(action);
    }

    /**
     * Gives the client a list of all the actions that are new compared to its
     * version. This method is thread-safe since it requests the object lock
     * (monitor pattern idea, but only for this method), and the rep. invariant
     * is valid at all times.
     * 
     * @param clientVersion
     *            the version of the Client requesting actions to update itself
     * @return a list of all actions (in correct order) that are not yet in the
     *         client version of the model
     */
//    public synchronized List<String> getActionsToUpdate(int clientVersion) {
//
//        /*
//         * This method has to be threadsafe so we can use it to improve
//         * performance in the ClientHandler
//         */
//        List<String> updates = new ArrayList<String>();
//
//        if (clientVersion < getVersion()) {
//
//            //TODO: This is not threadsafe!
//            updates = new ArrayList<String>(actions.subList(clientVersion,
//                    getVersion()));
//
//            assert (getVersion() == clientVersion + updates.size());
//        }
//
//        return updates;
//    }
}
