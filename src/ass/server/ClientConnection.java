package ass.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

import ass.communication.ClientMessage;
import ass.communication.GameContext;
import ass.communication.JsonUtility;
import ass.communication.ServerMessage;

public class ClientConnection extends Thread{

	private Socket clientSocket;

	private Server server;

	private String clientId;
	
	public ClientConnection(Socket socket, Server server) {
		clientSocket = socket;
		this.server = server;
	}
	
	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
			String input = null;
			while ((input = reader.readLine()) != null) {
				ClientMessage clientMessage = JsonUtility.fromJson(input, ClientMessage.class);
				ServerMessage serverMessage = processRequest(clientMessage);
				String output = JsonUtility.toJson(serverMessage);
				writer.write(output);
				writer.newLine();
				writer.flush();
			}
			clientSocket.close();
			System.out.println(
					"Client " + clientSocket.getInetAddress().getHostAddress() + ": Connection closed.");
		} catch (IOException e) {
			System.out.println(
					"Client " + clientSocket.getInetAddress().getHostAddress() + ": Connection interrupted!");
		}
	}

	private ServerMessage processRequest(ClientMessage cm) {
		ServerMessage sm = new ServerMessage();
		switch(cm.getType()) {
		case SYNC:
			List<String> clientIds = server.getClients().stream().map(ClientConnection::getClientId).collect(Collectors.toList());
			if (clientIds.contains(cm.getUserId())){
				sm.setType(ServerMessage.Type.REQUEST);
				sm.setMessage(Dictionary.ID_DUP);
			} else {
				server.getClients().add(this);
				sm.setType(ServerMessage.Type.INFORMATION);
				sm.setMessage(Dictionary.ID_OK);
			}
		default:
		}
		return sm;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
}
