package ass.client;

import ass.communication.JsonUtility;
import ass.communication.ServerMessage;
import ass.communication.ServerMessage.Type;

import java.io.BufferedReader;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

public class ContextListener extends Thread {

    private BufferedReader reader;
    private ClientContext context;
    private BroadcastMessage broadcastMessage;

    public ContextListener(BufferedReader reader, ClientContext context, BroadcastMessage broadcastMessage) {
        this.reader = reader;
        this.context = context;
        this.broadcastMessage = broadcastMessage;
    }

    @Override public void run() {
        try {
            String msg = null;
            while ((msg = reader.readLine()) != null) {
                try {
                    // parse message
                    System.out.println("get message:" + msg);
                    ServerMessage serverMessage = JsonUtility.fromJson(msg, ServerMessage.class);

                    if (serverMessage.getType() == Type.BROADCAST) {
                        // update broadcast message queue
                        broadcastMessage.put(serverMessage);
                    } else {
                        // update ClientContext
                        context.put(serverMessage);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            System.out.println("Socket closed because the user typed exit");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
