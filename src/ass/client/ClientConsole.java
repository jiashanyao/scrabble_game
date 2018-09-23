package ass.client;

import ass.communication.ClientMessage;
import ass.communication.GameContext;
import ass.communication.JsonUtility;
import ass.communication.ServerMessage;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;


/**
 * Created by hugh on 18/9/18.
 */
public class ClientConsole extends JFrame {
    static final Integer BOARD_SIZE = 20;

    private String[][] plainBoard;
    private JTable playerTable;
    private JTable gameTable;
    private Socket socket;
    private BufferedWriter writer;
    private ClientContext context;
    private String userId;
    private GameContext gameContext;


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                try {
                    String url = args[0];
                    Integer port = Integer.parseInt(args[1]);
                    String username = args[2];
                    ClientConsole gt = new ClientConsole(url, port, username);
                    gt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    gt.pack();
                    gt.setVisible(true);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private static String[][] createGameBoardModel() {
        String[][] gameboard = new String[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                gameboard[i][j] = "";
            }
        }
        return gameboard;
    }

    public ClientConsole(String url, Integer port, String username) throws IOException {

        this.plainBoard = createGameBoardModel();
        this.socket = new Socket(url, port);
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        this.context = new ClientContext();
        ContextListener listener = new ContextListener(new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8")), this.context);

        this.userId = username;
        // pass username to server
        ClientMessage cm = new ClientMessage();
        cm.setType(ClientMessage.Type.SYNC);
        cm.setUserId(this.userId);
        this.writer.write(JsonUtility.toJson(cm) + "\n");
        this.writer.flush();

        setTitle("Scrabble Game");
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths =
            new int[] {30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30};
        gridBagLayout.rowHeights = new int[] {30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 0, 30};
        gridBagLayout.columnWeights =
            new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
                1.0, 1.0};
        gridBagLayout.rowWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0};
        getContentPane().setLayout(gridBagLayout);

        /*
         * Message Area
         */
        JLabel lblMessageArea = new JLabel("Welcome to Scrbble Game");
        lblMessageArea.setFont(new Font("SimSun", Font.PLAIN, 18));
        GridBagConstraints gbc_lblMessageArea = new GridBagConstraints();
        gbc_lblMessageArea.gridwidth = 20;
        gbc_lblMessageArea.anchor = GridBagConstraints.WEST;
        gbc_lblMessageArea.insets = new Insets(0, 0, 5, 5);
        gbc_lblMessageArea.gridx = 1;
        gbc_lblMessageArea.gridy = 1;
        getContentPane().add(lblMessageArea, gbc_lblMessageArea);


        /*
         * Game Area
         */
        gameTable = new JTable();
        gameTable.setEnabled(false);
        gameTable.setBackground(new Color(255, 255, 204));
        gameTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        gameTable.setRowSelectionAllowed(false);
        gameTable.setFont(new Font("Arial", Font.PLAIN, 18));
        GridBagConstraints gbc_gameTable = new GridBagConstraints();
        gbc_gameTable.fill = GridBagConstraints.BOTH;
        gbc_gameTable.gridwidth = 20;
        gbc_gameTable.gridx = 1;
        gbc_gameTable.gridheight = 20;
        gbc_gameTable.gridy = 2;
        gameTable.setTableHeader(null);
        gameTable.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        gameTable.setGridColor(Color.GRAY);
        gameTable.setMaximumSize(new Dimension(600, 600));
        String[] gameColumnNames = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};

        gameTable.setModel(new DefaultTableModel(plainBoard, gameColumnNames));
        TableColumnModel gameColumModle = gameTable.getColumnModel();

        gameTable.setRowHeight(30);
        for (int i = 0; i < gameTable.getColumnCount(); i++) {
            gameColumModle.getColumn(i).setMaxWidth(30);
            gameColumModle.getColumn(i).setMinWidth(30);


        }


        JScrollPane gameScrollPane = new JScrollPane(gameTable);
        GridBagConstraints gbc_gameScrollPane = new GridBagConstraints();
        gbc_gameScrollPane.fill = GridBagConstraints.BOTH;
        gbc_gameScrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_gameScrollPane.gridwidth = 20;
        gbc_gameScrollPane.gridx = 1;
        gbc_gameScrollPane.gridheight = 20;
        gbc_gameScrollPane.gridy = 2;
        getContentPane().add(gameScrollPane, gbc_gameScrollPane);
        gameScrollPane.setBorder(BorderFactory.createEmptyBorder());
        gameScrollPane.setPreferredSize(new Dimension(600, 600));
        gameScrollPane.setMaximumSize(new Dimension(600, 600));
        gameScrollPane.setMinimumSize(new Dimension(600, 600));

        DefaultTableCellRenderer gameCellRenderer = new DefaultTableCellRenderer();
        gameCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < gameTable.getColumnCount(); i++) {
            gameTable.getColumnModel().getColumn(i).setCellRenderer(gameCellRenderer);
            gameTable.getColumnModel().getColumn(i).setMinWidth(30);
            gameTable.getColumnModel().getColumn(i).setMaxWidth(30);
        }

        /*
         * Player Info Area
         * 1) idle player list
         * 2) playing player list
         */
        JLabel lblIdlePlayers = new JLabel("Idle Players");
        lblIdlePlayers.setHorizontalAlignment(SwingConstants.LEFT);
        lblIdlePlayers.setFont(new Font("SimSun", Font.PLAIN, 18));
        GridBagConstraints gbc_lblIdlePlayers = new GridBagConstraints();
        gbc_lblIdlePlayers.fill = GridBagConstraints.VERTICAL;
        gbc_lblIdlePlayers.gridwidth = 5;
        gbc_lblIdlePlayers.anchor = GridBagConstraints.WEST;
        gbc_lblIdlePlayers.insets = new Insets(0, 0, 5, 5);
        gbc_lblIdlePlayers.gridx = 22;
        gbc_lblIdlePlayers.gridy = 1;
        getContentPane().add(lblIdlePlayers, gbc_lblIdlePlayers);

        JList<String> idlePlayerList = new JList<String>();
        idlePlayerList.setFont(new Font("SimSun", Font.PLAIN, 14));
        GridBagConstraints gbc_idelPlayerList = new GridBagConstraints();
        gbc_idelPlayerList.gridheight = 8;
        gbc_idelPlayerList.gridwidth = 11;
        gbc_idelPlayerList.insets = new Insets(0, 0, 5, 5);
        gbc_idelPlayerList.fill = GridBagConstraints.BOTH;
        gbc_idelPlayerList.gridx = 23;
        gbc_idelPlayerList.gridy = 2;
        idlePlayerList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        //TODO: change to get data from response
        DefaultListModel<String> listModel = new DefaultListModel<String>();
        listModel.addElement("Jane Doe (invited)");
        listModel.addElement("John Smith (invited)");
        listModel.addElement("Kathy Green");
        idlePlayerList.setModel(listModel);

        JScrollPane listScrollPane = new JScrollPane(idlePlayerList);
        GridBagConstraints gbc_listScrollPane = new GridBagConstraints();
        gbc_listScrollPane.gridwidth = 11;
        gbc_listScrollPane.gridheight = 8;
        gbc_listScrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_listScrollPane.fill = GridBagConstraints.BOTH;
        gbc_listScrollPane.gridx = 22;
        gbc_listScrollPane.gridy = 2;
        getContentPane().add(listScrollPane, gbc_listScrollPane);

        JLabel lblPlayingPlayers = new JLabel("Playing Players");
        lblPlayingPlayers.setFont(new Font("SimSun", Font.PLAIN, 18));
        GridBagConstraints gbc_lblPlayingPlayers = new GridBagConstraints();
        gbc_lblPlayingPlayers.gridwidth = 5;
        gbc_lblPlayingPlayers.anchor = GridBagConstraints.WEST;
        gbc_lblPlayingPlayers.insets = new Insets(0, 0, 5, 5);
        gbc_lblPlayingPlayers.gridx = 22;
        gbc_lblPlayingPlayers.gridy = 12;
        getContentPane().add(lblPlayingPlayers, gbc_lblPlayingPlayers);

        playerTable = new JTable();
        playerTable.setFont(new Font("SimSun", Font.PLAIN, 14));
        playerTable.setEnabled(false);
        //        playerTable.setRowHeight(20);
        GridBagConstraints gbc_playerTable = new GridBagConstraints();
        gbc_playerTable.fill = GridBagConstraints.BOTH;
        gbc_playerTable.gridwidth = 11;
        gbc_playerTable.gridx = 22;
        gbc_playerTable.gridheight = 8;
        gbc_playerTable.gridy = 13;
        gbc_playerTable.insets = new Insets(0, 0, 5, 5);

        String[] plColumnNames = {"User Name", "Status", "Score"};

        //TODO change to listener
        Object[][] plData =
            {{"Kathy", "Playing", new Integer(10)}, {"Snowboarding", "", new Integer(5)}, {"John", "", new Integer(4)}, {"Doe", "", new Integer(2)}, {"Joe", "", new Integer(10)}};

        DefaultTableModel dm = new DefaultTableModel(plData, plColumnNames);
        playerTable.setModel(dm);

        JScrollPane tableScrollPane = new JScrollPane(playerTable);
        GridBagConstraints gbc_tableScrollPane = new GridBagConstraints();
        gbc_tableScrollPane.fill = GridBagConstraints.BOTH;
        gbc_tableScrollPane.gridx = 22;
        gbc_tableScrollPane.gridwidth = 11;
        gbc_tableScrollPane.gridy = 13;
        gbc_tableScrollPane.gridheight = 8;
        gbc_tableScrollPane.insets = new Insets(0, 0, 5, 5);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.setPreferredSize(new Dimension(50, 50));
        getContentPane().add(tableScrollPane, gbc_tableScrollPane);

        /*
         * Game Button Area
         */

        JButton btnInvite = new JButton("Invite");
        btnInvite.setFont(new Font("SimSun", Font.PLAIN, 18));
        GridBagConstraints gbc_btnInvite = new GridBagConstraints();
        gbc_btnInvite.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnInvite.gridwidth = 5;
        gbc_btnInvite.insets = new Insets(0, 0, 5, 5);
        gbc_btnInvite.gridx = 22;
        gbc_btnInvite.gridy = 10;
        getContentPane().add(btnInvite, gbc_btnInvite);

        JButton btnStartGame = new JButton("Start Game");
        btnStartGame.setFont(new Font("SimSun", Font.PLAIN, 18));
        GridBagConstraints gbc_btnStartGame = new GridBagConstraints();
        gbc_btnStartGame.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnStartGame.gridwidth = 5;
        gbc_btnStartGame.insets = new Insets(0, 0, 5, 5);
        gbc_btnStartGame.gridx = 28;
        gbc_btnStartGame.gridy = 10;
        getContentPane().add(btnStartGame, gbc_btnStartGame);

        JButton btnPass = new JButton("Pass");
        btnPass.setFont(new Font("SimSun", Font.PLAIN, 18));
        GridBagConstraints gbc_btnPass = new GridBagConstraints();
        gbc_btnPass.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnPass.gridwidth = 5;
        gbc_btnPass.insets = new Insets(0, 0, 5, 5);
        gbc_btnPass.gridx = 22;
        gbc_btnPass.gridy = 21;
        getContentPane().add(btnPass, gbc_btnPass);

        JButton btnEndGame = new JButton("End Game");
        btnEndGame.setFont(new Font("SimSun", Font.PLAIN, 18));
        GridBagConstraints gbc_btnEndGame = new GridBagConstraints();
        gbc_btnEndGame.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnEndGame.gridwidth = 5;
        gbc_btnEndGame.insets = new Insets(0, 0, 5, 5);
        gbc_btnEndGame.gridx = 28;
        gbc_btnEndGame.gridy = 21;
        getContentPane().add(btnEndGame, gbc_btnEndGame);


        /**
         * Action Listeners
         */

        btnInvite.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (idlePlayerList.isSelectionEmpty()) {
                    lblMessageArea.setText(Dictionary.MORE_THAN_ONE_PLAYER);
                    lblMessageArea.setForeground(Color.RED);
                } else {
                    // ClientMessage setting
                    String[] selectedIdlePlayers = idlePlayerList.getSelectedValuesList().stream().toArray(String[]::new);
                    for (String item : selectedIdlePlayers) {
                        item = item.replaceAll("\\s\\(.*", "");
                    }
                    ClientMessage cm = new ClientMessage();
                    cm.setType(ClientMessage.Type.INVITATION);
                    cm.setUserId(username);
                    cm.setInvitations(selectedIdlePlayers);

                    try {
                        writer.write(JsonUtility.toJson(cm) + "\n");
                        writer.flush();
                    } catch (IOException e1) {
                        lblMessageArea.setText(e1.getMessage());
                        lblMessageArea.setForeground(Color.RED);
                        e1.printStackTrace();
                    }
                }
            }
        });

        btnStartGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ClientMessage setting
                ClientMessage cm = new ClientMessage();
                cm.setType(ClientMessage.Type.START);
                cm.setUserId(username);
                try {
                    writer.write(JsonUtility.toJson(cm) + "\n");
                    writer.flush();
                } catch (IOException e1) {
                    lblMessageArea.setText(e1.getMessage());
                    lblMessageArea.setForeground(Color.RED);
                    e1.printStackTrace();
                }
            }
        });

        btnPass.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ClientMessage setting
                ClientMessage cm = new ClientMessage();
                cm.setType(ClientMessage.Type.PASS);
                cm.setUserId(username);
                try {
                    writer.write(JsonUtility.toJson(cm) + "\n");
                    writer.flush();
                } catch (IOException e1) {
                    lblMessageArea.setText(e1.getMessage());
                    lblMessageArea.setForeground(Color.RED);
                    e1.printStackTrace();
                }
            }
        });

        btnEndGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ClientMessage setting
                ClientMessage cm = new ClientMessage();
                cm.setType(ClientMessage.Type.END);
                cm.setUserId(username);
                try {
                    writer.write(JsonUtility.toJson(cm) + "\n");
                    writer.flush();
                } catch (IOException e1) {
                    lblMessageArea.setText(e1.getMessage());
                    lblMessageArea.setForeground(Color.RED);
                    e1.printStackTrace();
                }
            }
        });

        gameTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent me) {
                int x = gameTable.rowAtPoint(me.getPoint());
                int y = gameTable.columnAtPoint(me.getPoint());
                System.out.println(x + "," + y);
                String cellValue = (String) gameTable.getModel().getValueAt(x, y);

                //TODO: when game_Stauts is playing && userID is currentuser && grid is empty, popup chooseCharcter
                if (!cellValue.trim().isEmpty()) {
                    lblMessageArea.setText(Dictionary.CHS_EMPTY_GRID);
                    lblMessageArea.setForeground(Color.RED);
                } else {
                    // Components setting
                    lblMessageArea.setText("");
                    lblMessageArea.setForeground(Color.BLACK);

                    // display 'Choose Character' dialog
                    Object[] options = new Object[26];
                    for (int i = 0; i < 26; i++) {
                        options[i] = (char) ((int) 'A' + i);
                    }
                    int selectedBotton = JOptionPane
                        .showOptionDialog(null, Dictionary.CHS_CHAR, Dictionary.CHS_CHAR_TITLE, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options,
                            options[0]);

                    // Choose a character
                    if (selectedBotton != -1) {
                        char inputChar = (char) ((int) 'A' + selectedBotton);
                        gameTable.getModel().setValueAt(inputChar, x, y);
                        // ClientMessage setting
                        ClientMessage cm = new ClientMessage();
                        cm.setType(ClientMessage.Type.CHARACTER);
                        cm.setUserId(username);
                        try {
                            writer.write(JsonUtility.toJson(cm) + "\n");
                            writer.flush();
                        } catch (IOException e1) {
                            lblMessageArea.setText(e1.getMessage());
                            lblMessageArea.setForeground(Color.RED);
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

        // start to listen
        listener.start();
        BackgroundThread backgroundThread = new BackgroundThread() {
            @Override public void run() {
                while (true) {
                    if (!context.isEmpty()) {
                        try {
                            ServerMessage headMessage = context.take();
                            Date newVersion = new Date(headMessage.getTime());
                            ServerMessage.Type type = headMessage.getType();
                            switch (type) {
                                case INFORMATION:
                                    //update pane
                                    if (context.getCurrentVersion().before(newVersion) && null != headMessage.getGameContext()) {
                                        gameContext = headMessage.getGameContext();
                                        GameContext.GameStatus status = gameContext.getGameStatus();
                                        switch (status) {
                                            case IDLING:
                                                gameTable.setModel(new DefaultTableModel(plainBoard, gameColumnNames));
                                                gameTable.setEnabled(false);
                                                listModel.clear();

                                                java.util.List<String> invitedUsers = Arrays.asList(gameContext.getInvitedUser());
                                                for (String user : gameContext.getIdleUsers()) {
                                                    listModel.addElement(invitedUsers.contains(user) ? user + " (invited)" : user);
                                                }
                                                idlePlayerList.setModel(listModel);

                                                for (String user : gameContext.getIdleUsers()) {
                                                    listModel.addElement(user);
                                                }
                                                playerTable.setModel(new DefaultTableModel(new Object[][] {}, plColumnNames));

                                                btnStartGame.setEnabled(true);
                                                btnPass.setEnabled(false);
                                                btnEndGame.setEnabled(false);
                                                break;
                                            case GAMING:
                                                String currentPlayer = gameContext.getCurrentUser();
                                                gameTable.setModel(new DefaultTableModel(gameContext.getGameBoard(), gameColumnNames));
                                                java.util.List<String> players = Arrays.asList(gameContext.getGamingUsers());
                                                if (players.contains(userId) && userId.equals(currentPlayer)) {
                                                    //TODO probably highlighting
                                                    gameTable.setEnabled(true);
                                                } else {
                                                    gameTable.setEnabled(false);
                                                }

                                                for (String user : gameContext.getIdleUsers()) {
                                                    listModel.addElement(user);
                                                }
                                                Object[][] playerModole = new Object[gameContext.getScores().size()][3];
                                                int index = 0;
                                                for (Map.Entry<String, Integer> player : gameContext.getScores().entrySet()) {
                                                    playerModole[index][0] = player.getKey();
                                                    playerModole[index][1] = player.getKey().equals(currentPlayer) ? "playing" : "";
                                                    playerModole[index][2] = player.getValue();

                                                }
                                                playerTable.setModel(new DefaultTableModel(playerModole, plColumnNames));
                                                btnStartGame.setEnabled(false);
                                                btnPass.setEnabled(true);
                                                btnEndGame.setEnabled(true);
                                                break;
                                            case INVITING:
                                                break;
                                            case VOTING:
                                                break;
                                            default:
                                                throw new IllegalArgumentException("Game status miss match");

                                        }
                                        context.setCurrentVersion(newVersion);
                                    }
                                    break;
                                case REQUEST:
                                    // require response
                                    Date expiredTime = new Date(headMessage.getExpiredTime());
                                    if (expiredTime.after(new Date())) {
                                        JOptionPane.showConfirmDialog(null, headMessage.getMessage() + "?");
                                    }
                                    break;
                            }

                        } catch (InterruptedException e) {
                            //TODO handle blocking
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }



                }
            }

        };
        backgroundThread.start();
    }
}
