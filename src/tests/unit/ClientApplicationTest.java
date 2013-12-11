package tests.unit;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import client.ClientApplication;
import client.ClientListener;
import shared.models.User;
import tests.integration.ClientIntegrationUtil;
import Protocol.CWPMessage;

public class ClientApplicationTest {

    private ClientApplication client;

    @Before
    public void initialize() throws IOException {
        ClientIntegrationUtil.start(5555);
        client = ClientIntegrationUtil.getClient();
    }

    @Test
    public void getSetName_valid() {

        /*
         * Tests the initial value of the user and its name, and then tests
         * initialization, getters, and setters.
         */
        User uninitializedUser = client.getUser();
        // Initially, user has an empty string as a name, until it is
        // initialized.
        assertTrue("".equals(uninitializedUser.getName()));

        // Initialize client with an empty listener, since we won't be testing
        // this listener with unit testing. We set the user.
        client.initialize(new ClientListener() {

            @Override
            public void onWhiteboardsMessageReceived(CWPMessage message) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onReceiveUpdatedUsersOnBoard(CWPMessage message) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPaintMessageReceived(CWPMessage message) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onNewuserMessageReceived(CWPMessage message) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onInvalidMessageReceived(String invalidMessage) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDisconnecteduserMessageReceived(CWPMessage message) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onChatMessageReceived(CWPMessage message) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onChangeboardMessageReceived(CWPMessage message) {
                // TODO Auto-generated method stub

            }
        }, "Desired name");

        User initializedUser = client.getUser();

        // Verifies if the name was set correctly
        assertTrue("Desired name".equals(initializedUser.getName()));

        // The object must be the same, in other words, the UUID shouldn't
        // change.
        assertTrue(initializedUser.equals(uninitializedUser));

        client.setUserName("setted name");
        User changedNameUser = client.getUser();

        // Verifies if the name was set correctly
        assertTrue("setted name".equals(changedNameUser.getName()));

        // The object must be the same, in other words, the UUID shouldn't
        // change.
        assertTrue(initializedUser.equals(changedNameUser));
    }

}
