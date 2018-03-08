import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;

import java.util.ArrayList;
import java.util.List;


public class ClientConnectionToServer implements Runnable{
    private Socket serverSocket;
    @SuppressWarnings("unused")
    private ClientStarter parent;
    private List<String> lines;
    private BufferedReader reader;
    private PrintWriter writer;
    
    public ClientConnectionToServer(Socket serverSocket, ClientStarter parent) throws IOException {
        this.serverSocket = serverSocket;
        this.parent = parent;
        this.lines = new ArrayList<String>();
        
        initStream();
        
        writer = new PrintWriter(this.serverSocket.getOutputStream());
        
        openingHandshake();
    }
    
    private void openingHandshake() {
        sendData("JAVA " + coder.getNormalVersion() + " " +  + PWCoder.getNormalVersion());
        sendData("");
    }

    private void initStream() throws IOException {
        InputStreamReader streamReader = new InputStreamReader(this.serverSocket.getInputStream());
        this.reader = new BufferedReader(streamReader);
    }

    @Override
    public void run() {
        try {
            while(true) {
                lines.add(reader.readLine());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean linesAvailable() {
        return lines.size() > 0;
    }
    
    public String getLine() {
        String line = lines.get(0);
        lines.remove(0);
        return line;
    }
    
    public String getLineWithoutReading() {
        return lines.get(0);
    }

    public void sendData(String data) {
        writer.println(data);
        writer.flush();
    }
}