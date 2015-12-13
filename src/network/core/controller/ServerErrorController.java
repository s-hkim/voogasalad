package network.core.controller;

import network.core.connections.Connection;
import network.core.containers.NetworkContainer;
import network.core.messages.Message;
import network.core.messages.format.Request;

/**
 * @author Chris Streiffer (cds33) and Austin Liu (abl17)
 */

public class ServerErrorController {

	public static void sendErrorMessage(String client, NetworkContainer<Connection> clients, String error) {
		clients.getObject(client).send(new Message(error, Request.ERROR, null));
	}
	
	public static void sendErrorMessage(Connection client, String error) {
		client.send(new Message(error, Request.ERROR, null));
	}
}
