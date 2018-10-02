package ass.client;

import ass.client.highlight.HighlightListener;
import ass.client.highlight.HighlightRender;
import ass.communication.ClientMessage;
import ass.communication.GameContext;
import ass.communication.GameContext.GameStatus;
import ass.communication.JsonUtility;
import ass.communication.ServerMessage;
import org.apache.commons.lang3.StringUtils;

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
import java.util.*;
import java.util.List;


/**
 * Created by hugh on 18/9/18.
 */
public class ClientConsole extends JFrame {

    public static final Integer BOARD_SIZE = 20;

    public static final Map<GameContext.GameStatus, ClientMessage.Type> RESPONSE_MAP;

    public static final DefaultTableCellRenderer GAME_CELL_RENDER;

    static {
        Map<GameContext.GameStatus, ClientMessage.Type> map = new HashMap<>();
        map.put(GameContext.GameStatus.INVITING, ClientMessage.Type.INVITATION_CONFIRM);
        map.put(GameContext.GameStatus.HIGHLIGHT, ClientMessage.Type.HIGHLIGHT);
        map.put(GameContext.GameStatus.VOTING, ClientMessage.Type.VOTE);
        RESPONSE_MAP = Collections.unmodifiableMap(map);

        GAME_CELL_RENDER = new DefaultTableCellRenderer();
        GAME_CELL_RENDER.setHorizontalAlignment(SwingConstants.CENTER);

    }

    private String[][] plainBoard;
    private JTable playerTable;
    private JTable gameTable;
    private HighlightListener highlightListener;
    private Socket socket;
    private BufferedWriter writer;
    private ClientContext context;
    private BroadcastMessage broadcastMessage;
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

    public static void setTableRender(JTable gameTable) {
        for (int i = 0; i < gameTable.getColumnCount(); i++) {
            gameTable.getColumnModel().getColumn(i).setCellRenderer(GAME_CELL_RENDER);
            gameTable.getColumnModel().getColumn(i).setMinWidth(30);
            gameTable.getColumnModel().getColumn(i).setMaxWidth(30);
        }
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
        this.broadcastMessage = new BroadcastMessage();
        ContextListener listener = new ContextListener(new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8")), this.context, this.broadcastMessage);

        this.userId = username;
        // pass username to server
        ClientMessage cm = new ClientMessage();
        cm.setType(ClientMessage.Type.SYNC);
        cm.setUserId(this.userId);
        this.writer.write(JsonUtility.toJson(cm) + "\n");
        this.writer.flush();

        setTitle("Scrabble Game:" + userId);
        System.out.println("Scrabble Game:" + userId);
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
        JLabel lblMessageArea = new JLabel(Dictionary.MSG_WELCOME);
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
        JLabel lblIdlePlayers = new JLabel(Dictionary.LBL_IDLE_PLY);
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

        idlePlayerList.setModel(new DefaultListModel<String>());

        JScrollPane listScrollPane = new JScrollPane(idlePlayerList);
        GridBagConstraints gbc_listScrollPane = new GridBagConstraints();
        gbc_listScrollPane.gridwidth = 11;
        gbc_listScrollPane.gridheight = 8;
        gbc_listScrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_listScrollPane.fill = GridBagConstraints.BOTH;
        gbc_listScrollPane.gridx = 22;
        gbc_listScrollPane.gridy = 2;
        getContentPane().add(listScrollPane, gbc_listScrollPane);

        JLabel lblPlayingPlayers = new JLabel(Dictionary.LBL_PLY_PLY);
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

        Object[][] plData = null;
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
                // Clear MessageArea
                lblMessageArea.setText("");

                if (idlePlayerList.isSelectionEmpty()) {
                    lblMessageArea.setText(Dictionary.MORE_THAN_ONE_PLAYER);
                    lblMessageArea.setForeground(Color.RED);
                } else {
                    String[] selectedIdlePlayers = idlePlayerList.getSelectedValuesList().stream().toArray(String[]::new);
                    for (String item : selectedIdlePlayers) {
                        item = item.replaceAll("\\s\\(.*", "");
                    }
                    // validation: cannot invite self
                    List<String> list = Arrays.asList(selectedIdlePlayers);
                    if (list.contains(username)) {
                        lblMessageArea.setText(Dictionary.NOT_INVITE_SELF);
                        lblMessageArea.setForeground(Color.RED);
                    } else {
                        // ClientMessage setting
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
            }
        });

        btnStartGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Clear MessageArea
                lblMessageArea.setText("");

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
                // Clear MessageArea
                lblMessageArea.setText("");

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
                // Clear MessageArea
                lblMessageArea.setText("");

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
                // Clear MessageArea
                lblMessageArea.setText("");

                // when gameTable is enable, pop-up choose character dialog
                if (gameTable.isEnabled()) {
                    int x = gameTable.rowAtPoint(me.getPoint());
                    int y = gameTable.columnAtPoint(me.getPoint());
                    System.out.println(x + "," + y);
                    String cellValue = (String) gameTable.getModel().getValueAt(x, y);

                    //TODO: when game_Stauts is playing && userID is currentuser && grid is empty, popup chooseCharcter
                    if (StringUtils.isNotBlank(cellValue)) {
                        lblMessageArea.setText(Dictionary.CHS_EMPTY_GRID);
                        lblMessageArea.setForeground(Color.RED);
                    } else {
                        // Components setting
                        lblMessageArea.setText("");
                        lblMessageArea.setForeground(Color.BLACK);

                        // display 'Choose Character' dialog
                        Object[] options = new Object[26];
                        for (int i = 0; i < 26; i++) {
                            options[i] = (char) (65 + i);
                        }
                        int selectedBotton = JOptionPane
                            .showOptionDialog(null, Dictionary.CHS_CHAR, Dictionary.CHS_CHAR_TITLE, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options,
                                options[0]);

                        // Choose a character
                        if (selectedBotton != -1) {
                            String inputChar = (char) (65 + selectedBotton) + "";
                            gameTable.getModel().setValueAt(inputChar, x, y);
                            // ClientMessage setting
                            ClientMessage cm = new ClientMessage();
                            cm.setType(ClientMessage.Type.CHARACTER);
                            cm.setUserId(username);
                            cm.setCellChar(inputChar);
                            cm.setCellX(x);
                            cm.setCellY(y);
                            try {
                                writer.write(JsonUtility.toJson(cm) + "\n");
                                writer.flush();
                                gameTable.setEnabled(false);
                            } catch (IOException e1) {
                                lblMessageArea.setText(e1.getMessage());
                                lblMessageArea.setForeground(Color.RED);
                                e1.printStackTrace();
                            }
                        }
                    }
                } else if (null != gameContext && GameContext.GameStatus.HIGHLIGHT.equals(gameContext.getGameStatus())
                        && username.equals(gameContext.getCurrentUser())) {
                    //if gameTable is disable and the game status is highlight, click highlight box send message

                    //Components setting
                    lblMessageArea.setText("");
                    lblMessageArea.setForeground(Color.BLACK);

                    // information preparation
                    int char_row = gameContext.getCellX();
                    int char_col = gameContext.getCellY();
                    int x = gameTable.rowAtPoint(me.getPoint());
                    int y = gameTable.columnAtPoint(me.getPoint());
                    int[][] highlightRange = null;
                    int[] highlighRangeCols = null;
                    int[] highlighRangeRols = null;
                    String[] highlighStr = null;

                    if (x == char_row || y == char_col) {

                        highlightRange = HighlightRender.selectHighlightRange(gameTable, char_row, char_col, x, y);
                        highlighStr = getHighlightString(highlightRange[0], highlightRange[1], char_row, char_col);

                        // ClientMessage setting
                        ClientMessage cm = new ClientMessage();
                        cm.setType(ClientMessage.Type.HIGHLIGHT);
                        cm.setUserId(username);
                        cm.setCellX(char_row);
                        cm.setCellY(char_col);
                        cm.setHighLight(highlighStr);
                        try {
                            writer.write(JsonUtility.toJson(cm) + "\n");
                            writer.flush();
                        } catch (IOException e1) {
                            lblMessageArea.setText(e1.getMessage());
                            lblMessageArea.setForeground(Color.RED);
                            e1.printStackTrace();
                        } finally {
                            //remove motion listener
                            highlightListener.turnOff();
                        }
                    }
                }
            }

        });
        gameTable.setEnabled(false);
        btnInvite.setEnabled(true);
        btnStartGame.setEnabled(false);
        btnPass.setEnabled(false);
        btnEndGame.setEnabled(false);

        // start to listen
        listener.start();
        
        /* Create and start a thread dedicated for processing broadcast messages */
        Thread broadcastHandling = new Thread() {
            
            @Override
            public void run() {
                while (true) {
                    try {
                        ServerMessage broadcast = broadcastMessage.take();
                        System.out.println("Broadcast thread is used!");
                        lblMessageArea.setText(broadcast.getMessage());
                        GameContext invitingContext = broadcast.getGameContext();
                        //update idle users
                        //TODO monitor here
                        System.out.println("Renew listModel");
                        DefaultListModel<String> listModel = new DefaultListModel<String>();
                        List<String> invitedUsers =
                            null != invitingContext && null != invitingContext.getInvitedUser() ? invitingContext.getInvitedUser() : new ArrayList<>();
                        for (String user : broadcast.getIdleUsers()) {
                            listModel.addElement(invitedUsers.contains(user) ? user + " (invited)" : user);
                        }
                        idlePlayerList.setModel(listModel);
                    } catch (InterruptedException e) {
                        //TODO: handle exception
                        e.printStackTrace();
                    }
                }
            }
        };
        broadcastHandling.start();
        
        BackgroundThread backgroundThread = new BackgroundThread() {
            @Override public void run() {
                while (true) {
                    try {
                        ServerMessage headMessage = context.take();
                        if (null != headMessage) {
                            Long newVersion = headMessage.getTime();
                            ServerMessage.Type type = headMessage.getType();
                            lblMessageArea.setText(headMessage.getMessage());
                            //update pane
                            if (ServerMessage.Type.ERROR.equals(type)) {
                                JOptionPane.showMessageDialog(null, headMessage.getMessage());
                                System.exit(0);
                            } else {
                                // gaming status and inviting status
                                //update global variable
                                gameContext = headMessage.getGameContext();
                                String currentPlayer = gameContext.getCurrentUser();
                                //update game board
                                GameContext.GameStatus status = gameContext.getGameStatus();
                                String[][] gameBoard = gameContext.getGameBoard();

                                for (int i = 0; i < BOARD_SIZE; i++) {
                                    for (int j = 0; j < BOARD_SIZE; j++) {
                                        String value = null != gameBoard ? gameBoard[i][j] : "";
                                        gameTable.getModel().setValueAt(value, i, j);
                                    }
                                }
                                setTableRender(gameTable);

                                //update players table
                                Object[][] playerModel;
                                List<String> gamingUsers = gameContext.getGamingUsers();
                                if (gamingUsers.contains(userId)) {
                                    Map<String, Integer> scoresMap = null != gameContext.getScores() ? gameContext.getScores() : new HashMap<String, Integer>();
                                    if (null != gamingUsers && gamingUsers.size() > 0) {

                                        int index = 0;
                                        playerModel = new Object[gamingUsers.size()][3];
                                        // System.out.println("Gaming size:" + gamingUsers.size());
                                        for (String userId : gamingUsers) {
                                            playerModel[index][0] = userId;
                                            playerModel[index][1] = userId.equals(currentPlayer) ? "playing" : "";
                                            playerModel[index][2] = null == scoresMap.get(userId) ? "" : scoresMap.get(userId);
                                            index++;
                                        }
                                    } else {
                                        playerModel = new Object[][] {};
                                    }
                                    playerTable.setModel(
                                            new DefaultTableModel(playerModel, plColumnNames));
                                }
                                if (status == GameStatus.IDLING) {  // empty player table
                                    playerTable.setModel(
                                            new DefaultTableModel());
                                }

                                //update button status
                                switch (status) {
                                    case IDLING:
                                        gameTable.setEnabled(false);
                                        btnInvite.setEnabled(true);
                                        btnStartGame.setEnabled(false);
                                        btnPass.setEnabled(false);
                                        btnEndGame.setEnabled(false);
                                        break;
                                    case INVITING:
                                        gameTable.setEnabled(false);
                                        btnInvite.setEnabled(userId.equals(currentPlayer));
                                        btnStartGame.setEnabled(userId.equals(currentPlayer) && gameContext.getGamingUsers().size() > 1);
                                        btnPass.setEnabled(false);
                                        btnEndGame.setEnabled(false);
                                        break;
                                    case GAMING:
                                        if (userId.equals(currentPlayer)) {
                                            gameTable.setEnabled(true);
                                            btnPass.setEnabled(true);
                                        } else {
                                            gameTable.setEnabled(false);
                                            btnPass.setEnabled(false);
                                        }
                                        btnInvite.setEnabled(false);
                                        btnStartGame.setEnabled(false);
                                        btnEndGame.setEnabled(true);
                                        break;
                                    case HIGHLIGHT:
                                        gameTable.setEnabled(false);
                                        btnInvite.setEnabled(false);
                                        btnStartGame.setEnabled(false);
                                        if (userId.equals(currentPlayer)) {
                                            btnPass.setEnabled(true);
                                        } else {
                                            btnPass.setEnabled(false);
                                        }
                                        btnEndGame.setEnabled(true);
                                        break;
                                    case VOTING:
                                        gameTable.setEnabled(false);
                                        btnInvite.setEnabled(false);
                                        btnStartGame.setEnabled(false);
                                        if (userId.equals(currentPlayer)) {
                                            btnPass.setEnabled(true);
                                        } else {
                                            btnPass.setEnabled(false);
                                        }
                                        btnEndGame.setEnabled(true);
                                        HighlightRender.displayHighlightString(gameTable, gameContext.getCellX(), gameContext.getCellY(), gameContext.getHighLight());
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Game status miss match");
                                }

                                // require response
                                if (ServerMessage.Type.REQUEST.equals(type)) {
                                    Date expiredTime = new Date(headMessage.getExpiredTime());
                                    if (expiredTime.after(new Date())) {
                                        try {
                                            //                                                GameContext.GameStatus status = null != gameContext ? gameContext.getGameStatus() : GameContext.GameStatus.INVITING;
                                            ClientMessage.Type responseType = null == RESPONSE_MAP.get(status) ? ClientMessage.Type.SYNC : RESPONSE_MAP.get(status);
                                            ClientMessage clientMessage = new ClientMessage();
                                            clientMessage.setUserId(userId);
                                            clientMessage.setType(responseType);
                                            int dialogResult = JOptionPane.showConfirmDialog(null, headMessage.getMessage());

                                            if (JOptionPane.YES_OPTION == dialogResult) {

                                                switch (status) {
                                                    case HIGHLIGHT:
                                                        highlightListener = new HighlightListener(gameTable, gameContext.getCellX(), gameContext.getCellY());
                                                        gameTable.addMouseMotionListener(highlightListener);
                                                        break;
                                                    case INVITING:
                                                    case VOTING:
                                                        clientMessage.setHighLight(gameContext.getHighLight());
                                                        clientMessage.setAccept(true);
                                                        writer.write(JsonUtility.toJson(clientMessage) + "\n");
                                                        writer.flush();
                                                        break;
                                                    default:
                                                        JOptionPane.showMessageDialog(null, "Unexpected action...");
                                                        break;
                                                }

                                            } else {
                                                switch (status) {
                                                    case HIGHLIGHT:
                                                        clientMessage.setHighLight(new String[] {"", ""});
                                                        break;
                                                }
                                                clientMessage.setAccept(false);
                                                writer.write(JsonUtility.toJson(clientMessage) + "\n");
                                                writer.flush();
                                            }

                                        } catch (IOException e) {
                                            //TODO handle
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            context.setCurrentVersion(newVersion);
                        }

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

        };
        backgroundThread.start();
    }

    /**
     * get the highlight string according to axis of character
     * if just one character, it will be set as row.
     *
     * @param cellsOfRow
     * @param cellsOfCol
     * @param char_row
     * @param char_col
     * @return
     */
    private String[] getHighlightString(int[] cellsOfRow, int[] cellsOfCol, int char_row, int char_col) {
        String[] result = new String[2];
        // same row
        if (cellsOfRow != null) {
            String rowStr = "";
            for (int i = cellsOfRow[0]; i <= cellsOfRow[1]; i++) {
                rowStr = rowStr + (String) gameTable.getModel().getValueAt(char_row, i);
            }
            result[0] = rowStr;
        } else {
            result[0] = "";
        }
        // same column
        if (cellsOfCol != null) {
            String colStr = "";
            for (int i = cellsOfCol[0]; i <= cellsOfCol[1]; i++) {
                colStr = colStr + (String) gameTable.getModel().getValueAt(i, char_col);
            }
            result[1] = colStr;
        } else {
            result[1] = "";
        }
        return result;
    }
}
