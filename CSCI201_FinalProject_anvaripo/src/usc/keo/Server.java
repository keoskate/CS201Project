package usc.keo;

import java.io.*;
import java.net.*;
import java.util.*;

//Server Accept/Stop Clients
public class Server implements KeoConstants {
	private static int userId;
	private ArrayList<ClientThread> clientThreads;
	private ServerGUI serverGUI;
	private HandleSession session;
	
	private boolean running;
		
	public Server(ServerGUI gui) {
		this.serverGUI = gui;
		clientThreads = new ArrayList<ClientThread>();
	}
	
	//Main While loop for adding new clients
	public void startServer() {
		running = true;
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			serverGUI.appendText(new Date() + ": Server started at socket "+ PORT + "\n");
			
			ClientThread ct;
			int sessionNo = 1;
			while (running) {
				
				serverGUI.appendText(new Date() + ": Wait for players to join session " + sessionNo + '\n');
				System.out.println("Server waiting for Clients on port " + PORT + ".");
				
				Socket player1 = serverSocket.accept();
				//append
				//append
				if(!running)
					break;
				
//				ct = new ClientThread(player1); 
//				clientThreads.add(ct);									
//				ct.start();
				new DataOutputStream(player1.getOutputStream()).writeInt(PLAYER1);

				Socket player2 = serverSocket.accept();
				//append
				//append
//				ct = new ClientThread(player2); 
//				clientThreads.add(ct);									
//				ct.start();
				new DataOutputStream(player2.getOutputStream()).writeInt(PLAYER2);
				
				// Create a new thread for this session of two players
				HandleSession task = new HandleSession(player1, player2);
				session = task;
				new Thread(task).start();
				
//				Socket socket = serverSocket.accept();  	
//				if(!running)
//					break;
//				ct = new ClientThread(socket); 
//				clientThreads.add(ct);									
//				ct.start();
				
			}
			//Terminate 
			try {
				serverSocket.close();
//				for(int i = 0; i < clientThreads.size(); ++i) {
//					ClientThread tc = clientThreads.get(i);
//					try {
//						tc.chatInput.close();
//						tc.chatOutput.close();
//						tc.socket.close();
//					} catch(IOException ioE) {  }
//				}
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
			new Socket(HOST, PORT); 
		}catch(Exception e) {  }
	}

	private void showTime(String msg) {
		//String time = DATEFORMAT.format(new Date()) + " " + msg;
		//System.out.println(time);
	}
	
	private synchronized void broadcast(String message) {
		String time = DATEFORMAT.format(new Date());
		String toSendMessage = time + " " + message + "\n";

		serverGUI.appendText(toSendMessage); 
		
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
	
	class HandleSession implements Runnable, KeoConstants {
		private Socket player1;
		private Socket player2;
		  // Create and initialize cells
		private char[][] cell = new char[3][3];
		 DataInputStream fromPlayer1; 
		 DataOutputStream toPlayer1; 
		 DataInputStream fromPlayer2; 
		 DataOutputStream toPlayer2;
		 
		private boolean continueToPlay = true;
		
		ClientMessage client1Message, client2Message;
		String date;
		
		public HandleSession(Socket player1, Socket player2) { 
			this.player1 = player1;
			this.player2 = player2;
		  	// Initialize cells
			for(int i = 0; i < 3; i++) 
				for(int j = 0; j < 3; j++)
					cell[i][j] = ' ';
			
	       // date = new Date().toString() + "\n";
			
		}
		public void run() {
			boolean running = true;
			try {
				toPlayer1 = new DataOutputStream( player1.getOutputStream());
				toPlayer2 = new DataOutputStream( player2.getOutputStream());
				fromPlayer1 = new DataInputStream( player1.getInputStream());
				fromPlayer2 = new DataInputStream( player2.getInputStream());
				
				toPlayer1.writeInt(1);
				
				while(true) {
					// Receive a move from player 1
				    int row =  fromPlayer1.readInt(); 
				    int column =  fromPlayer1.readInt(); 
				    cell[row][column] = 'X';
				   
				    // Check if Player 1 wins
				    if (isWon('X')) { 
				    	toPlayer1.writeInt(PLAYER1_WON); 
				    	toPlayer2.writeInt(PLAYER1_WON); 
				    	sendMove(toPlayer2, row, column); 
				    	break; // Break the loop
				    } else if (isFull()) { // Check if all cells are filled
				        toPlayer1.writeInt(DRAW); 
				        toPlayer2.writeInt(DRAW); 
				        sendMove(toPlayer2, row, column); 
				        break;
				    } else {
				        // Notify player 2 to take the turn
				        toPlayer2.writeInt(CONTINUE);
				        // Send player 1's selected row and column to player 2
				        sendMove(toPlayer2, row, column);
				    }
				   
				    // Receive a move from Player 2
				    row = (int)fromPlayer2.readInt(); 
				    column = (int)fromPlayer2.readInt(); 
				    cell[row][column] = 'O';
				   
				    // Check if Player 2 wins
				    if (isWon('O')) { 
				    	toPlayer1.writeInt(PLAYER2_WON); 
				    	toPlayer2.writeInt(PLAYER2_WON); 
				    	sendMove(toPlayer1, row, column); 
				    	break;
				    } else {
				        // Notify player 1 to take the turn
				        toPlayer1.writeInt(CONTINUE);
				        // Send player 2's selected row and column to player 1	
				        sendMove(toPlayer1, row, column);
					} 
				} 
			}catch(Exception e) {
					e.printStackTrace();
			}
				
		}
		
	
		private void sendMove(DataOutputStream out, int row, int column) throws IOException {
			  out.writeInt(row); // Send row index
			  out.writeInt(column); // Send column index
		}

		private boolean isFull() { 
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 3; j++) 
					if (cell[i][j] == ' ')
						return false; // At least one cell is not filled
			
			// All cells are filled
			return true; 
		}

		private boolean isWon(char token) {
		    // Check all rows
		    for(int i=0;i < 3; i++)
			    if ((cell[i][0] == token)
			        && (cell[i][1] == token)
			    	&& (cell[i][2] == token)) {
			        	return true;
			    }
		    /** Check all columns */
		    for (int j=0;j<3;j++) 
		    	if ((cell[0][j] == token)
			        && (cell[1][j] == token)
			   	    && (cell[2][j] == token)) { 
			   	    	return true;
			    }
		    /** Check major diagonal */
		    if ((cell[0][0] == token)
		        && (cell[1][1] == token)
		    	&& (cell[2][2] == token)) {
		        	return true;
		    }
		    /** Check subdiagonal */
		    if ((cell[0][2] == token)
		        && (cell[1][1] == token)
		    	&& (cell[2][0] == token)) {
		        	return true;
		    }
			return false;
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
		
		private char[][] cell = new char[3][3];
		
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



