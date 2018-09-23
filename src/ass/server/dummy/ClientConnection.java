package ass.server.dummy;

import ass.communication.ClientMessage;
import ass.communication.GameContext;
import ass.communication.JsonUtility;
import ass.communication.ServerMessage;
import org.apache.commons.beanutils.BeanUtils;

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
            GameContext gc = new GameContext();
            gc.setGameStatus(GameContext.GameStatus.IDLING);
            gc.setIdleUsers(new String[] {"Hugh", "Zoe", "AKB69"});

            action.put(ClientMessage.Type.SYNC, gc);

            GameContext gc1 = new GameContext();
            gc1.setGameStatus(GameContext.GameStatus.INVITING);
            gc1.setIdleUsers(new String[] { "Zoe"});
            gc1.setInvitedUser(new String[] {"Hugh", "AKB69"});
            gc1.setCurrentUser("Hugh");
            action.put(ClientMessage.Type.INVITATION, gc);

            GameContext gc2 = new GameContext();
            gc2.setGameStatus(GameContext.GameStatus.GAMING);
            gc2.setInvitedUser(null);
            gc2.setIdleUsers(new String[] {"Zoe"});
            gc2.setGamingUsers(new String[] {"Hugh", "AKB69"});
            String[][] gameBoard = new String[20][20];
            HashMap<String,Integer> scores = new HashMap<>();
            Random r = new Random();
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    gameBoard[i][j] = String.valueOf((char)(r.nextInt((90 - 65) + 1) + 65));
                }
            }

            scores.put("Hugh", 94);
            scores.put("AKB69", 87);

            gc2.setGameBoard(gameBoard);
            gc2.setScores(scores);
            action.put(ClientMessage.Type.START, gc2);

            GameContext gc3 = new GameContext();
            gc3.setGameStatus(GameContext.GameStatus.GAMING);
            gc3.setInvitedUser(null);
            gc3.setIdleUsers(new String[] {"Zoe"});
            gc3.setGamingUsers(new String[] {"Hugh", "AKB69"});
            gc3.setCurrentUser("AKB69");
            gameBoard[0][0] = "0";
            gameBoard[19][19] = "0";
            gc3.setGameBoard(gameBoard);
            gc3.setScores(scores);
            action.put(ClientMessage.Type.PASS, gc3);

            GameContext gc4 = new GameContext();
            gc4.setGameStatus(GameContext.GameStatus.GAMING);
            gc4.setInvitedUser(null);
            gc4.setIdleUsers(new String[] {"Zoe"});
            gc4.setGamingUsers(new String[] {"Hugh", "AKB69"});
            gc4.setCurrentUser("Hugh");
            gc4.setGameBoard(gameBoard);
            gc4.setScores(scores);
            action.put(ClientMessage.Type.HIGHLIGHT, gc4);

            action.put(ClientMessage.Type.END, gc1);

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
                sm.setType(ServerMessage.Type.INFORMATION);
                sm.setGameContext(action.get(type));
                sm.setTime(new Date().getTime());

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
