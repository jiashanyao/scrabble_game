package ass.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import ass.communication.ClientMessage;
import ass.communication.GameContext;
import ass.communication.JsonUtility;
import ass.communication.ServerMessage;

public class ClientConnection extends Thread{

	private Socket clientSocket;

	private Server server;

	private GameContext gameContext;
	
	private String userId;
	
	private ClientState clientState;
	
	private BufferedReader reader;
	
	private BufferedWriter writer;
	
	public ClientConnection(Socket socket, Server server) {
		clientSocket = socket;
		this.server = server;
		gameContext = null;
		userId = null;
		clientState = null;
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
		ClientConnection thisClientConnection = this;
		Thread read = new Thread() { // thread for reading requests
			@Override
			public void run() {
				String input = null;
				try {
					while ((input = reader.readLine()) != null) {
						ClientMessage cm = JsonUtility.fromJson(input, ClientMessage.class);
						if (cm.getType() == ClientMessage.Type.SYNC) {
							ServerMessage sm = new ServerMessage();
							if (server.getClients().containsKey(cm.getUserId())){
								sm.setType(ServerMessage.Type.REQUEST);
								sm.setMessage(Dictionary.ID_DUP);
								//clientSocket.close();		// if username duplicates, socket can close
								//return;					// and thread can terminate
								// or request user to enter another username?
							} else {
								userId = cm.getUserId();
								clientState = ClientState.IDLE;
								server.getClients().put(userId, thisClientConnection);
								sm.setType(ServerMessage.Type.INFORMATION);
								sm.setMessage(Dictionary.ID_OK);
							}
							write(sm);
						} else {
							server.getMessageQueue().put(cm);
						}
					}
				} catch (IOException e) {
					System.out.println(
							"Client " + clientSocket.getInetAddress().getHostAddress() + ": Connection interrupted!");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		read.start();
	}
	
	public void invitedBy(String host) {
		ServerMessage sm = new ServerMessage();
		sm.setType(ServerMessage.Type.REQUEST);
		sm.setMessage(host + " invites you to join a game.\n yes or no?");
		write(sm);
	}
	
	public void write(ServerMessage serverMessage) {
		try {
			String jsonString = JsonUtility.toJson(serverMessage);
			writer.write(jsonString);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			System.out.println(
					"Client " + clientSocket.getInetAddress().getHostAddress() + ": Connection interrupted!");
		}
	}

	public GameContext getGameContext() {
		return gameContext;
	}

	public void setGameContext(GameContext gameContext) {
		this.gameContext = gameContext;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public ClientState getClientState() {
		return clientState;
	}

	public void setClientState(ClientState clientState) {
		this.clientState = clientState;
	}

	public enum ClientState{
		IDLE, INVITING, INVITED, GAMING
	}
}
