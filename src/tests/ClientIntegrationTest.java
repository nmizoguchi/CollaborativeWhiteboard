package tests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import client.ClientApplication;
import client.ClientListener;
import Protocol.CWPMessage;

public class ClientIntegrationTest {

    private static ClientApplication client;

    @Before
    public void initialize() throws IOException {
        ClientIntegrationUtil.start();
        client = ClientIntegrationUtil.getClient();
    }

    @Test(timeout = 10000)
    public void integrationTest_messageListeners() throws InterruptedException {
        /*
         * This integration test creates a mock server, so we can test the
         * client response to messages. We simulate the server behavior and test
         * the desired behavior from the client.
         */

        // Avoid race where we try to connect to server too early
        Thread.sleep(100);

        final List<CWPMessage> outputs = new ArrayList<CWPMessage>();
        final List<CWPMessage> inputs = new ArrayList<CWPMessage>();

        client.initialize(new ClientListener() {

            @Override
            public void onWhiteboardsMessageReceived(CWPMessage message) {
                outputs.add(message);
            }

            @Override
            public void onReceiveUpdatedUsersOnBoard(CWPMessage message) {
                outputs.add(message);
            }

            @Override
            public void onPaintMessageReceived(CWPMessage message) {
                outputs.add(message);
            }

            @Override
            public void onNewuserMessageReceived(CWPMessage message) {
                System.out.println(message.toString());
                outputs.add(message);
            }

            @Override
            public void onInvalidMessageReceived(String message) {
                outputs.add(new CWPMessage(CWPMessage.Encode(client.getUser(),
                        "chat", new String[] { "Invalid!" })));
            }

            @Override
            public void onDisconnecteduserMessageReceived(CWPMessage message) {
                outputs.add(message);
            }

            @Override
            public void onChatMessageReceived(CWPMessage message) {
                outputs.add(message);
            }

            @Override
            public void onChangeboardMessageReceived(CWPMessage message) {
                outputs.add(message);
            }
        }, "Nick");

        inputs.add(new CWPMessage(CWPMessage.Encode(client.getUser(),
                "whiteboards", new String[] {})));

        inputs.add(new CWPMessage(CWPMessage.Encode(client.getUser(),
                "updateusers", new String[] {
                        client.getUser().getUid().toString(),
                        client.getUser().getName() })));

        inputs.add(new CWPMessage(
                CWPMessage.Encode(client.getUser(), "drawline", new String[] {
                        "10", "20", "30", "40", "50", "60" })));

        inputs.add(new CWPMessage(CWPMessage.Encode(client.getUser(),
                "newuser", new String[] { client.getUser().getUid().toString(),
                        client.getUser().getName() })));

        inputs.add(new CWPMessage(CWPMessage.Encode(client.getUser(),
                "disconnecteduser", new String[] {
                        client.getUser().getUid().toString(),
                        client.getUser().getName() })));

        inputs.add(new CWPMessage(CWPMessage.Encode(client.getUser(), "chat",
                new String[] { "This is a chat message! :D" })));

        inputs.add(new CWPMessage(CWPMessage.Encode(client.getUser(),
                "changeboard", new String[] { "new board" })));

        for (CWPMessage msg : inputs) {
            ClientIntegrationUtil.send(msg.toString());
        }

        // Sends an error message to the client, and add an input that will be
        // added to the output list when the callback is called.
        inputs.add(new CWPMessage(CWPMessage.Encode(client.getUser(), "chat",
                new String[] { "Invalid!" })));
        ClientIntegrationUtil
                .send("A message that is not part of the Protocol!");

        // Let the messages propagate through the listeners
        Thread.sleep(1000);

        // Verifies if the messages were listened by the client, and all the
        // callback methods were called.
        assertTrue(inputs.containsAll(outputs));
        assertTrue(outputs.containsAll(inputs));
    }
}
