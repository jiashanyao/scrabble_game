package ass.client;

import java.io.BufferedReader;
import java.net.SocketException;

public class ContextListener extends Thread {


    private BufferedReader reader;
    private ClientContext context;

    public ContextListener(BufferedReader reader, ClientContext context) {
        this.reader = reader;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            String msg = null;
            while ((msg = reader.readLine()) != null) {
                try {
                    //TODO fromJson
                    //TODO ClientContext update UI
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
