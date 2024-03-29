package ass.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import ass.communication.ClientMessage;
import ass.communication.ServerMessage;
import ass.server.ClientConnection.ClientState;

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
        clients = new ConcurrentHashMap<>(); // for concurrent access
        messageQueue = new LinkedBlockingQueue<>();
    }

    static public void main(String[] args) {
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
        Thread listen = new Thread() { // this thread is for listening to and start client connections
            @Override
            public void run() {
                System.out.println("Listening on port " + serverSocket.getLocalPort());
                while (true) {
                    try {
                        if (clients.size() < MAX_CLIENTS) { // limit max client number to prevent crash
                            Socket clientSocket = serverSocket.accept();
                            new ClientConnection(clientSocket, theServer).start();
                        } else {
                            Thread.sleep(5000); // iterate every 5 seconds when clients are full for lower CPU use
                        }
                    } catch (IOException e) {
                        break;
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        listen.start();
        MessageHandling messageHandling = new MessageHandling(this);
        messageHandling.start();
        Thread standardInput = new Thread() {
            @Override
            public void run() {
                BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
                try {
                    while (true) {
                        String input = keyboard.readLine();
                        if (input.equals("stats")) {
                            System.out.println("User list:");
                            for (ClientConnection cc : clients.values()) {
                                System.out.println(cc.getUserId() + "\t " + cc.getClientState());
                            }
                        } else if (input.equals("exit")) {
                            System.out.println("Normal exit.");
                            System.exit(0);
                        } else {
                            System.out.println("Unrecognized command!");
                        }
                    }
                } catch (IOException e) {
                    System.out.println("An I/O error happens! Exit.");
                }
            }
        };
        standardInput.start();
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

    public List<String> getIdleUsers() {
        List<String> idleUsers = new ArrayList<>();
        for (ClientConnection cc : clients.values()) {
            if (cc.getClientState() == ClientState.IDLE || cc.getClientState() == ClientState.INVITED) {
                idleUsers.add(cc.getUserId());
            }
        }
        return idleUsers;
    }

    public void idleUserUpdate() {
        ServerMessage idleUserUpdate = new ServerMessage("Idle user update");
        idleUserUpdate.setType(ServerMessage.Type.BROADCAST);
        idleUserUpdate.setIdleUsers(getIdleUsers());
        for (ClientConnection cc : clients.values()) {
            cc.write(idleUserUpdate);
        }
    }
}
