package ass.communication;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by hugh on 20/9/18.
 */
public class ServerMessage {

    private Type type;

    private GameContext gameContext;

    private Long time;

    private String message;

    private Integer cellX;

    private Integer cellY;

    private Long expiredTime;

    public ServerMessage(GameContext gameContext) {
        this.gameContext = gameContext;

        Date now = new Date();
        this.time = now.getTime();

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

    public Integer getCellX() {
        return cellX;
    }

    public void setCellX(Integer cellX) {
        this.cellX = cellX;
    }

    public Integer getCellY() {
        return cellY;
    }

    public void setCellY(Integer cellY) {
        this.cellY = cellY;
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


