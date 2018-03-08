import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;


public class ClientStarter {
	/**
	 * Nach dem Start ( und eventuell nach einer gewissen Zeit)
	 * 		aenderung des Stringes mit dem die Passwoerter Verschluesselt werden
	 * 		Vom Client:				typ (websocket: [handshake], java: JAVA) & version
	 * 		Vom Server:				coding\t\t[Schluessel]
	 * 
	 * Protokoll:
	 * 1) main Login
	 * 		Zum Server:				login\t[username]\t[verschluesseltes Passwort]
	 * 		Vom Server bei Erfolg:	authOK
	 * 		Bei Miserfolg:			authNotOK
	 * 
	 * 2) main Login erstellen eines Accounts
	 * 		Zum Server:				createAccount\t[username]\t[verschluesseltes Passwort]
	 * 		Vom Server (erfolg):	createAccountOK\t
	 * 		Vom Server (miserfolg):	createAccountNotOK\t[Message why]
	 * 
	 * 3) Einloggen in einen Raum
	 * 		Senden zum Server:		join\t[raum]\t[verschluesseltes Passwort]
	 * 		Vom Server bei Erfolg:	entryOK
	 * 		Bei Miserfolg:			entryNotOK
	 * 
	 * 4) Bekommen aller alten Daten dieses Raumes (und der weiteren nachrichten)
	 * 		Vom Server:				[name]\t[verschluesselter Text]\t[Zeit]\t[md5 des Textes]
	 * 
	 * 5) Senden einer nachricht zum Server
	 * 		Zum Server:				[name]\t[verschluesselter Text]\t\t[md5 des Textes]
	 * 
	 * 6) Erstellen & verlassen eines Raumes
	 * 		Zum Server (Erstellen:	createRoom\t[raum]\t[verschluesseltes Passwort]
	 * 		Vom Server (erfolg):	createRoomOK\t
	 * 		Vom Server (miserfolg):	createRoomNotOK\t[Message why]
	 * 		Zum Server (verlassen):	exit\t\t[name]
	 * 		Vom Server (Admin Loescht Raum): roomdeleted
	 * 
	 * 7) Ausloggen
	 * 		Zum Server:				logout\t\t
	 * 
	 * 8) Vom Raumadministrator
	 * 		User kicken:			kick\t[username]
	 * 		Vom Server(bei kick):	kick\t[username des Admins]
	 * 		Raum schliessen:		closeRoom
	 * 		Neuen admin ernennen:	admin\t[username]
	 * 		Vom Server(bei change):	admin\t[username des neuen Admins]
	 * 		Änderung der Sicherheit (Encryption)	encryptionNeeded\t[neuer Status (0,1)]
	 */
	
	public ClientGUI gui;
	public ClientMain main;
	public ClientConnectionToServer watcher;
	
	public String serverName;
	public int serverPort;
	public Socket sock;
	
	public Thread watcherThread;
	
	public static void main(String[] args) throws IOException {
		new ClientStarter(args);
	}
	
	public ClientStarter(String[] args) throws IOException {
		//init a shutdown hook to close the stream if programm is closed
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override public void run() {closeStream();}}));
		
		//Initialize the Socket
		if(args.length == 2) {
			serverName = args[0];
			serverPort = Integer.parseInt(args[1]);
		}
		else {
			String input = JOptionPane.showInputDialog(null,"Geben sie den Servernamen ein",
					"Serverabfrage", JOptionPane.PLAIN_MESSAGE);
			
			if(input.indexOf(":") == -1) {
				serverName = input;
				serverPort = 5000;
			}
			else {
				String splited[] = input.split(":");
				
				serverName = splited[0];
				serverPort = Integer.parseInt(splited[1]);
			}
		}
		
		
		sock = new Socket(serverName, serverPort);
		
		watcher = new ClientConnectionToServer(sock, this);
		gui = new ClientGUI(this);
		main = new ClientMain(this);
		
		watcherThread = new Thread(watcher);
		watcherThread.start();
	}

	protected void closeStream() {
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}