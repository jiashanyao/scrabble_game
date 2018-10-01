package ass.client;

import ass.communication.ServerMessage;

import java.util.concurrent.LinkedBlockingQueue;

public class ClientContext extends LinkedBlockingQueue<ServerMessage> {

    private Long currentVersion;

    public Long getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(Long currentVersion) {
        this.currentVersion = currentVersion;
    }
}
