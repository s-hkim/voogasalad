package network.core.client;

import java.io.IOException;

import network.util.ThreadFactory;
import network.util.ConnectionType;

/**
 * @author Austin Liu (abl17) and Chris Streiffer (cds33)
 *
 */

public class ClientAdapter {
	
	private NetworkClient myClient;
	private volatile boolean isConnected;
	
	private static final ClientAdapter myAdapter = new ClientAdapter();
	
	private ClientAdapter () {
		// TODOs
	}
	
	public static ClientAdapter getInstance () {
		return myAdapter;
	}
	
	private class NetworkClient extends Client {

		private static final int DEFAULT_PORT = 5959;
		
		NetworkClient(String host) throws IOException {
			super(host, DEFAULT_PORT);
		}

		protected void messageReceived(Object message) {
			Runnable myRunnable =  new Runnable() { public void run() {}};
			ThreadFactory.execute(myRunnable, ConnectionType.JAVAFX);
		}
	
	}
}