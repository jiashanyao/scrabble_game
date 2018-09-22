package ass.client;

import ass.communication.JsonUtility;
import ass.communication.ServerMessage;

import java.io.BufferedReader;
import java.net.SocketException;

public class ContextListener extends Thread {

    private BufferedReader reader;
    private ClientContext context;

    public ContextListener(BufferedReader reader, ClientContext context) {
        this.reader = reader;
        this.context = context;
    }

    @Override public void run() {
        try {
            String msg = null;
            while ((msg = reader.readLine()) != null) {
                try {
                    // parse message
                    System.out.println("get message:" + msg);
                    ServerMessage serverMessage = JsonUtility.fromJson(msg, ServerMessage.class);

                    //update ClientContext
                    context.add(serverMessage);

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
