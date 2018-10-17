package ass.client;

import javax.swing.*;
import java.awt.*;

public class ClientLogin {

    private JFrame frame;
    private JTextField textFieldServerAddr;
    private JTextField textFieldServerPort;
    private JTextField textFieldUserName;
    private JButton btnLogin;
    private JLabel lblTitle;
    private JLabel lblTeamName;

    /**
     * Create the application.
     */
    public ClientLogin() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 382);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {0, 90, 78, 160, 0, 0};
        gridBagLayout.rowHeights = new int[] {0, 52, 0, 0, 0, 0, 27, 0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[] {0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        frame.getContentPane().setLayout(gridBagLayout);

        lblTitle = new JLabel("SCRABBLE GAME");
        lblTitle.setFont(new Font("Arial Black", Font.PLAIN, 20));
        GridBagConstraints gbc_lblTitle = new GridBagConstraints();
        gbc_lblTitle.gridwidth = 3;
        gbc_lblTitle.insets = new Insets(0, 0, 5, 5);
        gbc_lblTitle.gridx = 1;
        gbc_lblTitle.gridy = 1;
        frame.getContentPane().add(lblTitle, gbc_lblTitle);

        lblTeamName = new JLabel("Team: Multi-threaded");
        lblTeamName.setFont(new Font("Calibri", Font.PLAIN, 18));
        GridBagConstraints gbc_lblTeamName = new GridBagConstraints();
        gbc_lblTeamName.gridwidth = 2;
        gbc_lblTeamName.insets = new Insets(0, 0, 5, 5);
        gbc_lblTeamName.gridx = 2;
        gbc_lblTeamName.gridy = 2;
        frame.getContentPane().add(lblTeamName, gbc_lblTeamName);

        JLabel lblServerAddress = new JLabel("Server Address");
        lblServerAddress.setFont(new Font("Calibri", Font.PLAIN, 18));
        GridBagConstraints gbc_lblServerAddress = new GridBagConstraints();
        gbc_lblServerAddress.insets = new Insets(0, 0, 5, 5);
        gbc_lblServerAddress.anchor = GridBagConstraints.EAST;
        gbc_lblServerAddress.gridx = 1;
        gbc_lblServerAddress.gridy = 4;
        frame.getContentPane().add(lblServerAddress, gbc_lblServerAddress);

        textFieldServerAddr = new JTextField();
        textFieldServerAddr.setFont(new Font("Calibri", Font.PLAIN, 18));
        GridBagConstraints gbc_textFieldServerAddr = new GridBagConstraints();
        gbc_textFieldServerAddr.gridwidth = 2;
        gbc_textFieldServerAddr.fill = GridBagConstraints.HORIZONTAL;
        gbc_textFieldServerAddr.insets = new Insets(0, 0, 5, 5);
        gbc_textFieldServerAddr.anchor = GridBagConstraints.NORTH;
        gbc_textFieldServerAddr.gridx = 2;
        gbc_textFieldServerAddr.gridy = 4;
        frame.getContentPane().add(textFieldServerAddr, gbc_textFieldServerAddr);
        textFieldServerAddr.setColumns(10);

        JLabel lblServerPort = new JLabel("Server Port");
        lblServerPort.setFont(new Font("Calibri", Font.PLAIN, 18));
        GridBagConstraints gbc_lblServerPort = new GridBagConstraints();
        gbc_lblServerPort.anchor = GridBagConstraints.EAST;
        gbc_lblServerPort.insets = new Insets(0, 0, 5, 5);
        gbc_lblServerPort.gridx = 1;
        gbc_lblServerPort.gridy = 5;
        frame.getContentPane().add(lblServerPort, gbc_lblServerPort);

        textFieldServerPort = new JTextField();
        textFieldServerPort.setFont(new Font("Calibri", Font.PLAIN, 18));
        GridBagConstraints gbc_textFieldServerPort = new GridBagConstraints();
        gbc_textFieldServerPort.insets = new Insets(0, 0, 5, 5);
        gbc_textFieldServerPort.fill = GridBagConstraints.HORIZONTAL;
        gbc_textFieldServerPort.gridx = 2;
        gbc_textFieldServerPort.gridy = 5;
        frame.getContentPane().add(textFieldServerPort, gbc_textFieldServerPort);
        textFieldServerPort.setColumns(10);

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Calibri", Font.PLAIN, 18));
        GridBagConstraints gbc_lblUsername = new GridBagConstraints();
        gbc_lblUsername.anchor = GridBagConstraints.EAST;
        gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
        gbc_lblUsername.gridx = 1;
        gbc_lblUsername.gridy = 6;
        frame.getContentPane().add(lblUsername, gbc_lblUsername);

        textFieldUserName = new JTextField();
        textFieldUserName.setFont(new Font("Calibri", Font.PLAIN, 18));
        GridBagConstraints gbc_textFieldUserName = new GridBagConstraints();
        gbc_textFieldUserName.gridwidth = 2;
        gbc_textFieldUserName.insets = new Insets(0, 0, 5, 5);
        gbc_textFieldUserName.fill = GridBagConstraints.HORIZONTAL;
        gbc_textFieldUserName.gridx = 2;
        gbc_textFieldUserName.gridy = 6;
        frame.getContentPane().add(textFieldUserName, gbc_textFieldUserName);
        textFieldUserName.setColumns(10);

        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Calibri", Font.PLAIN, 18));
        GridBagConstraints gbc_btnLogin = new GridBagConstraints();
        gbc_btnLogin.insets = new Insets(0, 0, 5, 5);
        gbc_btnLogin.gridx = 3;
        gbc_btnLogin.gridy = 8;
        frame.getContentPane().add(btnLogin, gbc_btnLogin);
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public JTextField getTextFieldServerAddr() {
        return textFieldServerAddr;
    }

    public void setTextFieldServerAddr(JTextField textFieldServerAddr) {
        this.textFieldServerAddr = textFieldServerAddr;
    }

    public JTextField getTextFieldServerPort() {
        return textFieldServerPort;
    }

    public void setTextFieldServerPort(JTextField textFieldServerPort) {
        this.textFieldServerPort = textFieldServerPort;
    }

    public JTextField getTextFieldUserName() {
        return textFieldUserName;
    }

    public void setTextFieldUserName(JTextField textFieldUserName) {
        this.textFieldUserName = textFieldUserName;
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public void setBtnLogin(JButton btnLogin) {
        this.btnLogin = btnLogin;
    }

    public JLabel getLblTitle() {
        return lblTitle;
    }

    public void setLblTitle(JLabel lblTitle) {
        this.lblTitle = lblTitle;
    }

    public JLabel getLblTeamName() {
        return lblTeamName;
    }

    public void setLblTeamName(JLabel lblTeamName) {
        this.lblTeamName = lblTeamName;
    }
}
