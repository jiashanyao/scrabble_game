package ass.server;

import java.net.Socket;

public class ClientConnection extends Thread{

	private Socket clientSocket;
	
	public ClientConnection(Socket socket) {
		clientSocket = socket;
	}
	
	public void run() {
		
	}
}
