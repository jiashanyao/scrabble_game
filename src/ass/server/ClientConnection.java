package ass.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import ass.communication.ClientMessage;
import ass.communication.JsonUtility;
import ass.communication.ServerMessage;

public class ClientConnection extends Thread{

	private Socket clientSocket;

	private Server server;

	private Game game;
	
	private String clientId;
	
	private BufferedReader reader;
	
	private BufferedWriter writer;
	
	public ClientConnection(Socket socket, Server server) {
		clientSocket = socket;
		this.server = server;
		game = null;
		try {
			reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
			writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
		} catch (IOException e) {
			System.out.println(
					"Client " + clientSocket.getInetAddress().getHostAddress() + ": Connection interrupted!");
		}
	}
	
	@Override
	public void run() {
		Thread read = new Thread() { // thread for reading requests
			@Override
			public void run() {
				String input = null;
				try {
					while ((input = reader.readLine()) != null) {
						ClientMessage clientMessage = JsonUtility.fromJson(input, ClientMessage.class);
						processRequest(clientMessage);
						//TODO make a message queue
					}
				} catch (IOException e) {
					System.out.println(
							"Client " + clientSocket.getInetAddress().getHostAddress() + ": Connection interrupted!");
				}
			}
		};
		read.start();
	}

	private void processRequest(ClientMessage cm) {
		ServerMessage sm = new ServerMessage();
		switch(cm.getType()) {
		case SYNC:
			if (server.getClients().containsKey(cm.getUserId())){
				sm.setType(ServerMessage.Type.REQUEST);
				sm.setMessage(Dictionary.ID_DUP);
			} else {
				clientId = cm.getUserId();
				server.getClients().put(clientId, this);
				sm.setType(ServerMessage.Type.INFORMATION);
				sm.setMessage(Dictionary.ID_OK);
			}
			break;
		case INVITATION:
			game = new Game(this);
			String[] invitedUsers = cm.getInvitations();
			for (String user : invitedUsers) {
				ClientConnection cc = server.getClients().get(user);
				game.addPlayer(cc);
			}
			
		default:
		}
		
	}
	
	public String getClientId() {
		return clientId;
	}

	public void invitedBy(String host) {
		ServerMessage sm = new ServerMessage();
		sm.setType(ServerMessage.Type.REQUEST);
		sm.setMessage(host + " invites you to join a game.\n yes or no?");
		String message = JsonUtility.toJson(sm);
		write(message);
	}
	
	private void write(String message) {
		try {
			writer.write(message);
			writer.flush();
		} catch (IOException e) {
			System.out.println(
					"Client " + clientSocket.getInetAddress().getHostAddress() + ": Connection interrupted!");
		}
	}
}
