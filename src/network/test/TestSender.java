package network.test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import authoring.model.game.Game;
import network.deprecated.DataDecorator;
import network.deprecated.Mail;
import network.deprecated.RequestType;

/**
 * @author Austin Liu (abl17) and Chris Streiffer (cds33)
 *
 */

public class TestSender {
	
	private static final String ipaddress = "52.20.247.225";
	//private static final String ipaddress = "localhost";
	private static final Integer port = 5055;
	
	public static void main(String...args) {
		try {
			Socket connection = new Socket(ipaddress, port);
			System.out.println("Sender just connected to: " + connection.getLocalAddress());
			
			ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
			
			Game game = new Game();			
			Mail toSend = new DataDecorator(RequestType.ADD, game, null);
			
			out.writeObject(toSend);
			out.flush();
			
			out.writeObject(new DataDecorator(RequestType.DISCONNECT, null, null));
			out.flush();
			
			out.close();
			in.close();
			connection.close();
			
		} catch(Exception e) {}		
	}
}
