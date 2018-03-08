import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;


@SuppressWarnings("serial")
public class ClientTest extends JFrame implements Runnable {
	
	private JPanel contentPane;
	private JTextArea textArea;
	private JTextArea textArea_1;
	private Socket s;
	private PrintWriter writer;
	private JButton btnSend;
	
	/**
	 * Create the frame.
	 * @param serverClient 
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public ClientTest(String serverAdress, int serverPort) throws UnknownHostException, IOException {
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textArea = new JTextArea();
		textArea.setBounds(0, 0, 434, 300);
		contentPane.add(textArea);
		
		textArea_1 = new JTextArea();
		textArea_1.setBounds(0, 300, 434, 79);
		contentPane.add(textArea_1);
		
		btnSend = new JButton("Send");
		btnSend.setBounds(0, 379, 434, 40);
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sendText();
			}
		});
		contentPane.add(btnSend);
		
		JButton btnNewButton = new JButton("Close Stream");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clStr();
			}
		});
		btnNewButton.setBounds(0, 422, 434, 40);
		contentPane.add(btnNewButton);
		
		s = new Socket(serverAdress, serverPort); // localhost
		
		writer = new PrintWriter(this.s.getOutputStream());
	}
	
	protected void clStr() {
		writer.close();
	}

	protected void sendText() {
		writer.print(textArea_1.getText() + "\r\n");
		writer.flush();
		
		textArea_1.setText("");
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		String serverName;
		int serverPort;
		
		//Initialize the Socket
		if(args.length == 2) {
			serverName = args[0];
			serverPort = Integer.parseInt(args[1]);
		}
		else {
			InputStreamReader streamReader = new InputStreamReader(System.in);
			BufferedReader bReader = new BufferedReader(streamReader);
			
			System.out.print("Server address or name: ");
			serverName = bReader.readLine();
			
			System.out.print("Port number: ");
			serverPort = Integer.parseInt(bReader.readLine());
		}
		
		ClientTest c = new ClientTest(serverName, serverPort);
		Thread t = new Thread(c);
		t.start();
	}

	@Override
	public void run() {
		try {
			InputStreamReader streamReader = new InputStreamReader(s.getInputStream());
			BufferedReader bReader = new BufferedReader(streamReader);
			
			String message;
			while((message = bReader.readLine()) != null) {
				textArea.setText(textArea.getText() + "\n" + message);
			}
			JOptionPane.showMessageDialog(null, "Stream Closed");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
