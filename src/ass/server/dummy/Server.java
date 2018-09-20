package ass.server.dummy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {

        ServerSocket listeningSocket = null;

        try {

            Integer port = Integer.parseInt(args[0]);
            listeningSocket = new ServerSocket(port);

            // Listen for incoming connections for ever
            System.out.println("Server successfully initiated at port:" + port);
            while (true) {

                // Accept an incoming client connection request
                Socket clientSocket = listeningSocket.accept();

                // Create a client connection to listen for and process all the messages
                // sent by the client
                ClientConnection clientConnection = new ClientConnection(clientSocket);
                clientConnection.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (listeningSocket != null) {
                try {
                    listeningSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
