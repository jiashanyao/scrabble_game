package ass.server.dummy;

import ass.communication.ClientMessage;
import ass.communication.GameContext;
import ass.communication.JsonUtility;
import ass.communication.ServerMessage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ClientConnection extends Thread {

    private Socket clientSocket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Map<ClientMessage.Type, GameContext> action;

    public ClientConnection(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
            action = new HashMap<>();
            // init GameContext
            GameContext gc2 = new GameContext();
            gc2.setCurrentUser("Hugh");
            gc2.setGameStatus(GameContext.GameStatus.GAMING);
            gc2.setInvitedUser(null);
            gc2.setIdleUsers(new String[] {"Zoe", "Hugh", "AKB69"});
            gc2.setGamingUsers(new String[] {"Hugh", "AKB69"});
            gc2.setCellX(10);
            gc2.setCellX(6);
            String[][] gameBoard = new String[20][20];
            HashMap<String, Integer> scores = new HashMap<>();
            Random r = new Random();
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    if (i == 5 && j > 2 && j < 15) {
                        gameBoard[i][j] = String.valueOf((char) (r.nextInt((90 - 65) + 1) + 65));
                    } else if (j == 10 && i > 2 && i < 15) {
                        gameBoard[i][j] = String.valueOf((char) (r.nextInt((90 - 65) + 1) + 65));
                    }
                }
            }
            gameBoard[5][10] = "";
            scores.put("Hugh", 94);
            scores.put("AKB69", 87);

            gc2.setGameBoard(gameBoard);
            gc2.setScores(scores);
            action.put(ClientMessage.Type.START, gc2);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override public void run() {
        try {
            String clientMsg = null;

            //             waiting for incoming request from client
            while ((clientMsg = reader.readLine()) != null) {
                System.out.print("In coming request:");
                System.out.println(clientMsg);
                ClientMessage clientMessage = JsonUtility.fromJson(clientMsg, ClientMessage.class);
                ClientMessage.Type type = clientMessage.getType();
                ServerMessage sm = new ServerMessage(new GameContext());

                if(type.equals(ClientMessage.Type.CHARACTER)){
                    GameContext c = action.get(ClientMessage.Type.START);
                    c.getGameBoard()[clientMessage.getCellX()][clientMessage.getCellY()]=clientMessage.getCellChar();
                    c.setGameStatus(GameContext.GameStatus.HIGHLIGHT);
                    sm.setType(ServerMessage.Type.REQUEST);
                    c.setCellX(clientMessage.getCellX());
                    c.setCellY(clientMessage.getCellY());
                    sm.setGameContext(c);
                }else if(type.equals(ClientMessage.Type.START)){
                    GameContext c = action.get(ClientMessage.Type.START);
                    c.getGameBoard()[5][10]="";
                    sm.setGameContext(action.get(ClientMessage.Type.START));
                    sm.setType(ServerMessage.Type.INFORMATION);
                    sm.setGameContext(c);

                }

                // response to client
                writer.write(JsonUtility.toJson(sm) + "\n");
                writer.flush();
            }
            // TODO timing to disconnect
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
