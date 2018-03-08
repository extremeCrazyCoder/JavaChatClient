import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;


@SuppressWarnings("serial")
public class ClientAdminToolsGUI extends JFrame {

	private JPanel contentPane;
	private JTextField textKick;
	private ClientGUI parent;
	private JTextField textNewAdmin;
	private JCheckBox chckbxAllowOnlyEncrypted;

	/**
	 * Create the frame.
	 */
	public ClientAdminToolsGUI(ClientGUI parent) {
		this.parent = parent;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Admintools");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lblNewLabel.setBounds(0, 0, 434, 35);
		contentPane.add(lblNewLabel);
		
		JButton btnNewButton = new JButton("Close Room");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				closeRoom();
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnNewButton.setBounds(33, 223, 374, 40);
		contentPane.add(btnNewButton);
		
		textKick = new JTextField();
		textKick.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10 || e.getKeyCode() == 13) {
					String temp = textKick.getText();
					textKick.setText("");
					kick(temp);
				}
			}
		});
		textKick.setFont(new Font("Tahoma", Font.PLAIN, 20));
		textKick.setBounds(160, 73, 247, 40);
		contentPane.add(textKick);
		textKick.setColumns(10);
		
		JLabel lblKick = new JLabel("Kick:");
		lblKick.setHorizontalAlignment(SwingConstants.RIGHT);
		lblKick.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblKick.setBounds(0, 73, 150, 40);
		contentPane.add(lblKick);
		
		JLabel lblNewAdmin = new JLabel("new Admin:");
		lblNewAdmin.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewAdmin.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewAdmin.setBounds(0, 124, 150, 40);
		contentPane.add(lblNewAdmin);
		
		textNewAdmin = new JTextField();
		textNewAdmin.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10 || e.getKeyCode() == 13) {
					String temp = textNewAdmin.getText();
					textNewAdmin.setText("");
					newAdmin(temp);
				}
			}
		});
		textNewAdmin.setFont(new Font("Tahoma", Font.PLAIN, 20));
		textNewAdmin.setColumns(10);
		textNewAdmin.setBounds(160, 124, 247, 40);
		contentPane.add(textNewAdmin);
		
		chckbxAllowOnlyEncrypted = new JCheckBox("Allow only encrypted Users");
		chckbxAllowOnlyEncrypted.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				changeOnSecurity();
			}
		});
		chckbxAllowOnlyEncrypted.setBounds(33, 192, 227, 23);
		contentPane.add(chckbxAllowOnlyEncrypted);
		
		this.setVisible(true);
	}

	protected void changeOnSecurity() {
		parent.newSecuritySetting(chckbxAllowOnlyEncrypted.isSelected());
	}

	protected void newAdmin(String newAdmin) {
		parent.newAdmin(newAdmin);
	}

	protected void closeRoom() {
		parent.closeRoom();
	}

	protected void kick(String toKick) {
		parent.kick(toKick);
	}
}
