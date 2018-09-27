package ass.communication;

public class ClientMessage {

    private String userId;

    private Type type;

    private String[] invitations;

    private boolean accept;

    private Boolean response;

    private Integer cellX;

    private Integer cellY;

    private String cellChar;

    private String[] highLight;

    public ClientMessage() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String[] getInvitations() {
        return invitations;
    }

    public void setInvitations(String[] invitations) {
        this.invitations = invitations;
    }

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
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

    public String getCellChar() {
        return cellChar;
    }

    public void setCellChar(String cellChar) {
        this.cellChar = cellChar;
    }

    public String[] getHighLight() {
        return highLight;
    }

    public void setHighLight(String[] highLight) {
        this.highLight = highLight;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setResponse(boolean response) {
        this.accept = response;
    }

    public enum Type {
        SYNC, INVITATION, INVITATION_CONFIRM, START, CHARACTER, HIGHLIGHT, VOTE, PASS, END
    }

}
