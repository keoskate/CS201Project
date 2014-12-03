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
			
			int sessionNo = 1;
//			while (running) {
//				serverGUI.appendText(new Date() + ": Wait for players to join session " + sessionNo + '\n');
//				
//				Socket player1 = serverSocket.accept();
//				//append
//				//append
//				if(!running)
//					break;
//				
//				new ObjectOutputStream(player1.getOutputStream()).writeObject(PLAYER1);
//
//				Socket player2 = serverSocket.accept();
//				//append
//				//append
//				
//				new ObjectOutputStream(player2.getOutputStream()).writeObject(PLAYER2);
//				
//				// Create a new thread for this session of two players
//				HandleSession task = new HandleSession(player1, player2);
//				session = task;
//				new Thread(task).start();
//			}
			
			
			while(running) {
				showTime("Server waiting for Clients on port " + PORT + ".");
				
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
			new Socket(HOST, PORT); 
		}catch(Exception e) {  }
	}

	private void showTime(String msg) {
		//String time = DATEFORMAT.format(new Date()) + " " + msg;
		//System.out.println(time);
	}
	
//	private synchronized void broadcast(String message) {
//		String time = DATEFORMAT.format(new Date());
//		String toSendMessage = time + " " + message + "\n";
//
//		serverGUI.appendText(toSendMessage); 
//		
//		if(!session.writeMsg1(toSendMessage)) {
//			System.out.println("Client 1 Disconnected Client " );
//		}
//		if(!session.writeMsg2(toSendMessage)) {
//			System.out.println("Client 2 Disconnected Client " );
//		}
//	}

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
		 ObjectInputStream fromPlayer1; 
		 ObjectOutputStream toPlayer1; 
		 ObjectInputStream fromPlayer2; 
		 ObjectOutputStream toPlayer2;
//		private ObjectInputStream chatInput;
//		private ObjectOutputStream chatOutput;
//		private ObjectInputStream chatInput2;
//		private ObjectOutputStream chatOutput2;
		 
		private boolean continueToPlay = true;
		int id;
		
		ClientMessage client1Message, client2Message;
		String date, user1, user2;
		
		public HandleSession(Socket player1, Socket player2) { 
			id = userId++;
			this.player1 = player1;
			this.player2 = player2;
		  	// Initialize cells
			for(int i = 0; i < 3; i++) 
				for(int j = 0; j < 3; j++)
					cell[i][j] = ' ';
			
			try {
				// Create data input and output streams
				toPlayer1 = new ObjectOutputStream( player1.getOutputStream());
				toPlayer2 = new ObjectOutputStream( player2.getOutputStream());
				fromPlayer1 = new ObjectInputStream( player1.getInputStream());
				fromPlayer2 = new ObjectInputStream( player2.getInputStream());
				
//				chatInput  = new ObjectInputStream(player1.getInputStream());
//				chatInput2  = new ObjectInputStream(player1.getInputStream());
				
				
				user1 = (String)fromPlayer1.readObject();
				user2 = (String)fromPlayer2.readObject();
				
				
			}catch (IOException e) {
				e.printStackTrace();
				return;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	        date = new Date().toString() + "\n";
			
		}
		public void run() {
			boolean running = true;
			try {
				toPlayer1.writeObject(1);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while (running) {
				try {
					client1Message = (ClientMessage)fromPlayer1.readObject();
					client2Message = (ClientMessage)fromPlayer2.readObject();
				}catch (IOException e) {
					break;				
				}catch(ClassNotFoundException e2) {
					break;
				}
				
				String message1 = client1Message.getMessage();
				String message2 = client1Message.getMessage();
					
				switch(client1Message.getType()) {
					case ClientMessage.MESSAGE:
						broadcast(user1 + ": " + message1);
						break;
					case ClientMessage.LOGOUT:
						running = false;
						break;
				}
				switch(client2Message.getType()) {
					case ClientMessage.MESSAGE:
						broadcast(user2 + ": " + message2);
						break;
					case ClientMessage.LOGOUT:
						running = false;
						break;
				}	
				try {
					// Receive a move from player 1
				    int row = (int) fromPlayer1.readObject(); 
				    int column = (int) fromPlayer1.readObject(); 
				    cell[row][column] = 'X';
				    // Check if Player 1 wins
				    if (isWon('X')) { 
				    	toPlayer1.writeObject(PLAYER1_WON); 
				    	toPlayer2.writeObject(PLAYER1_WON); 
				    	sendMove(toPlayer2, row, column); 
				    	break; // Break the loop
				    }
				    else if (isFull()) { // Check if all cells are filled
				        toPlayer1.writeObject(DRAW); 
				        toPlayer2.writeObject(DRAW); 
				        sendMove(toPlayer2, row, column); 
				        break;
				    }
				    else {
				        // Notify player 2 to take the turn
				        toPlayer2.writeObject(CONTINUE);
				        // Send player 1's selected row and column to player 2
				        sendMove(toPlayer2, row, column);
				    }
				    // Receive a move from Player 2
				    row = (int)fromPlayer2.readObject(); 
				    column = (int)fromPlayer2.readObject(); 
				    cell[row][column] = 'O';
				    // Check if Player 2 wins
				    if (isWon('O')) { 
				    	toPlayer1.writeObject(PLAYER2_WON); 
				    	toPlayer2.writeObject(PLAYER2_WON); 
				    	sendMove(toPlayer1, row, column); 
				    	break;
				    }
				    else {
				        // Notify player 1 to take the turn
				        toPlayer1.writeObject(CONTINUE);
				        // Send player 2's selected row and column to player 1	
				        sendMove(toPlayer1, row, column);
					} 
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			removeClientThread(id);
			closeConnections();
			}
		}
		
		private void closeConnections() {
			// try to close the connection
			try {
				if(toPlayer1 != null) 
					toPlayer1.close();
				if(toPlayer2 != null) 
					toPlayer2.close();
			} catch(Exception e) {}
			
			try {
				if(fromPlayer1 != null) 
					fromPlayer1.close();
				if(fromPlayer2 != null) 
					fromPlayer2.close();
			} catch(Exception e) {};
			
			try {
				if(player1 != null) 
					player1.close();
				if(player2 != null) 
					player2.close();
			} catch (Exception e) {}
		}
		
		private boolean writeMsg1(String msg) {

			if(!player1.isConnected()) {
				closeConnections();
				return false;
			}

			try {
				toPlayer1.writeObject(msg);
			} catch(IOException e) {
				e.printStackTrace();
			}
			
			return true;
		}
		
		private boolean writeMsg2(String msg) {

			if(!player2.isConnected()) {
				closeConnections();
				return false;
			}

			try {
				toPlayer2.writeObject(msg);
			} catch(IOException e) {
				e.printStackTrace();
			}
			
			return true;
		}
		
		private void sendMove(ObjectOutputStream out, int row, int column) throws IOException {
			  out.writeObject(row); // Send row index
			  out.writeObject(column); // Send column index
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
			// Initialize cells
			for(int i = 0; i < 3; i++) 
				for(int j = 0; j < 3; j++)
					cell[i][j] = ' ';

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
			try {
				clientThreads.get(0).chatOutput.writeObject("1");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
//					case ClientMessage.LIST:
//						//writeMsg("List of the users connected at " + DATEFORMAT.format(new Date()) + "\n");
//						broadcast("Users connected:  ");
//						for(int i = 0; i < clientThreads.size(); ++i) {
//							ClientThread ct = clientThreads.get(i);
//							broadcast((i+1) + ") " + ct.user + " since " + ct.date);
//							//writeMsg((i+1) + ") " + ct.user + " since " + ct.date);
//						}
//						break;
				}
				try {
					// Receive a move from player 1
				    String row = (String) clientThreads.get(0).chatInput.readObject();
				    String column = (String) clientThreads.get(0).chatInput.readObject(); 
				    cell[Integer.parseInt(row)][Integer.parseInt(column)] = 'X';
				    // Check if Player 1 wins
				    if (isWon('X')) { 
				    	clientThreads.get(0).chatOutput.writeObject(PLAYER1_WON); 
				    	clientThreads.get(1).chatOutput.writeObject(PLAYER1_WON); 
				    	sendMove(clientThreads.get(1).chatOutput, row, column); 
				    	break; // Break the loop
				    }
				    else if (isFull()) { // Check if all cells are filled
				    	clientThreads.get(0).chatOutput.writeObject(DRAW); 
				    	clientThreads.get(1).chatOutput.writeObject(DRAW); 
				        sendMove(clientThreads.get(1).chatOutput, row, column); 
				        break;
				    }
				    else {
				        // Notify player 2 to take the turn
				    	clientThreads.get(1).chatOutput.writeObject(CONTINUE);
				        // Send player 1's selected row and column to player 2
				        sendMove(clientThreads.get(1).chatOutput, row, column);
				    }
				    // Receive a move from Player 2
				    row = (String)clientThreads.get(1).chatInput.readObject(); 
				    column = (String)clientThreads.get(1).chatInput.readObject(); 
				    cell[Integer.parseInt(row)][Integer.parseInt(column)] = 'O';
				    // Check if Player 2 wins
				    if (isWon('O')) { 
				    	clientThreads.get(0).chatOutput.writeObject(PLAYER2_WON); 
				    	clientThreads.get(1).chatOutput.writeObject(PLAYER2_WON); 
				    	sendMove(clientThreads.get(0).chatOutput, row, column); 
				    	break;
				    }
				    else {
				        // Notify player 1 to take the turn
				    	clientThreads.get(0).chatOutput.writeObject(CONTINUE);
				        // Send player 2's selected row and column to player 1	
				        sendMove(clientThreads.get(0).chatOutput, row, column);
					} 
				}catch(Exception e) {
					e.printStackTrace();
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
		
		private void sendMove(ObjectOutputStream out, String row, String column) throws IOException {
			  out.writeObject(row); // Send row index
			  out.writeObject(column); // Send column index
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
}



