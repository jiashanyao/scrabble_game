package ass.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ass.communication.GameContext;

/**
 */
public class Server {
	
	public static final int CMD_INPUT_LEN = 1;
	
	public static final int PORT_POS = 0;
	
	private int port;
	
	private int maxClients = 4;	// default
	
	private ServerSocket serverSocket;
	
	private GameContext gameContext;
	
	public Server(String[] args) {
		parseArgs(args);
		serverSocket = null;
		gameContext = null;
	}
	
    static public void main(String[] args){
    	new Server(args).start();
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
    	new Thread() {	// this thread is for listening to and start client connections
    		@Override
    		public void run() {
    			System.out.println("Started listening on port " + serverSocket.getLocalPort());
				while (true) {
					try {
						Socket clientSocket = serverSocket.accept();
						System.out.println("Serving a client at " + clientSocket.getInetAddress().getHostAddress());
						
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
}
