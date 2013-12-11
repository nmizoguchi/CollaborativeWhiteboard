package tests;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.junit.Before;
import org.junit.Test;

import shared.models.User;
import Protocol.CWPMessage;

public class ServerIntegrationTest {

    private User user;

    @Before
    public void initializeServer() {
        user = new User("4b1fabfa-13d9-4b47-a6d2-a64518e0c85b", "Nick");
        ServerIntegrationUtil.startServer();
    }

    @Test(timeout = 10000)
    public void serverResponse_messages() throws InterruptedException,
            IOException {

        // Avoid race where we try to connect to server too early
        Thread.sleep(100);

        try {
            // Connects a client to the server.
            Socket sock = ServerIntegrationUtil.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    sock.getInputStream()));
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);

            /*----------
             * initialize: receives a list of updated users from the server
             * connected to the "Default" board.
             */
            out.println(CWPMessage.Encode(user, "initialize", new String[] {
                    user.getUid().toString(), user.getName() }));

            // Receives the updated list of the connected clients on the
            // "Default" board
            assertEquals(CWPMessage.Encode(
                    ServerIntegrationUtil.getServerUser(), "updateusers",
                    new String[] { user.getUid().toString(), user.getName() }),
                    ServerIntegrationUtil.nextNonEmptyLine(in));

            // The message of a newuser (this user) has connected to
            // the server
            assertEquals(CWPMessage.Encode(
                    ServerIntegrationUtil.getServerUser(), "newuser",
                    new String[] { user.getUid().toString(), user.getName() }),
                    ServerIntegrationUtil.nextNonEmptyLine(in));

            // Finally, the list of whiteboards available
            assertEquals(CWPMessage.Encode(
                    ServerIntegrationUtil.getServerUser(), "whiteboards", new String[] {"Default" }),
                    ServerIntegrationUtil.nextNonEmptyLine(in));

            /*----------
             * changeboard: receives the updated list of users in the new board.
             */
            out.println(CWPMessage.Encode(user, "changeboard",
                    new String[] { "new board" }));
            
            assertEquals(CWPMessage.Encode(
                    ServerIntegrationUtil.getServerUser(), "whiteboards",
                    new String[] { "Default", "new board" }),
                    ServerIntegrationUtil.nextNonEmptyLine(in));

            assertEquals(CWPMessage.Encode(
                    ServerIntegrationUtil.getServerUser(), "changeboard",
                    new String[] { "new board" }),
                    ServerIntegrationUtil.nextNonEmptyLine(in));
            
            assertEquals(CWPMessage.Encode(
                    ServerIntegrationUtil.getServerUser(), "updateusers",
                    new String[] { user.getUid().toString(), user.getName() }),
                    ServerIntegrationUtil.nextNonEmptyLine(in));
            
            /*----------
             * whiteboards: receives the updated list of available boards
             */
            out.println(CWPMessage.Encode(user, "whiteboards",
                    new String[] {}));
            
            assertEquals(CWPMessage.Encode(
                    ServerIntegrationUtil.getServerUser(), "whiteboards",
                    new String[] { "Default", "new board" }),
                    ServerIntegrationUtil.nextNonEmptyLine(in));
            
            /*----------
             * chat: receives the message sent
             */
            out.println(CWPMessage.Encode(user, "chat",
                    new String[] {"Hello! This is our chat test :D"}));
            
            assertEquals(CWPMessage.Encode(
                    ServerIntegrationUtil.getServerUser(), "chat",
                    new String[] { "Hello! This is our chat test :D" }),
                    ServerIntegrationUtil.nextNonEmptyLine(in));
            
            /*----------
             * erase action: receive the action back
             */
            out.println(CWPMessage.Encode(user, "erase",
                    new String[] {"10", "20", "30", "40", "50", "60"}));
            
            assertEquals(CWPMessage.Encode(user, "erase",
                    new String[] {"10", "20", "30", "40", "50", "60"}),
                    ServerIntegrationUtil.nextNonEmptyLine(in));
            
            /*----------
             * drawline action: receive the action back
             */
            out.println(CWPMessage.Encode(user, "drawline",
                    new String[] {"10", "20", "30", "40", "50", "60"}));
            
            assertEquals(CWPMessage.Encode(user, "drawline",
                    new String[] {"10", "20", "30", "40", "50", "60"}),
                    ServerIntegrationUtil.nextNonEmptyLine(in));
            
            /*----------
             * drawrect action: receive the action back
             */
            out.println(CWPMessage.Encode(user, "drawrect",
                    new String[] {"10", "20", "30", "40", "50", "60", "0", "70"}));
            
            assertEquals(CWPMessage.Encode(user, "drawrect",
                    new String[] {"10", "20", "30", "40", "50", "60", "0", "70"}),
                    ServerIntegrationUtil.nextNonEmptyLine(in));
            
            
            sock.close();

        } catch (SocketTimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
