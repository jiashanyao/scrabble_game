package ass.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import ass.communication.ClientMessage;
import ass.communication.GameContext;
import ass.communication.GameContext.GameStatus;
import ass.communication.JsonUtility;
import ass.communication.ServerMessage;

public class ClientConnection extends Thread {

    private String userId;

    private Socket clientSocket;

    private Server server;

    private GameContext gameContext;

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
            System.out
                    .println("Client " + clientSocket.getInetAddress().getHostAddress() + ": Connection interrupted!");
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
                            if (server.getClients().containsKey(cm.getUserId())) {
                                ServerMessage sm = new ServerMessage(Dictionary.ID_DUP);
                                sm.setType(ServerMessage.Type.ERROR);
                                write(sm);
                                clientSocket.close(); // if username duplicates, socket can close
                                return; // and thread can terminate
                            } else {
                                userId = cm.getUserId();
                                clientState = ClientState.IDLE;
                                server.getClients().put(userId, thisClientConnection);
                                System.out.println(userId + "\t Connection established.");
                                ServerMessage sm = new ServerMessage(Dictionary.ID_OK);
                                sm.setType(ServerMessage.Type.BROADCAST);
                                sm.setIdleUsers(server.getIdleUsers());
                                write(sm);
                                server.idleUserUpdate();
                            }
                        } else {
                            server.getMessageQueue().put(cm);
                        }
                        System.out.println("get message: " + input);
                    }
                    clientSocket.close();
                    server.getClients().remove(userId, thisClientConnection);
                    for (ClientMessage cm : server.getMessageQueue()) {
                        // remove the message sent by the user if he exits to prevent messing up message handling
                        if (cm.getUserId().equals(userId))
                            server.getMessageQueue().remove(cm);                            
                    }
                    server.idleUserUpdate();
                    System.out.println(userId + "\t Connection closed.");
                } catch (IOException e) {
                    System.out.println(userId + "\t Connection interrupted!");
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        read.start();
    }

    public void write(ServerMessage serverMessage) {
        try {
            String jsonString = JsonUtility.toJson(serverMessage);
            writer.write(jsonString);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.out
                    .println("Client " + clientSocket.getInetAddress().getHostAddress() + ": Connection interrupted!");
        }
    }

    /**
     * Write server message to other clients except this client
     * 
     * @param serverMessage
     */
    public void writeToOthers(ServerMessage serverMessage) {
        for (ClientConnection cc : server.getClients().values()) {
            if (cc != this)
                cc.write(serverMessage);
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
        IDLE, INVITING, INVITED, GAMING, VOTING, PASSING
    }
}
