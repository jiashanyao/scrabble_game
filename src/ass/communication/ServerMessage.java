package ass.communication;

/**
 * Created by hugh on 20/9/18.
 */
public class ServerMessage {

    private Type type;

    private GameContext gameContext;

    private Long time;

    private String message;

    private Long expiredTime;

    public ServerMessage() {
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public GameContext getGameContext() {
        return gameContext;
    }

    public void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Long expiredTime) {
        this.expiredTime = expiredTime;
    }

    public enum Type {
        REQUEST, INFORMATION
    }

}


