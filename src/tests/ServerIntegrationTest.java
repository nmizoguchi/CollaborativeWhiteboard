package tests;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import server.ServerApplication;

public class ServerIntegrationTest {

	private ServerApplication server;

	@Before
	public void initializeServer() {
		server = new ServerApplication("Server Name");
		server.start(4444);
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
