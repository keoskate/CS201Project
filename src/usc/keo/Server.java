package usc.keo;

import java.io.*;
import java.net.*;
import java.util.*;

//Server Accept/Stop Clients
public class Server implements KeoConstants {
	private static int userId;
	private ArrayList<ClientThread> clientThreads;
	private ServerGUI serverGUI;
	
	private int port;
	private boolean running;
	
	public Server(int port) {
		this(port, null);
	}
	
	public Server(int port, ServerGUI gui) {
		this.serverGUI = gui;
		this.port = port;
		clientThreads = new ArrayList<ClientThread>();
	}
	
	public void startServer() {
		running = true;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			//Main While loop for adding new clients
			while(running) {
				showTime("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();  	
				if(!running)
					break;
				ClientThread ct = new ClientThread(socket); 
				clientThreads.add(ct);									
				ct.start();
			}
			//Terminate 
			try {
				serverSocket.close();
				for(int i = 0; i < clientThreads.size(); ++i) {
					ClientThread tc = clientThreads.get(i);
					try {
						tc.chatInput.close();
						tc.chatOutput.close();
						tc.socket.close();
					} catch(IOException ioE) {  }
				}
			}catch(Exception e) {
				showTime("Exception closing the server and clients: " + e);
			}
		}catch (IOException e) {
            String msg = DATEFORMAT.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			showTime(msg);
		}		
	}
		
	protected void stopServer() {
		running = false;
		try {
			new Socket(HOST, port); 
		}catch(Exception e) {  }
	}

	private void showTime(String msg) {
		String time = DATEFORMAT.format(new Date()) + " " + msg;
		if(serverGUI == null)
			System.out.println(time);
	}

	private synchronized void broadcast(String message) {
		String time = DATEFORMAT.format(new Date());
		String toSendMessage = time + " " + message + "\n";
		// show message on console or GUI
		if(serverGUI == null) {
			System.out.print(toSendMessage);
		}else {
			serverGUI.appendText(toSendMessage); 
		}
		for(int i = clientThreads.size(); --i >= 0;) {
			ClientThread ct = clientThreads.get(i);
			if(!ct.writeMsg(toSendMessage)) {
				clientThreads.remove(i);
				showTime("Disconnected Client " + ct.user + " removed from list.");
			}
		}
	}

	private synchronized void removeClientThread(int id) {
		for(int i = 0; i < clientThreads.size(); ++i) {
			ClientThread ct = clientThreads.get(i);
			if(ct.id == id) {
				clientThreads.remove(i);
				return;
			}
		}
	}
	
	class ClientThread extends Thread {
		Socket socket;
		ObjectInputStream chatInput;
		ObjectOutputStream chatOutput;
		int id;
		String user;
		ClientMessage clientMessage;
		String date;

		ClientThread(Socket socket) {
			id = userId++;
			this.socket = socket;

			System.out.println("Thread trying to create Object Input/Output Streams");
			try {
				chatOutput = new ObjectOutputStream(socket.getOutputStream());
				chatInput  = new ObjectInputStream(socket.getInputStream());

				user = (String) chatInput.readObject();
				showTime(user + " just connected.");
			}catch (IOException e) {
				showTime("Exception creating new Input/output Streams: " + e);
				return;
			}catch (ClassNotFoundException e) { }
			
            date = new Date().toString() + "\n";
		}
		
		@Override
		public void run() {
			boolean running = true;
			while(running) { //loop until LOGOUT
				try {
					clientMessage = (ClientMessage)chatInput.readObject();
				}catch (IOException e) {
					showTime(user + " Exception reading Streams: " + e);
					break;				
				}catch(ClassNotFoundException e2) {
					break;
				}
	
				String message = clientMessage.getMessage();

				switch(clientMessage.getType()) {
					case ClientMessage.MESSAGE:
						broadcast(user + ": " + message);
						break;
					case ClientMessage.LOGOUT:
						showTime(user + " disconnected with a LOGOUT message.");
						running = false;
						break;
					case ClientMessage.LIST:
						writeMsg("List of the users connected at " + DATEFORMAT.format(new Date()) + "\n");
						for(int i = 0; i < clientThreads.size(); ++i) {
							ClientThread ct = clientThreads.get(i);
							writeMsg((i+1) + ") " + ct.user + " since " + ct.date);
						}
						break;
				}
			}
			removeClientThread(id);
			closeConnections();
		}
		
		private void closeConnections() {
			// try to close the connection
			try {
				if(chatOutput != null) 
					chatOutput.close();
			} catch(Exception e) {}
			
			try {
				if(chatInput != null) 
					chatInput.close();
			} catch(Exception e) {};
			
			try {
				if(socket != null) 
					socket.close();
			} catch (Exception e) {}
		}

		private boolean writeMsg(String msg) {

			if(!socket.isConnected()) {
				closeConnections();
				return false;
			}

			try {
				chatOutput.writeObject(msg);
			} catch(IOException e) {
				showTime("Error sending message to " + user);
				showTime(e.toString());
			}
			return true;
		}
	}
}
