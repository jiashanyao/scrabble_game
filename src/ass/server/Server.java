package ass.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ass.communication.GameContext;

/**
 * Type port number in command line input to start server.
 */
public class Server {
	
	public static final int CMD_INPUT_LEN = 1;
	
	public static final int PORT_POS = 0;
	
	public static final int MAX_CLIENTS = 4;	// default
	
	private int port;
	
	private ServerSocket serverSocket;
	
	private GameContext gameContext;
	
	private List<ClientConnection> clients;

	public Server(String[] args) {
		parseArgs(args);
		serverSocket = null;
		setGameContext(null);
		clients = new ArrayList<>();
	}
	
    static public void main(String[] args){
    	Server server = new Server(args);
    	server.start();
    }
    
    public void parseArgs(String[] args) {
    	if (args.length < CMD_INPUT_LEN) {
			System.out.println("Not enough arguments provided!\nExiting...");
			System.exit(1);
		}
		if (!isNumeric(args[PORT_POS])) {
			System.out.println("Invalid port number!\nExiting...");
			System.exit(1);
		}
		port = Integer.parseInt(args[PORT_POS]);
    }
    
    public void start() {
    	try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Unable to create a socket! Port may be not available!\nExiting...");
			System.exit(1);
		}
    	Server theServer = this;
    	new Thread() {	// this thread is for listening to and start client connections
    		@Override
    		public void run() {
    			System.out.println("Started listening on port " + serverSocket.getLocalPort());
				while (true) {
					try {
						Socket clientSocket = serverSocket.accept();
						System.out.println("Serving a client at " + clientSocket.getInetAddress().getHostAddress());
						ClientConnection newClient = new ClientConnection(clientSocket, theServer);
						if (clients.size() < MAX_CLIENTS) {
							newClient.start();
						}
					} catch (IOException e) {
						break;
					}
				}
    		}
    	}.start();
    }
    
	public boolean isNumeric(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public GameContext getGameContext() {
		return gameContext;
	}

	public void setGameContext(GameContext gameContext) {
		this.gameContext = gameContext;
	}
	
	public List<ClientConnection> getClients() {
		return clients;
	}
	
	public void setClients(List<ClientConnection> clients) {
		this.clients = clients;
	}
}
