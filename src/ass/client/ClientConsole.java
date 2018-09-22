package ass.client;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by hugh on 18/9/18.
 */
public class ClientConsole extends JFrame {
	private JTable playerTable;
	private JTable gameTable;
	private JTable table;

    public static void main(String[] args) {
        ClientConsole gt = new ClientConsole();
        gt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gt.pack();
        gt.setVisible(true);
    }

    public ClientConsole() {
    	setTitle("Scrabble Game");
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30};
        gridBagLayout.rowHeights = new int[]{30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 0, 30};
        gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
        gridBagLayout.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0};
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
        gameTable.setMaximumSize(new Dimension(600,600));
        String[] gameColumnNames = {
        	"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"
        };
        
        Object[][] gameData = {
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
        	    {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""}
                };
        
        DefaultTableModel gameDm = new DefaultTableModel(gameData,gameColumnNames);
        gameTable.setModel(gameDm);
        TableColumnModel gameColumModle = gameTable.getColumnModel();
        
        gameTable.setRowHeight(30);
        for (int i = 0; i<gameTable.getColumnCount();i++) {
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
        gameCellRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for(int i=0; i<gameTable.getColumnCount();i++) {
            gameTable.getColumnModel().getColumn(i).setCellRenderer(gameCellRenderer);
            gameTable.getColumnModel().getColumn(i).setMinWidth(30);
            gameTable.getColumnModel().getColumn(i).setMaxWidth(30);
        }
        
        
//        Container pane = getContentPane();
//      for (int i = 0; i < 20; i++) {
//    	for (int j = 0; j < 20; j++) {
//    		JButton button = new JButton(Integer.toString(i + 1));
//    		panel.add(button);
//    	}
//    	
//    }
        
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
	
		String[] plColumnNames = {
			"User Name",
			"Status",
			"Score"
		};
		
        //TODO change to listener
		Object[][] plData = {
    	    {"Kathy", "Playing", new Integer(10)},
    	    {"Snowboarding", "", new Integer(5)},
    	    {"John", "", new Integer(4)},
    	    {"Doe", "", new Integer(2)},
    	    {"Joe", "", new Integer(10)}
		};
			
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
        btnEndGame.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });
        GridBagConstraints gbc_btnEndGame = new GridBagConstraints();
        gbc_btnEndGame.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnEndGame.gridwidth = 5;
        gbc_btnEndGame.insets = new Insets(0, 0, 5, 5);
        gbc_btnEndGame.gridx = 28;
        gbc_btnEndGame.gridy = 21;
        getContentPane().add(btnEndGame, gbc_btnEndGame);
        
        btnStartGame.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });
    }
}

