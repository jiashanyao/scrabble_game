package ass.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import ass.communication.ClientMessage;

/**
 * Enter port number as command line input to start server.
 */
public class Server {
	
	public static final int CMD_INPUT_LEN = 1;
	
	public static final int PORT_POS = 0;
	
	public static final int MAX_CLIENTS = 4;
	
	private int port;
	
	private ServerSocket serverSocket;
	
	private Map<String, ClientConnection> clients;
	
	private LinkedBlockingQueue<ClientMessage> messageQueue;

	public Server(String[] args) {
		parseArgs(args);
		serverSocket = null;
		clients = new ConcurrentHashMap<>();
		messageQueue = new LinkedBlockingQueue<>();
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
						if (clients.size() < MAX_CLIENTS) {
							new ClientConnection(clientSocket, theServer).start();
						}
					} catch (IOException e) {
						break;
					}
				}
    		}
    	}.start();
    	MessageHandling messageHandling = new MessageHandling(this);
    	messageHandling.start();
    }
    
	public boolean isNumeric(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public Map<String, ClientConnection> getClients() {
		return clients;
	}

	public LinkedBlockingQueue<ClientMessage> getMessageQueue() {
		return messageQueue;
	}

}
