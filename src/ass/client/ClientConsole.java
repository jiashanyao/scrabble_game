package ass.client;

import javax.swing.*;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by hugh on 18/9/18.
 */
public class ClientConsole extends JFrame {
	private JTable table;

    public static void main(String[] args) {
//        int rows = 2;
//        int cols = 3;
        ClientConsole gt = new ClientConsole();
        gt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gt.pack();
        gt.setVisible(true);
    }

    public ClientConsole() {
    	setTitle("Scrabble Game");
        
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {30, 30, 0, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30};
        gridBagLayout.rowHeights = new int[]{30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30};
        gridBagLayout.columnWeights = new double[]{Double.MIN_VALUE, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
        gridBagLayout.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
        getContentPane().setLayout(gridBagLayout);
        
        JLabel lblWelcomeToScrbble = new JLabel("Welcome to Scrbble Game");
        lblWelcomeToScrbble.setFont(new Font("Arial", Font.PLAIN, 18));
        GridBagConstraints gbc_lblWelcomeToScrbble = new GridBagConstraints();
        gbc_lblWelcomeToScrbble.gridheight = 2;
        gbc_lblWelcomeToScrbble.gridwidth = 20;
        gbc_lblWelcomeToScrbble.anchor = GridBagConstraints.WEST;
        gbc_lblWelcomeToScrbble.insets = new Insets(0, 0, 5, 5);
        gbc_lblWelcomeToScrbble.gridx = 1;
        gbc_lblWelcomeToScrbble.gridy = 0;
        getContentPane().add(lblWelcomeToScrbble, gbc_lblWelcomeToScrbble);
        
        JLabel lblIdolePlayers = new JLabel("Idole Players");
        lblIdolePlayers.setHorizontalAlignment(SwingConstants.LEFT);
        lblIdolePlayers.setFont(new Font("Arial", Font.PLAIN, 18));
        GridBagConstraints gbc_lblIdolePlayers = new GridBagConstraints();
        gbc_lblIdolePlayers.fill = GridBagConstraints.VERTICAL;
        gbc_lblIdolePlayers.gridwidth = 5;
        gbc_lblIdolePlayers.anchor = GridBagConstraints.WEST;
        gbc_lblIdolePlayers.insets = new Insets(0, 0, 5, 5);
        gbc_lblIdolePlayers.gridx = 22;
        gbc_lblIdolePlayers.gridy = 1;
        getContentPane().add(lblIdolePlayers, gbc_lblIdolePlayers);
        
        JPanel panel = new JPanel();
        panel.setBackground(SystemColor.inactiveCaption);
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.gridwidth = 20;
        gbc_panel.gridheight = 20;
        gbc_panel.insets = new Insets(0, 0, 5, 5);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 1;
        gbc_panel.gridy = 2;
        getContentPane().add(panel, gbc_panel);
        
//        Container pane = getContentPane();
//      for (int i = 0; i < 20; i++) {
//    	for (int j = 0; j < 20; j++) {
//    		JButton button = new JButton(Integer.toString(i + 1));
//    		panel.add(button);
//    	}
//    	
//    }
                               
        JList list = new JList();
        GridBagConstraints gbc_list = new GridBagConstraints();
        gbc_list.gridheight = 7;
        gbc_list.gridwidth = 11;
        gbc_list.insets = new Insets(0, 0, 5, 5);
        gbc_list.fill = GridBagConstraints.BOTH;
        gbc_list.gridx = 22;
        gbc_list.gridy = 2;
//        list.setVisibleRowCount(5);
        getContentPane().add(list, gbc_list);
        
        //TODO: change to get data from response
        DefaultListModel listModel = new DefaultListModel();
        listModel.addElement("Jane Doe (invited)");
        listModel.addElement("John Smith (invited)");
        listModel.addElement("Kathy Green");
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setModel(listModel);
        
        JScrollPane listScrollPane = new JScrollPane(list);
        GridBagConstraints gbc_listScrollPane = new GridBagConstraints();
        gbc_listScrollPane.gridwidth = 11;
        gbc_listScrollPane.gridheight = 7;
        gbc_listScrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_listScrollPane.fill = GridBagConstraints.BOTH;
        gbc_listScrollPane.gridx = 22;
        gbc_listScrollPane.gridy = 2;
        getContentPane().add(listScrollPane, gbc_listScrollPane);
        
        JButton btnInvite = new JButton("Invite");
        btnInvite.setFont(new Font("Arial", Font.PLAIN, 18));
        GridBagConstraints gbc_btnInvite = new GridBagConstraints();
        gbc_btnInvite.gridwidth = 3;
        gbc_btnInvite.insets = new Insets(0, 0, 5, 5);
        gbc_btnInvite.gridx = 23;
        gbc_btnInvite.gridy = 10;
        getContentPane().add(btnInvite, gbc_btnInvite);
        
        JButton btnStartGame = new JButton("Start Game");
        btnStartGame.setFont(new Font("Arial", Font.PLAIN, 18));
        GridBagConstraints gbc_btnStartGame = new GridBagConstraints();
        gbc_btnStartGame.gridwidth = 5;
        gbc_btnStartGame.insets = new Insets(0, 0, 5, 5);
        gbc_btnStartGame.gridx = 28;
        gbc_btnStartGame.gridy = 10;
        getContentPane().add(btnStartGame, gbc_btnStartGame);
        
        JLabel lblPlayingPlayers = new JLabel("Playing Players");
        lblPlayingPlayers.setFont(new Font("Arial", Font.PLAIN, 18));
        GridBagConstraints gbc_lblPlayingPlayers = new GridBagConstraints();
        gbc_lblPlayingPlayers.gridwidth = 5;
        gbc_lblPlayingPlayers.anchor = GridBagConstraints.WEST;
        gbc_lblPlayingPlayers.insets = new Insets(0, 0, 5, 5);
        gbc_lblPlayingPlayers.gridx = 22;
        gbc_lblPlayingPlayers.gridy = 12;
        getContentPane().add(lblPlayingPlayers, gbc_lblPlayingPlayers);
        

        // TODO change to listener
        String[] columnNames = {"User Name",
                "In Turn",
                "Score"};
        Object[][] data = {
        	    {"Kathy", new Boolean(true), new Integer(10)},
        	    {"Snowboarding", new Boolean(false), new Integer(5)},
        	    {"John", new Boolean(false), new Integer(4)},
        	    {"Doe", new Boolean(false), new Integer(2)},
        	    {"Joe", new Boolean(false), new Integer(10)}
                };
        table = new JTable();
        GridBagConstraints gbc_table = new GridBagConstraints();
        gbc_table.gridheight = 7;
        gbc_table.gridwidth = 11;
        gbc_table.insets = new Insets(0, 0, 5, 5);
        gbc_table.fill = GridBagConstraints.BOTH;
        gbc_table.gridx = 22;
        gbc_table.gridy = 13;
        getContentPane().add(table, gbc_table);
        
        
//        JScrollPane tableScrollPane = new JScrollPane(table);
//        GridBagConstraints gbc_tableScrollPane = new GridBagConstraints();
//        gbc_tableScrollPane.gridwidth = 11;
//        gbc_tableScrollPane.gridheight = 7;
//        gbc_tableScrollPane.insets = new Insets(0, 0, 5, 5);
//        gbc_tableScrollPane.fill = GridBagConstraints.BOTH;
//        gbc_tableScrollPane.gridx = 22;
//        gbc_tableScrollPane.gridy = 13;
//        getContentPane().add(tableScrollPane, gbc_tableScrollPane);
        
        
        JButton btnPass = new JButton("Pass");
        btnPass.setFont(new Font("Arial", Font.PLAIN, 18));
        GridBagConstraints gbc_btnPass = new GridBagConstraints();
        gbc_btnPass.gridwidth = 3;
        gbc_btnPass.insets = new Insets(0, 0, 5, 5);
        gbc_btnPass.gridx = 23;
        gbc_btnPass.gridy = 21;
        getContentPane().add(btnPass, gbc_btnPass);
                
        JButton btnEndGame = new JButton("End Game");
        btnEndGame.setFont(new Font("Arial", Font.PLAIN, 18));
        btnEndGame.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });
        GridBagConstraints gbc_btnEndGame = new GridBagConstraints();
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

