import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;


@SuppressWarnings("serial")
public class ClientGUI extends JFrame {
    private ClientStarter parent;
    private List<Message> messages = new ArrayList<Message>();
    private ClientAdminToolsGUI adminTools;
    
    private JPanel contentPane;
    private JProgressBar barConnecting;
    private JPasswordField password;
    private JTextField textRoomname;
    private JTextField textUsername;
    private JTextArea messagesArea;
    private JTextArea UserTextInput;
    private JLabel lblHeader;
    private JLabel lblConnecting;
    private JLabel lblUserRoomName;
    private JLabel lblPassword;
    private JLabel lblRoomName;
    private JButton btnEnter;
    private JButton btnCreateAccount;
    private JButton btnLogin;
    private JButton btnCreateRoom;
    private JButton btnAdmin;
    
    private Timer animation = new Timer();
    private int animationState = 0;
    private JScrollPane scrollMessages;
    
    /**
     * Create the frame.
     */
    public ClientGUI(ClientStarter parent) {
        this.parent = parent;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        
        firstInitGUI();
        createContentPane();
        
        setVisible(true);
    }

    public void showNotification(String warning) {
        JOptionPane.showMessageDialog(null, warning);
    }
    
    public void addLine(String username, String text) {
        messages.add(new Message(username, text));
        
        messagesArea.setText(messagesArea.getText() +
                messages.get(messages.size() - 1).getCompleteMessage() + "\n");
    }
    
    private String getPassword(JPasswordField field) {
        char pw[] = field.getPassword();
        StringBuilder pwStr = new StringBuilder();
        
        for(int i = 0; i < pw.length; i++)
            pwStr.append(pw[i]);
        
        return pwStr.toString();
    }

    public String getUserText() {
        String text = UserTextInput.getText();
        UserTextInput.setText("");
        
        text = text.replaceAll("\r", "");
        while(text.endsWith("\n"))
            text = text.substring(0, text.length() - 1);
        
        addLine(parent.main.username, text);
        return text;
    }
    
    public void createContentPane() {
        animation.cancel();
        
        resetAllElements();
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);
        
        if(ClientMain.state == ClientMain.NOTLOGEDIN)
            createLoginForm();
        
        if(ClientMain.state == ClientMain.LOGEDIN)
            createRoomEnterForm();
        
        if(ClientMain.state == ClientMain.CONNECTED)
            createChatForm();
        
        if(ClientMain.state == ClientMain.LOGINGIN ||
                ClientMain.state == ClientMain.CREATINGACCOUNT ||
                ClientMain.state == ClientMain.CREATINGROOM ||
                ClientMain.state == ClientMain.CONNECTING)
            createDoingForm();
        
        setVisible(false);
        setVisible(true);
    }

    private void resetAllElements() {
        barConnecting.setValue(0);
        password.setText("");
        textRoomname.setText("");
        textUsername.setText("");
        messagesArea.setText("");
        UserTextInput.setText("");
        lblConnecting.setText("Verbinde ");
    }

    private void createDoingForm() {
        lblHeader.setFont(new Font("Tahoma", Font.PLAIN, 30));
        lblHeader.setBounds(0, 0, 434, 59);
        contentPane.add(lblHeader);
        contentPane.add(barConnecting);
        contentPane.add(lblConnecting);
        
        animation = new Timer();
        animation.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                animateDoing();
            }
        }, 10, 10);
    }
    
    protected void animateDoing() {
        if((animationState % 20) == 0) {
            lblConnecting.setText(lblConnecting.getText() + ".");
            
            if((animationState % 80) == 0)
                lblConnecting.setText("Verarbeite ");
        }
        barConnecting.setValue(animationState % barConnecting.getMaximum());
        animationState++;
    }
    
    private void createLoginForm() {
        lblUserRoomName.setText("Username:");
        contentPane.add(lblUserRoomName);
        
        lblHeader.setFont(new Font("Tahoma", Font.PLAIN, 30));
        lblHeader.setBounds(0, 0, 434, 59);
        contentPane.add(lblHeader);
        contentPane.add(lblPassword);
        contentPane.add(textUsername);
        contentPane.add(password);
        contentPane.add(btnCreateAccount);
        contentPane.add(btnLogin);
    }

    private void createRoomEnterForm() {
        lblUserRoomName.setText("Room:");
        contentPane.add(lblUserRoomName);
        
        lblHeader.setFont(new Font("Tahoma", Font.PLAIN, 30));
        lblHeader.setBounds(0, 0, 434, 59);
        contentPane.add(lblHeader);
        contentPane.add(lblPassword);
        contentPane.add(textRoomname);
        contentPane.add(password);
        contentPane.add(btnCreateRoom);
        contentPane.add(btnEnter);
    }

    private void createChatForm() {
        contentPane.add(UserTextInput);
        contentPane.add(scrollMessages);
        contentPane.add(lblRoomName);
        
        lblHeader.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lblHeader.setBounds(0, 0, 434, 36);
        contentPane.add(lblHeader);
        
        if(parent.main.isAdmin())
            contentPane.add(btnAdmin);
    }

    public void emptyMessages() {
        messages = new ArrayList<Message>();
    }
    
    private void firstInitGUI() {
        //Doing Form & Login Form & Room Enter Form & Chat Form
        lblHeader = new JLabel("Chat-Client");
        lblHeader.setFont(new Font("Tahoma", Font.PLAIN, 30));
        lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
        lblHeader.setBounds(15, 0, 404, 36);
        lblHeader.setBorder(null);
        
        //Login Form & Room Enter Form
        lblUserRoomName = new JLabel();
        lblUserRoomName.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lblUserRoomName.setHorizontalAlignment(SwingConstants.LEFT);
        lblUserRoomName.setBounds(10, 92, 146, 34);
        lblUserRoomName.setBorder(null);

        lblPassword = new JLabel("Passwort:");
        lblPassword.setHorizontalAlignment(SwingConstants.LEFT);
        lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lblPassword.setBorder(null);
        lblPassword.setBounds(10, 137, 146, 34);
        
        password = new JPasswordField();
        password.setBounds(166, 137, 146, 34);
        
        //Doing Form
        barConnecting = new JProgressBar();
        barConnecting.setBounds(10, 201, 414, 24);
        barConnecting.setMinimum(0);
        barConnecting.setMaximum(100);
        
        lblConnecting = new JLabel("Verarbeite ");
        lblConnecting.setFont(new Font("Tahoma", Font.PLAIN, 25));
        lblConnecting.setHorizontalAlignment(SwingConstants.CENTER);
        lblConnecting.setBounds(10, 134, 414, 50);
        
        //Login Form
        textUsername = new JTextField();
        textUsername.setBounds(166, 92, 146, 34);
        textUsername.setColumns(10);
        
        btnCreateAccount = new JButton("Create Account");
        btnCreateAccount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                parent.main.createAccount(textUsername.getText(),
                        getPassword(password));
            }
        });
        btnCreateAccount.setBounds(296, 47, 138, 34);
        
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Tahoma", Font.PLAIN, 25));
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                parent.main.login(textUsername.getText(),
                        getPassword(password));
            }
        });
        btnLogin.setBounds(50, 200, 324, 40);
        
        //Room Enter Form
        textRoomname = new JTextField();
        textRoomname.setBounds(166, 92, 146, 34);
        textRoomname.setColumns(10);
        
        btnCreateRoom = new JButton("Create Room");
        btnCreateRoom.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                parent.main.createRoom(textRoomname.getText(),
                        getPassword(password));
            }
        });
        btnCreateRoom.setBounds(296, 47, 138, 34);
        
        btnEnter = new JButton("Enter Room");
        btnEnter.setFont(new Font("Tahoma", Font.PLAIN, 25));
        btnEnter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                parent.main.enterRoom(textRoomname.getText(),
                        getPassword(password));
            }
        });
        btnEnter.setBounds(50, 201, 324, 40);
        
        //Chat Form
        UserTextInput = new JTextArea();
        UserTextInput.setLineWrap(true);
        UserTextInput.setBounds(0, 228, 434, 34);
        UserTextInput.setBorder(BorderFactory.createLineBorder(Color.black));
        UserTextInput.addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent e) {
                //not Used
            }
            
            @Override
            public void keyPressed(KeyEvent e) {
                //not Used
            }
    
            @Override
            public void keyTyped(KeyEvent e) {
                if(((int) e.getKeyChar() == 10) || ((int) e.getKeyChar() == 13)) {
                    if(!e.isShiftDown()) {
                        try {
                            parent.main.sendText(getUserText());
                        } catch (IOException e1) {
                            showNotification(e1.getMessage());
                            e1.printStackTrace();
                        }
                    }
                    else {
                        UserTextInput.setText(UserTextInput.getText() + "\n");
                    }
                }
            }
        });
        
        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        messagesArea.setLineWrap(true);
        scrollMessages = new JScrollPane(messagesArea);
        scrollMessages.setBounds(0, 56, 434, 172);
        scrollMessages.setBorder(BorderFactory.createLineBorder(Color.black));
        scrollMessages.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        lblRoomName = new JLabel("");
        lblRoomName.setHorizontalAlignment(SwingConstants.CENTER);
        lblRoomName.setBounds(0, 36, 434, 20);
        
        btnAdmin = new JButton("Admin");
        btnAdmin.setBounds(345, 2, 89, 23);
        btnAdmin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                createAdminToolsGUI();
            }
        });
    }
    
    protected void createAdminToolsGUI() {
        if(adminTools != null)
            adminTools.setVisible(false);
        
        adminTools = new ClientAdminToolsGUI(this);
    }
    
    public void closeRoom() {
        parent.main.closeRoom();
        this.adminTools.setVisible(false);
    }
    public void kick(String toKick) {
        parent.main.kick(toKick);
    }

    public void newAdmin(String newAdmin) {
        parent.main.newAdmin(newAdmin);
        this.adminTools.setVisible(false);
    }

    public void newSecuritySetting(boolean value) {
        parent.main.newSecuritySetting(value);
    }
}

class Message {
    String username;
    String text;
    
    public Message(String username, String text) {
        this.username = username;
        this.text = text;
    }
    
    public String getCompleteMessage() {
        return username + ":" + text;
    }
}