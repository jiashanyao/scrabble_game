package ass.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Game logic
 * @author jiashany
 *
 */
public class Game {

	private String host;
	
	private Map<String, ClientConnection> players;
	
	private State state;

	public Game(ClientConnection host) {
		this.host = host.getClientId();
		players = new ConcurrentHashMap<>();
		players.put(this.host, host);
		state = State.INVITING;
	}
	
	public boolean addPlayer(ClientConnection clientConnection) {
		if (clientConnection != null) {
			players.put(clientConnection.getClientId(), clientConnection);
			clientConnection.invitedBy(host);
			return true;
		}
		return false;
	}
	
	public enum State{
		INVITING, GAMING
	}
}
