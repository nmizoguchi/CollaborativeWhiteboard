package Protocol;

public class Protocol {

    /**
     * Checks if the input is a valid input of the protocol.
     * 
     * @param input The command desired.
     * @return message to client
     */
    public static String[] CheckAndFormat(String input) {
        String regex = "(update -?\\d+)|"
                + "(drawline -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+)|"
                + "(erase -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+)|"
                + "(drawrect -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+ -?\\d+)|"
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
