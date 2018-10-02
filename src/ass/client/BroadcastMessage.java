package ass.client;

import java.util.concurrent.LinkedBlockingQueue;

import ass.communication.ServerMessage;

public class BroadcastMessage extends LinkedBlockingQueue<ServerMessage> {
    
    private Long currentVersion;

    public Long getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(Long currentVersion) {
        this.currentVersion = currentVersion;
    }
}
