package ass.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameContext {

    private List<String> gamingUsers;

    private List<String> invitedUser;

    private GameStatus gameStatus;

    private String currentUser;

    private Integer cellX;

    private Integer cellY;

    private String[][] gameBoard;

    private String[] highLight;

    private Map<String, Integer> scores;

    public GameContext() {
        gamingUsers = new ArrayList<>();
        invitedUser = new ArrayList<>();
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
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

    public String[][] getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(String[][] gameBoard) {
        this.gameBoard = gameBoard;
    }

    public String[] getHighLight() {
        return highLight;
    }

    public void setHighLight(String[] highLight) {
        this.highLight = highLight;
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public void setScores(Map<String, Integer> scores) {
        this.scores = scores;
    }

    public List<String> getGamingUsers() {
        return gamingUsers;
    }

    public void setGamingUsers(List<String> gamingUsers) {
        this.gamingUsers = gamingUsers;
    }

    public List<String> getInvitedUser() {
        return invitedUser;
    }

    public void setInvitedUser(List<String> invitedUser) {
        this.invitedUser = invitedUser;
    }

    public enum GameStatus {
        IDLING, INVITING, GAMING, HIGHLIGHT, VOTING
    }

}
