package ass.server;

import ass.communication.ClientMessage;
import ass.communication.GameContext;
import ass.communication.ServerMessage;

public class MessageHandling extends Thread {

	private Server server;
	
	public MessageHandling(Server server) {
		this.server = server;
	}
	
	@Override
	public void run() {
		while(true) {
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
		ServerMessage sm = new ServerMessage();
		switch(cm.getType()) {
		case INVITATION:
			client.setGameContext(new GameContext());
			String[] invited = cm.getInvitations();
			for (String user : invited) {
				ClientConnection cc = server.getClients().get(user);
				cc.write(new ServerMessage("you are invited"));
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
