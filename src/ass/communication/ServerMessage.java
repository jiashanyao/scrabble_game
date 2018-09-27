package ass.communication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hugh on 20/9/18.
 */
public class ServerMessage {

    private Type type;

    private List<String> idleUsers;

    private GameContext gameContext;

    private Long time;

    private String message;

    private Long expiredTime;

    public ServerMessage() {
        Date now = new Date();
        this.time = now.getTime();

        idleUsers = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.SECOND, 15);
        this.expiredTime = calendar.getTime().getTime();
    }

    public ServerMessage(String message) {
        this.message = message;
        Date now = new Date();
        this.time = now.getTime();

        idleUsers = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.SECOND, 15);
        this.expiredTime = calendar.getTime().getTime();
    }

    public ServerMessage(GameContext gameContext) {
        this.gameContext = gameContext;
        Date now = new Date();
        this.time = now.getTime();

        idleUsers = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.SECOND, 15);
        this.expiredTime = calendar.getTime().getTime();
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

    public List<String> getIdleUsers() {
        return idleUsers;
    }

    public void setIdleUsers(List<String> idleUsers) {
        this.idleUsers = idleUsers;
    }

    public enum Type {
        REQUEST, INFORMATION, ERROR
    }

}
