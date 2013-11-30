package Protocol;

public class Protocol {

    /**
     * Checks if the input is a valid input of the protocol.
     * 
     * @param input
     *            The command desired.
     * @return message to client
     */
    public static String[] CheckAndFormat(String input) {

        // Commands sent and received both sides:
        // drawline x1 y1 x2 y2 color brushSize
        // erase x1 y1 x2 y2 brushSize
        // drawrect x1 y1 x2 y2 brushColor brushSize fillColor hasFill
        // changeboard boardName
        // cleanboard boardName
        // newuser username
        // updateuser oldUsername newUsername
        //
        // Commands sent from the client (to the server):
        //
        // initialize username
        // 
        // Commands sent from the server (to the client):
        // whiteboards name1 name2 name3 ...
        // disconnecteduser username
        String regex = ""
                + "(drawline -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+)|"
                + "(erase -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+)|"
                + "(drawrect -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+)|"
                + "(changeboard [^\\s]+)|"
                + "(cleanboard [^\\s]+)|"
                + "(whiteboards( [^\\s]+)*)|"
                + "(initialize [^\\s]+)|"
                + "(newuser [^\\s]+)|"
                + "(updateuser [^\\s]+ [^\\s]+)|"
                + "(disconnecteduser [^\\s]+)|"
                + "(help)|(bye)";

        if (!input.matches(regex)) {
            // invalid input
            System.out.println(input);
            throw new UnsupportedOperationException();
        }

        String[] tokens = input.split(" ");

        return tokens;
    }
}
