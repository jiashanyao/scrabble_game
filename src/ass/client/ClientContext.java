package ass.client;

import ass.communication.ServerMessage;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class ClientContext extends PriorityBlockingQueue<ServerMessage> {

    public ClientContext() {
        super(1, new Comparator<ServerMessage>() {
            @Override public int compare(ServerMessage o1, ServerMessage o2) {
                return Math.toIntExact(o2.getTime() - o1.getTime()) * (-1);
            }
        });
    }
}
