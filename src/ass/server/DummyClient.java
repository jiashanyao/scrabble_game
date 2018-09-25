package ass.server;

import ass.communication.ClientMessage;
import ass.communication.JsonUtility;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class DummyClient {

	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket = new Socket("localhost", 1234);
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
		new Thread() {
			@Override
			public void run() {
				String output = null;
				try {
					while ((output = reader.readLine()) != null) {
						System.out.println(output);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		Scanner scanner = new Scanner(System.in);
		String userId = null;
		while (true) {
			String output = scanner.nextLine();
			String json = null;
			ClientMessage cm = new ClientMessage();
			if (output.equals("login")) {
				cm.setType(ClientMessage.Type.SYNC);
				System.out.println("Username: ");
				userId = scanner.nextLine();
				cm.setUserId(userId);
			}
			if (output.equals("invite")) {	// invite all users
				String[] invited = scanner.nextLine().split("/s");
				cm.setUserId(userId);
				cm.setInvitations(invited);
				cm.setType(ClientMessage.Type.INVITATION);
			}
			json = JsonUtility.toJson(cm);
			try {
				writer.write(json);
				writer.newLine();
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
