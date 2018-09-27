package ass.server;

import ass.communication.ClientMessage;
import ass.communication.GameContext;
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
                        gameContext.getGamingUsers().add(client.getUserId());
                        client.setGameContext(gameContext);
                        client.setClientState(ClientState.INVITING);
                        sm.setMessage("You initiated a game and invited someone(s).");
                    } else {
                        gameContext = client.getGameContext();
                        sm.setMessage("You invited someone(s).");
                    }
                    sm.setGameContext(gameContext);
                    String[] invited = cm.getInvitations();
                    for (String user : invited) {
                        ClientConnection cc = server.getClients().get(user);
                        if (cc.getClientState() != ClientState.GAMING && cc.getClientState() != ClientState.INVITING) {
                            // if invited client is not gaming or inviting, invite him
                            // new invitation can overwrite old invitation
                            cc.setGameContext(gameContext);
                            cc.setClientState(ClientState.INVITED);
                            gameContext.getInvitedUser().add(user);
                            ServerMessage invitation = new ServerMessage(
                                    client.getUserId() + " invites you to a game. yes/no?");
                            invitation.setType(ServerMessage.Type.REQUEST);
                            invitation.setIdleUsers(server.getIdleUsers());
                            cc.write(invitation);
                        }
                    }
                    sm.setIdleUsers(server.getIdleUsers());
                    client.write(sm);
                    ServerMessage idleUserUpdate = new ServerMessage("Idle user update");
                    idleUserUpdate.setType(ServerMessage.Type.INFORMATION);
                    idleUserUpdate.setIdleUsers(server.getIdleUsers());
                    // TODO: notifyAll(idleUserUpdate)
                }
                break;
            case INVITATION_CONFIRM:
                if (client.getClientState() == ClientConnection.ClientState.INVITED && client.getGameContext() != null
                        && cm.isAccept()) {
                    client.getGameContext().getGamingUsers().add(client.getUserId()); // join game of latest invitation
                    client.setClientState(ClientState.INVITING); // after joining, he can invite other users as well
                    ServerMessage sm = new ServerMessage("You are in the game.");
                    sm.setType(Type.INFORMATION);
                    sm.setGameContext(client.getGameContext());
                    sm.setIdleUsers(server.getIdleUsers());
                    client.write(sm);
                }
            case START:
                break;
            case CHARACTER:
                break;
            case HIGHLIGHT:
                break;
            case PASS:
                break;
            case END:
                break;
            default:
        }
    }
}
