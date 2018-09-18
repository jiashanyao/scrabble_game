package ass.client;

import javax.swing.*;
import java.awt.*;

/**
 * Created by hugh on 18/9/18.
 */
public class ClientConsole extends JFrame {

    public static void main(String[] args) {
        int rows = 2;
        int cols = 3;
        ClientConsole gt = new ClientConsole(rows, cols);
        gt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gt.pack();
        gt.setVisible(true);
    }

    public ClientConsole(int rows, int cols) {
        Container pane = getContentPane();
        pane.setLayout(new GridLayout(rows, cols));
        for (int i = 0; i < 20; i++) {
            JButton button = new JButton(Integer.toString(i + 1));
            pane.add(button);
        }
    }
}
