package ass.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ChooseCharacter extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ChooseCharacter dialog = new ChooseCharacter();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ChooseCharacter() {
		setTitle("Choose Character");
		setBounds(100, 100, 498, 196);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		for (int i = 0; i < 26; i++){
			String btmName = Character.toString((char)((int)'A' + i));
			JButton btnCharButton = new JButton(btmName);
			btnCharButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					//TODO: change to return text
					System.out.print(btnCharButton.getText());
					
					setVisible(false);
					dispose();
					}
			});
			contentPanel.add(btnCharButton);
		}
	}

}
