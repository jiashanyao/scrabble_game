package ass.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import ass.communication.ClientMessage;
import ass.communication.GameContext;
import ass.communication.GameContext.GameStatus;
import ass.communication.ServerMessage;
import ass.communication.ServerMessage.Type;
import ass.server.ClientConnection.ClientState;

public class MessageHandling extends Thread {

    private Server server;

    public MessageHandling(Server server) {
        this.server = server;
        super.setName(this.getClass().getName());
    }

    @Override
    public void run() {
        while (true) {
            try {
                ClientMessage clientMessage = server.getMessageQueue().take();
                System.out.println("took a message from queue");
                messageHandle(clientMessage);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void messageHandle(ClientMessage cm) {
        ClientConnection client = server.getClients().get(cm.getUserId());
        switch (cm.getType()) {
            case INVITATION:
                if (client.getClientState() != ClientState.GAMING) {
                    // if inviting client is not gaming, he can invite
                    GameContext gameContext = null;
                    ServerMessage sm = new ServerMessage();
                    sm.setType(Type.INFORMATION);
                    if (client.getClientState() != ClientState.INVITING) {
                        gameContext = new GameContext();
                        gameContext.setCurrentUser(client.getUserId());
                        gameContext.setGameStatus(GameContext.GameStatus.INVITING);
                        gameContext.getGamingUsers().add(client.getUserId());
                        client.setGameContext(gameContext);
                        client.setClientState(ClientState.INVITING);
                        sm.setMessage("You initiated a game and invited someone(s).");
                    } else {
                        gameContext = client.getGameContext();
                        sm.setMessage("You invited someone(s).");
                    }
                    String[] toBeInvited = cm.getInvitations();
                    List<ClientConnection> invited = new ArrayList<>();
                    for (String user : toBeInvited) {
                        // check if a user is eligible for being invited
                        ClientConnection cc = server.getClients().get(user);
                        if (cc.getClientState() != ClientState.GAMING && cc.getClientState() != ClientState.INVITING) {
                            // if invited client is not gaming or inviting, invite him
                            // new invitation can overwrite old invitation
                            invited.add(cc);
                            gameContext.getInvitedUser().add(user);
                        }
                    }
                    for (ClientConnection cc : invited) {
                        cc.setGameContext(gameContext);
                        cc.setClientState(ClientState.INVITED);
                        ServerMessage invitation = new ServerMessage(client.getUserId() + " invites you to a game. yes/no?");
                        invitation.setType(ServerMessage.Type.REQUEST);
                        invitation.setIdleUsers(server.getIdleUsers());
                        invitation.setGameContext(gameContext);
                        cc.write(invitation);
                    }
                    sm.setGameContext(gameContext);
                    sm.setIdleUsers(server.getIdleUsers());
                    client.write(sm);
                    server.idleUserUpdate();
                }
                break;
            case INVITATION_CONFIRM:
                if (client.getClientState() == ClientConnection.ClientState.INVITED
                        && client.getGameContext() != null
                        && client.getGameContext().getGameStatus() == GameStatus.INVITING
                        && cm.isAccept()) { // whether eligible to join a game
                    client.getGameContext().getGamingUsers().add(client.getUserId()); // join game of latest invitation
                    client.setClientState(ClientState.INVITING); // after joining, he can invite
                    // other users as well
                    ServerMessage toInGameUsers = new ServerMessage(client.getUserId() + " has joined game.");
                    /* update Playing Players list */
                    toInGameUsers.setType(Type.INFORMATION);
                    toInGameUsers.setGameContext(client.getGameContext());
                    toInGameUsers.setIdleUsers(server.getIdleUsers());
                    // send to in-game users
                    for (String userId : client.getGameContext().getGamingUsers()) {
                        server.getClients().get(userId).write(toInGameUsers);
                    }
                    // send to host
                    server.getClients().get(client.getGameContext().getCurrentUser()).write(toInGameUsers);
                    server.idleUserUpdate();
                }
                break;
            case START: {
                GameContext startContext = client.getGameContext();
                if (startContext.getGameStatus().equals(GameStatus.INVITING) && startContext.getGamingUsers().size() >= 2) {
                    String currentUserID = cm.getUserId();
                    startContext.setGameStatus(GameStatus.GAMING);
                    startContext.setCurrentUser(currentUserID);
                    client.setGameContext(startContext);

                    startGameClientStatusChange(startContext.getGamingUsers());
                    // initial scores
                    for (String userId : startContext.getGamingUsers()) {
                        startContext.getScores().put(userId, 0);
                    }

                    ServerMessage startInformation = new ServerMessage(startContext);
                    startInformation.setIdleUsers(server.getIdleUsers());
                    startInformation.setType(Type.INFORMATION);
                    startInformation.setMessage("Game Start!");
                    notifyInGameClients(startContext, startInformation);
                }
                break;
            }
            case CHARACTER: {
                if (client.getClientState().equals(ClientState.IDLE)) { // If the user is not in any game,
                    // ignore this message and sends an game-already-ends message to the user.
                    GameContext endContext = new GameContext();
                    endContext.setGameStatus(GameStatus.IDLING);
                    ServerMessage gameAlreadyEnds = new ServerMessage(endContext);
                    gameAlreadyEnds.setMessage("Game already ends by others!");
                    client.write(gameAlreadyEnds);
                    break;
                }
                if (client.getClientState().equals(ClientState.PASSING)) {
                    client.setClientState(ClientState.GAMING);
                }
                int x = cm.getCellX();
                int y = cm.getCellY();
                GameContext charContext = client.getGameContext();
                charContext.setCellX(x);
                charContext.setCellY(y);
                String[][] gameBoard = charContext.getGameBoard();
                if (gameBoard[x][y] == null) {  // check if a cell already has a char
                    gameBoard[x][y] = cm.getCellChar();
                } else {
                    break;
                }
                charContext.setGameBoard(gameBoard);
                charContext.setGameStatus(GameStatus.HIGHLIGHT);
                client.setGameContext(charContext);
                ServerMessage charMessage = new ServerMessage(charContext);
                charMessage.setType(Type.REQUEST);
                charMessage.setMessage("Would you like to highlight term(s) for '" + cm.getCellChar() + "'?");
                client.write(charMessage);
                break;
            }
            case HIGHLIGHT: {
                String[] highStr = cm.getHighLight();
                GameContext highContext = client.getGameContext();
                if ((highStr == null) || (highStr[0].equals("") && highStr[1].equals(""))) {
                    highContext.setGameStatus(GameStatus.GAMING);
                    String currentUser = getNextUser(highContext.getGamingUsers(), highContext.getCurrentUser());
                    highContext.setCurrentUser(currentUser);
                    ServerMessage highLightMessage = new ServerMessage(highContext);
                    highLightMessage.setMessage("Now it is " + currentUser + "'s turn");
                    notifyInGameClients(highContext, highLightMessage);
                } else {
                    highContext.setGameStatus(GameStatus.VOTING);
                    highContext.setHighLight(highStr);
                    changeClientThreadStatus(highContext.getGamingUsers(), ClientState.VOTING);
                    client.setClientState(ClientState.GAMING);
                    ServerMessage highLightMessage = new ServerMessage(highContext);
                    highLightMessage.setType(Type.REQUEST);
                    highLightMessage.setMessage("Voting Yes/No for words");
                    notifyOtherGamingUser(highContext.getCurrentUser(), highContext, highLightMessage);
                }
                break;
            }
            case VOTE: {
                if (client.getClientState().equals(ClientState.GAMING)) {
                    ServerMessage sMessage = new ServerMessage(client.getGameContext());
                    sMessage.setMessage("Voting finished already.");
                    client.write(sMessage);
                    break;
                }

                client.setClientState(ClientState.GAMING);
                GameContext voteContext = client.getGameContext();
                Map<String, ClientConnection> clients = this.server.getClients();
                int voteNum = 0;
                for (String uid : voteContext.getGamingUsers()) {
                    if (clients.get(uid).getClientState() == ClientState.GAMING) {
                        voteNum++;
                    }
                }

                if (!cm.isAccept()) {
                    voteContext.setGameStatus(GameStatus.GAMING);
                    voteContext.setCurrentUser(getNextUser(voteContext.getGamingUsers(), voteContext.getCurrentUser()));
                    client.setGameContext(voteContext);
                    ServerMessage voteMessage = new ServerMessage(voteContext);
                    voteMessage.setMessage(client.getUserId() + " vote false for this turn.");
                    changeClientThreadStatus(voteContext.getGamingUsers(), ClientState.GAMING);
                    notifyInGameClients(voteContext, voteMessage);
                    break;
                } else if (voteNum == voteContext.getGamingUsers().size()) {
                    voteContext.setGameStatus(GameStatus.GAMING);
                    Map<String, Integer> scoreList = voteContext.getScores();
                    String currentUID = voteContext.getCurrentUser();
                    int currentScore = 0;
                    int highLightNum = 0;
                    for (String highString : cm.getHighLight()) {
                        if (highString != null && highString.length() > 0) {
                            highLightNum = highLightNum + 1;
                        }
                        if (highString != null) {
                            currentScore = currentScore + highString.length();
                        }

                    }

                    // If highlighting a cross, the center character will be calculated twice.
                    if (highLightNum == 2) {
                        currentScore = currentScore - 1;
                    }

                    if (scoreList.containsKey(currentUID)) {
                        currentScore = scoreList.get(currentUID) + currentScore;
                    }
                    scoreList.put(currentUID, currentScore);
                    voteContext.setCurrentUser(getNextUser(voteContext.getGamingUsers(), voteContext.getCurrentUser()));
                    changeClientThreadStatus(voteContext.getGamingUsers(), ClientState.GAMING);
                    client.setGameContext(voteContext);
                    ServerMessage voteMessage = new ServerMessage(voteContext);
                    notifyInGameClients(voteContext, voteMessage);
                }
                break;
            }
            case PASS: {
                client.setClientState(ClientState.PASSING);
                GameContext passContext = client.getGameContext();
                passContext.setCurrentUser(getNextUser(passContext.getGamingUsers(), passContext.getCurrentUser()));
                client.setGameContext(passContext);

                ServerMessage passMessage = new ServerMessage(passContext);
                passMessage.setMessage(client.getUserId() + "passed this turn.");
                int currentUserNum = client.getGameContext().getGamingUsers().size();
                if (countPassingNum(passContext.getGamingUsers()) != currentUserNum) {
                    notifyInGameClients(passContext, passMessage);
                    break;
                }
            }
            case END:
                GameContext endContext = new GameContext();
                endContext.setGameStatus(GameStatus.IDLING);
                ServerMessage endMessage = new ServerMessage(endContext);
                endMessage.setIdleUsers(server.getIdleUsers());
                endMessage.setType(Type.INFORMATION);
                Map.Entry<String, Integer> winner = getWinner(client.getGameContext().getScores());
                endMessage.setMessage("Game End. Winner is " + winner.getKey() + ", score is " + winner.getValue());
                for (String userId : client.getGameContext().getGamingUsers()) {
                    server.getClients().get(userId).write(endMessage);
                }
                changeClientThreadStatus(client.getGameContext().getGamingUsers(), ClientState.IDLE);
                server.idleUserUpdate();
                client.setGameContext(null);
                break;
            default:
                break;
        }
    }

    private void notifyInGameClients(GameContext gameContext, ServerMessage serverMessage) {
        List<String> gamingUsers = gameContext.getGamingUsers();
        for (String userId : gamingUsers) {
            server.getClients().get(userId).write(serverMessage);
        }
    }

    /**
     * 
     * @param userID CurrentUser ID
     * @param gameContext
     * @param serverMessage
     */
    private void notifyOtherGamingUser(String userID, GameContext gameContext, ServerMessage serverMessage) {
        List<String> gamingUserList = gameContext.getGamingUsers();
        for (String uid : gamingUserList) {
            if (!uid.equals(userID)) {
                server.getClients().get(uid).write(serverMessage);
            }
        }

    }

    private void notifyAllClients(ServerMessage sm) {
        Map<String, ClientConnection> clients = this.server.getClients();
        Iterator<Entry<String, ClientConnection>> entries = clients.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, ClientConnection> client = entries.next();
            String uid = client.getKey();
            ClientConnection connection = client.getValue();
            connection.write(sm);
        }
    }

    private List<String> getOtherUsers(String currentUser) {
        List<String> oUsers = new ArrayList<>();
        Map<String, ClientConnection> clients = this.server.getClients();
        Iterator<Entry<String, ClientConnection>> entries = clients.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, ClientConnection> client = entries.next();
            String uid = client.getKey();
            if (!uid.equals(currentUser)) {
                oUsers.add(uid);
            }
        }
        return oUsers;
    }

    private int countPassingNum(List<String> gamingUsers) {
        int passingNUM = 0;
        Map<String, ClientConnection> clients = this.server.getClients();
        for (String user : gamingUsers) {
            if (clients.get(user).getClientState() == ClientState.PASSING) {
                passingNUM++;
            }
        }
        return passingNUM;
    }

    private String getNextUser(List<String> gamingUsers, String currentUser) {
        List<String> userList = gamingUsers;
        String uid = "";
        if (userList.contains(currentUser)) {
            int i = userList.indexOf(currentUser);
            if (i < userList.size() - 1) {
                uid = userList.get(i + 1);
            } else {
                uid = userList.get(0);
            }
        }
        System.out.println("current: " + currentUser + " , next: " + uid);
        return uid;
    }

    private void changeClientThreadStatus(List<String> gamingUsers, ClientState cType) {
        Map<String, ClientConnection> clients = this.server.getClients();
        Iterator<Entry<String, ClientConnection>> entries = clients.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, ClientConnection> tClient = entries.next();
            if (gamingUsers.contains(tClient.getKey())) {
                tClient.getValue().setClientState(cType);
            }
        }
    }

    private void startGameClientStatusChange(List<String> gamingUsers) {
        Map<String, ClientConnection> clients = this.server.getClients();
        for (String user : gamingUsers) {
            ClientConnection clientConnection = clients.get(user);
            clientConnection.setClientState(ClientState.GAMING);
        }
    }

    private Map.Entry<String, Integer> getWinner(Map<String, Integer> scoreList) {
        Map.Entry<String, Integer> winner = null;
        for (Map.Entry<String, Integer> scoreItem : scoreList.entrySet()) {
            if (winner == null || (scoreItem.getValue().compareTo(winner.getValue()) > 0)) {
                winner = scoreItem;
            }
        }
        return winner;
    }
}
