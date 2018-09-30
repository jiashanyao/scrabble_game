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
    }

    @Override
    public void run() {
        while (true) {
            try {
                ClientMessage clientMessage = server.getMessageQueue().take();
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
                        if (cc.getClientState() != ClientState.GAMING
                                && cc.getClientState() != ClientState.INVITING) {
                            // if invited client is not gaming or inviting, invite him
                            // new invitation can overwrite old invitation
                            invited.add(cc);
                            gameContext.getInvitedUser().add(user);
                        }
                    }
                    for (ClientConnection cc : invited) {
                        cc.setGameContext(gameContext);
                        cc.setClientState(ClientState.INVITED);
                        ServerMessage invitation = new ServerMessage(
                                client.getUserId() + " invites you to a game. yes/no?");
                        invitation.setType(ServerMessage.Type.REQUEST);
                        invitation.setIdleUsers(server.getIdleUsers());
                        invitation.setGameContext(gameContext);
                        cc.write(invitation);
                    }
                    sm.setGameContext(gameContext);
                    sm.setIdleUsers(server.getIdleUsers());
                    client.write(sm);
                    ServerMessage idleUserUpdate = new ServerMessage("Idle user update");
                    idleUserUpdate.setType(ServerMessage.Type.INFORMATION);
                    idleUserUpdate.setIdleUsers(server.getIdleUsers());
                    notifyAllClients(idleUserUpdate);
                }
                break;
            case INVITATION_CONFIRM:
                if (client.getClientState() == ClientConnection.ClientState.INVITED
                        && client.getGameContext() != null && cm.isAccept()) {
                    client.getGameContext().getGamingUsers().add(client.getUserId()); // join game
                                                                                      // of latest
                                                                                      // invitation
                    client.setClientState(ClientState.INVITING); // after joining, he can invite
                                                                 // other users as well
                    ServerMessage sm = new ServerMessage("You are in the game.");
                    sm.setType(Type.INFORMATION);
                    sm.setGameContext(client.getGameContext());
                    sm.setIdleUsers(server.getIdleUsers());
                    client.write(sm);
                    ServerMessage replyToHost =
                            new ServerMessage(client.getUserId() + " has joined your game.");
                    replyToHost.setType(Type.INFORMATION);
                    replyToHost.setGameContext(client.getGameContext());
                    replyToHost.setIdleUsers(server.getIdleUsers());
                    ClientConnection host =
                            server.getClients().get(client.getGameContext().getCurrentUser());
                    host.write(replyToHost);
                    ServerMessage idleUserUpdate = new ServerMessage("Idle user update");
                    idleUserUpdate.setType(ServerMessage.Type.INFORMATION);
                    idleUserUpdate.setIdleUsers(server.getIdleUsers());
                    notifyAllClients(idleUserUpdate);
                }
                break;
            case START: {
                GameContext startContext = client.getGameContext();
                if (startContext.getGameStatus().equals(GameStatus.INVITING)) {
                    String currentUserID = cm.getUserId();
                    startContext.setGameStatus(GameStatus.GAMING);
                    startContext.setCurrentUser(currentUserID);
                    client.setGameContext(startContext);

                    startGameClientStatusChange(startContext.getGamingUsers());

                    ServerMessage startInformation = new ServerMessage(startContext);
                    startInformation.setIdleUsers(server.getIdleUsers());
                    startInformation.setType(Type.INFORMATION);
                    startInformation.setMessage("Game Start!");
                    notifyAllClients(startInformation);
                }
                break;
            }
            case CHARACTER: {
                int x = cm.getCellX();
                int y = cm.getCellY();
                GameContext charContext = client.getGameContext();
                charContext.setCellX(x);
                charContext.setCellY(y);
                String[][] gameBoard = charContext.getGameBoard();
                gameBoard[x][y] = cm.getCellChar();
                charContext.setGameBoard(gameBoard);
                charContext.setGameStatus(GameStatus.HIGHLIGHT);
                client.setGameContext(charContext);
                ServerMessage charMessage = new ServerMessage(charContext);
                charMessage.setType(Type.INFORMATION);
                client.write(charMessage);
                break;
            }
            case HIGHLIGHT: {
                String[] highStr = cm.getHighLight();
                GameContext highContext = client.getGameContext();
                if ((highStr == null) || (highStr[0] == "" && highStr[1] == "")) {
                    highContext.setGameStatus(GameStatus.GAMING);
                    String currentUser =
                            getNextUser(highContext.getGamingUsers(), highContext.getCurrentUser());
                    highContext.setCurrentUser(currentUser);
                    ServerMessage highLightMessage = new ServerMessage(highContext);
                    highLightMessage.setMessage("Now it is " + currentUser + "'s turn");
                    notifyAllClients(highLightMessage);
                } else {
                    highContext.setGameStatus(GameStatus.VOTING);
                    highContext.setHighLight(highStr);
                    changeClientThreadStatus(ClientState.VOTING);
                    ServerMessage highLightMessage = new ServerMessage(highContext);
                    highLightMessage.setType(Type.REQUEST);
                    highLightMessage.setMessage("Voting Yes/No for words");
                    notifyAllClients(highLightMessage);
                }
                break;
            }
            case VOTE: {
                client.setClientState(ClientState.GAMING);
                GameContext voteContext = client.getGameContext();
                Map<String, ClientConnection> clients = this.server.getClients();
                int voteNum = 0;
                for (String uid : voteContext.getGamingUsers()) {
                    if (clients.get(uid).getClientState() == ClientState.GAMING) {
                        voteNum++;
                    }
                }
                if (voteNum == voteContext.getGamingUsers().size()) {
                    voteContext.setGameStatus(GameStatus.GAMING);
                    voteContext.setCurrentUser(
                            getNextUser(voteContext.getGamingUsers(), cm.getUserId()));
                    Map<String, Integer> scoreList = voteContext.getScores();
                    String currentUID = voteContext.getCurrentUser();
                    int currentScore = 0;
                    for (String highString : cm.getHighLight()) {
                        currentScore = currentScore + highString.length();
                    }
                    if (scoreList.containsKey(currentUID)) {
                        currentScore = scoreList.get(currentUID) + currentScore;
                    }
                    scoreList.put(currentUID, currentScore);
                    client.setGameContext(voteContext);
                    ServerMessage voteMessage = new ServerMessage(voteContext);
                    notifyAllClients(voteMessage);
                } else {
                    if (!cm.getResponse()) {
                        voteContext.setGameStatus(GameStatus.GAMING);
                        voteContext.setCurrentUser(
                                getNextUser(voteContext.getGamingUsers(), cm.getUserId()));
                        client.setGameContext(voteContext);
                        ServerMessage voteMessage = new ServerMessage(voteContext);
                        notifyAllClients(voteMessage);
                    }
                }
                break;
            }
            case PASS: {
                client.setClientState(ClientState.PASSING);
                GameContext passContext = client.getGameContext();
                passContext
                        .setCurrentUser(getNextUser(passContext.getGamingUsers(), cm.getUserId()));
                client.setGameContext(passContext);

                ServerMessage passMessage = new ServerMessage(passContext);
                int currentUserNum = client.getGameContext().getGamingUsers().size();
                if (countPassingNum(passContext.getGamingUsers()) != currentUserNum) {
                    notifyAllClients(passMessage);
                    break;
                }
            }
            case END:
                client.setGameContext(null);
                ServerMessage endMessage = new ServerMessage(client.getGameContext());
                endMessage.setType(Type.INFORMATION);
                notifyAllClients(endMessage);
                break;
            default:
                break;
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
        String uid = currentUser;
        if (userList.contains(uid)) {
            int i = userList.indexOf(uid);
            if (i < userList.size() - 1) {
                uid = userList.get(i + 1);
            } else {
                uid = userList.get(0);
            }
        }
        return uid;
    }

    private void changeClientThreadStatus(ClientState cType) {
        Map<String, ClientConnection> clients = this.server.getClients();
        Iterator<Entry<String, ClientConnection>> entries = clients.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, ClientConnection> tClient = entries.next();
            tClient.getValue().setClientState(cType);
        }
    }

    private void startGameClientStatusChange(List<String> gamingUsers) {
        Map<String, ClientConnection> clients = this.server.getClients();
        for (String user : gamingUsers) {
            ClientConnection clientConnection = clients.get(user);
            clientConnection.setClientState(ClientState.GAMING);
        }
    }
}
