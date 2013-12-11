package tests.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import server.ServerApplication;
import shared.models.User;

public class ServerIntegrationUtil {

    private static final int port = 4444;
    private static final ServerApplication server = new ServerApplication("server name");

    public static void startServer() {
        server.start(port);
    }

    public static Socket connect() throws IOException {
        Socket ret = null;
        final int MAX_ATTEMPTS = 50;
        int attempts = 0;
        do {
            try {
                ret = new Socket("127.0.0.1", port);
            } catch (ConnectException ce) {
                try {
                    if (++attempts > MAX_ATTEMPTS)
                        throw new IOException(
                                "Exceeded max connection attempts", ce);
                    Thread.sleep(300);
                } catch (InterruptedException ie) {
                    throw new IOException("Unexpected InterruptedException", ie);
                }
            }
        } while (ret == null);
        ret.setSoTimeout(3000);
        return ret;
    }

    public static boolean eqNoSpace(String s1, String s2) {
        return s1.replaceAll("\\s+", "").equals(s2.replaceAll("\\s+", ""));
    }

    public static String nextNonEmptyLine(BufferedReader in) throws IOException {
        while (true) {
            String ret = in.readLine();
            if (ret == null || !ret.equals(""))
                return ret;
        }
    }
    
    public static User getServerUser() {
        return server.getServerUser();
    }
}
