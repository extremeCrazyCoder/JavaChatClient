import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;


public class ClientMain {
	private Timer startTreads;
	private int delayBetweenQuestions = 20;
	private ClientStarter parent;
	
	private boolean isAdmin = false;
	
	public String username = "";
	
	public String roomName = "";
	private String roomPW = "";
	
	public static final int NOTLOGEDIN = 0;
	public static final int LOGINGIN = 1;
	public static final int LOGEDIN = 2;
	public static final int CONNECTING = 3;
	public static final int CONNECTED = 4;
	public static final int CREATINGROOM = 5;
	public static final int CREATINGACCOUNT = 6;
	public static int state = NOTLOGEDIN;
	
	//For Security  all passwords are coded
	public String passwordEncodeString = "";
	
	public ClientMain(ClientStarter parent) {
		this.parent = parent;
		
		startTreads = new Timer();
		startTreads.scheduleAtFixedRate(new TimerTask() {
			public void run() {mainLoop();}}, 0, delayBetweenQuestions);
	}
	
	void mainLoop() {
		if(parent.watcher.linesAvailable()) {
			String line = parent.watcher.getLine();
			String[] splited = line.split("\t");
			
			//Look if it is a new Encode string
			if(splited.length == 3 && splited[0].equals("coding")) {
				passwordEncodeString = line.split("\t")[2];
			}
			
			//Check the states if it is something else
			else if(state == LOGINGIN) {
				if(line.equals("authOK")) {
					state = LOGEDIN;
					parent.gui.createContentPane();
				}
				else if(line.equals("authNotOK")) {
					parent.gui.showNotification("Falscher Benutzername oder Passwort");
					state = NOTLOGEDIN;
					parent.gui.createContentPane();
				}
				else {
					showErrWrongData(line);
				}
			}
			else if(state == CONNECTING) {
				if(line.equals("entryOK")) {
					state = CONNECTED;
					setIsAdmin(false);
					parent.gui.createContentPane();
				}
				else if(line.equals("entryNotOK")) {
					parent.gui.showNotification("Falscher Raumname oder Passwort");
					state = LOGEDIN;
					parent.gui.createContentPane();
				}
				else {
					showErrWrongData(line);
				}
			}
			else if(state == CONNECTED) {
				if(splited.length == 4) {
					String text = coder.decodeNormal(splited[1], roomPW);
					if(checkMD5(text, splited[3]))
						parent.gui.addLine(splited[0], text);
					else
						parent.gui.showNotification("Konnte daten vom Server nicht dekodieren\nFalscher MD5");
				}
				else if(line.equals("roomdeleted")) {
					state = LOGEDIN;
					resetAllRoomVars();
					parent.gui.createContentPane();
					parent.gui.showNotification("Der Raum wurde vom Raumadministrator gel�scht");
				}
				else if(splited.length == 2 && splited[0].equals("admin")) {
					if(this.username.equals(splited[1]))
						setIsAdmin(true);
					else
						setIsAdmin(false);
				}
				else if(splited.length == 2 && splited[0].equals("kick")) {
					state = LOGEDIN;
					resetAllRoomVars();
					parent.gui.createContentPane();
					parent.gui.showNotification("Du wurdest vom Raumaudministrator (" + splited[1] +
							") gekickt");
				}
				else {
					showErrWrongData(line);
				}
			}
			else if(state == CREATINGACCOUNT) {
				if(splited[0].equals("createAccountOK")) {
					state = LOGEDIN;
					parent.gui.createContentPane();
				}
				else if(splited[0].equals("createAccountNotOK")) {
					if(splited.length == 2) {
						this.username = "";
						
						state = NOTLOGEDIN;
						parent.gui.createContentPane();
						parent.gui.showNotification(splited[1]);
					}
					else {
						showErrWrongData(line);
					}
				}
				else {
					showErrWrongData(line);
				}
			}
			else if(state == CREATINGROOM) {
				if(splited[0].equals("createRoomOK")) {
					state = CONNECTED;
					parent.gui.createContentPane();
					setIsAdmin(true);
				}
				else if(splited[0].equals("createRoomNotOK")) {
					if(splited.length == 2) {
						this.roomName = "";
						this.roomPW = "";
						
						state = LOGEDIN;
						parent.gui.createContentPane();
						parent.gui.showNotification(splited[1]);
					}
					else {
						showErrWrongData(line);
					}
				}
				else {
					showErrWrongData(line);
				}
			}
			else {
				showErrWrongData(line);
			}
		}
	}
	
	void setIsAdmin(boolean value) {
		isAdmin = value;
		parent.gui.createContentPane();
	}
	
	void showErrWrongData(String data) {
		if(data != null && !data.equals(""))
			parent.gui.showNotification("Status: " + state + "\nFalsche daten vom Server:\n" + data);
	}

	private void resetAllRoomVars() {
		parent.gui.emptyMessages();
		roomName = "";
		roomPW = "";
	}
	
	private boolean checkMD5(String text, String md5) {
		return byteToHex(getMD5(text)).equals(md5);
	}

	private String byteToHex(byte[] bytes) {
		char[] hexChars = "0123456789ABCDEF".toCharArray();
		StringBuilder hex = new StringBuilder();
		
		for(int i = bytes.length; i > 0; i--) {
			int tempByte = bytes[i - 1] & 0xFF;
			hex.append(hexChars[tempByte >> 4]);
			hex.append(hexChars[tempByte & 0x0F]);
		}
		
		return hex.toString();
	}

	private byte[] getMD5(String toHash) {
		try {
			return MessageDigest.getInstance("MD5").digest(toHash.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			return null;
		}
	}
	
	public String codeText(String uncoded) throws IOException {
		return coder.codeNormal(uncoded, roomPW);
	}

	public void setRoomPW(String password) {
		roomPW = password;
	}
	
	protected void sendText(String text) throws IOException {
		parent.watcher.sendData(username + "\t" +
				coder.codeNormal(text, this.roomPW) + "\t\t" +
				byteToHex(getMD5(text)));
	}

	protected void login(String username, String password) {
		if(!password.equals("") && !username.equals("")) {
			parent.watcher.sendData("login\t" + username + "\t"
					+ PWCoder.codeNormal(password, passwordEncodeString));
			
			this.username = username;
			state = LOGINGIN;
			
			parent.gui.createContentPane();
		}
		else
			parent.gui.showNotification("Leere Felder sind nicht erlaubt");
	}
	
	protected void createAccount(String username, String password) {
		if(!password.equals("") && !username.equals("")) {
			parent.watcher.sendData("createAccount\t" + username + "\t"
					+ PWCoder.codeNormal(password, passwordEncodeString));

			this.username = username;
			state = CREATINGACCOUNT;
			
			parent.gui.createContentPane();
		}
		else
			parent.gui.showNotification("Sie m�ssen usernamen und passwort eingeben");
	}
	
	protected void enterRoom(String roomname, String password) {
		if(!password.equals("") && !roomname.equals("")) {
			parent.watcher.sendData("join\t" + roomname + "\t"
					+ PWCoder.codeNormal(password, passwordEncodeString));
			
			roomName = roomname;
			setRoomPW(password);
			state = CONNECTING;
			
			parent.gui.createContentPane();
		}
		else
			parent.gui.showNotification("Leere Felder sind nicht erlaubt");
	}

	protected void createRoom(String roomname, String password) {
		if(!password.equals("") && !roomname.equals("")) {
			parent.watcher.sendData("createRoom\t" + roomname + "\t"
					+ PWCoder.codeNormal(password, passwordEncodeString));
			
			roomName = roomname;
			setRoomPW(password);
			state = CREATINGROOM;
			
			parent.gui.createContentPane();
		}
		else
			parent.gui.showNotification("Leere Felder sind nicht erlaubt");
	}

	public void closeRoom() {
		if(isAdmin)
			parent.watcher.sendData("closeRoom");
	}

	public void kick(String toKick) {
		if(isAdmin)
			parent.watcher.sendData("kick\t" + toKick);
	}

	public void newAdmin(String newAdmin) {
		if(isAdmin)
			parent.watcher.sendData("admin\t" + newAdmin);
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void newSecuritySetting(boolean value) {
		parent.watcher.sendData("encryptionNeeded\t" + ((value)?("1"):("0")));
	}
}