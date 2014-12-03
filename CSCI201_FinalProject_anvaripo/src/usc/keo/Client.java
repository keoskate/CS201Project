package usc.keo;

import java.net.*;
import java.io.*;

public class Client {
	
	private ObjectInputStream chatInput;
	private ObjectOutputStream chatOutput;
	private Socket socket;
	private ChatClientGUI cGUI;
	private String server, user;
	private int port;
	
	Client(String server, int port, String username) {
		this.server = server;
		this.port = port;
		this.user = username;
		this.cGUI = null;
	}
	
	Client(String server, int port, String username, ChatClientGUI cGUI) {
		this.server = server;
		this.port = port;
		this.user = username;

		this.cGUI = cGUI;
	}
	
	public boolean initClient() {
		// try to connect to the server
		try {
			socket = new Socket(server, port);
		} catch(Exception ec) {
			display("Error connecting to server:" + ec);
			return false;
		}
		
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);

		try{
			chatInput  = new ObjectInputStream(socket.getInputStream());
			chatOutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException eIO) {
			display("Error creating new Input/Output Streams: " + eIO);
			return false;
		}

		new ServerListenerThread().start();
		
		try {
			chatOutput.writeObject(user);
		} catch (IOException eIO) {
			display("Exception during login : " + eIO);
			disconnect();
			return false;
		}

		return true;
	}
	
	private void display(String msg) {
		if(cGUI == null)
			System.out.println(msg);      
		else
			cGUI.appendMessage(msg + "\n");		
	}
	
	void sendMessage(ClientMessage msg) {
		try {
			chatOutput.writeObject(msg);
		} catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}
	
	private void disconnect() {
		try { 
			if(chatInput != null) chatInput.close();
		} catch(Exception e) {} 
		
		try {
			if(chatOutput != null) chatOutput.close();
		} catch(Exception e) {} 
        
		try{
			if(socket != null) socket.close();
		} catch(Exception e) {} 

		if(cGUI != null)
			cGUI.connectionFailed();		
	}
	
	class ServerListenerThread extends Thread {
		
		@Override
		public void run() {
			while(true) {
				try {
					String msg = (String) chatInput.readObject();
					
					if(cGUI == null) {
						System.out.println(msg);
						System.out.print("> ");
					} else {
						cGUI.appendMessage(msg);
					}
				} catch(IOException e) {
					display("Server has closed the connection: " + e);
					if(cGUI != null) 
						cGUI.connectionFailed();
					break;
				} catch(ClassNotFoundException e2) { }
			}
		}
	}

}
