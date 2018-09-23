package ass.communication;

import java.util.Map;

public class GameContext {

    private String[] idleUsers;

    private String[] gamingUsers;

    private String[] invitedUser;

    private GameStatus gameStatus;

    private String currentUser;

    private Integer cellX;

    private Integer cellY;

    private String[][] gameBoard;

    private Map<String, Integer> scores;

    public GameContext() {
    }

    public String[] getIdleUsers() {
        return idleUsers;
    }

    public void setIdleUsers(String[] idleUsers) {
        this.idleUsers = idleUsers;
    }

    public String[] getGamingUsers() {
        return gamingUsers;
    }

    public void setGamingUsers(String[] gamingUsers) {
        this.gamingUsers = gamingUsers;
    }

    public String[] getInvitedUser() {
        return invitedUser;
    }

    public void setInvitedUser(String[] invitedUser) {
        this.invitedUser = invitedUser;
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

    public Map<String, Integer> getScores() {
        return scores;
    }

    public void setScores(Map<String, Integer> scores) {
        this.scores = scores;
    }

    public enum GameStatus {
        IDLING, INVITING, GAMING, HIGHLIGHT, VOTING
    }

}
