package usc.keo;

import java.net.*;
import java.io.*;

public class Client implements KeoConstants{
	
	private ObjectInputStream chatInput;
	private ObjectOutputStream chatOutput;
	private Socket socket;
	private ClientGUI cGUI;
	private String user;
	//private int user;
	
	private DataInputStream fromServer;
	private DataOutputStream toServer;

	Client(String username, ClientGUI cGUI) {
		this.setUser(username);
		this.cGUI = cGUI;
	}
	Client(ClientGUI cGUI) {
		this.cGUI = cGUI;
	}
	public boolean initClient2() {
		// try to connect to the server
		try {
			socket = new Socket(HOST, PORT);
		} catch(Exception ec) {
			display("Error connecting to server:" + ec);
			return false;
		}
		
		try {
			toServer = new DataOutputStream(socket.getOutputStream());
			fromServer = new DataInputStream(socket.getInputStream());
		} catch (IOException eIO) {
			eIO.printStackTrace();
			display("Error creating new Input/Output Streams: " + eIO);
			return false;
		}
		new ServerListenerThread2().start();
		
		return true;
	}
	public boolean initClient() {
		// try to connect to the server
		try {
			socket = new Socket(HOST, PORT);
		} catch(Exception ec) {
			display("Error connecting to server:" + ec);
			return false;
		}
		
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);

		try{
			chatOutput = new ObjectOutputStream(socket.getOutputStream());
			chatInput = new ObjectInputStream(socket.getInputStream());
			
		} catch (IOException eIO) {
			eIO.printStackTrace();
			display("Error creating new Input/Output Streams: " + eIO);
			return false;
		}

		new ServerListenerThread().start();
		
		try {
			//toServer.writeObject(user);
			chatOutput.writeObject(getUser());
		} catch (IOException eIO) {
			display(" during login : " + eIO);
			disconnect();
			return false;
		}

		return true;
	}
	
	private void display(String msg) {
		cGUI.appendMessage(msg + "\n");		
	}
	
	void sendMessage(ClientMessage msg) {
		try {
			//toServer.writeObject(msg);
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
	
	class ServerListenerThread2 extends Thread {
		@Override
		public void run() {
			try {				
				 // Get notification from the server
		        int player = fromServer.readInt();
		     
		        // Am I player 1 or 2?
		        if (player == PLAYER1) {
		        	cGUI.myToken = 'X';
		        	cGUI.otherToken = 'O';
		        	cGUI.jlblTitle.setText("Player 1 with token 'X'"); 
		        	cGUI.jlblStatus.setText("Waiting for player 2 to join");
		            // Receive startup notification from the server
		        	fromServer.readInt(); // Whatever read is ignored        
		            cGUI.jlblStatus.setText("Player 2 has joined. I start first");
		            cGUI.myTurn = true; 
		        } else if (player == PLAYER2) {
		        	System.out.println("Test");
		        	cGUI.myToken = 'O';
		        	cGUI.otherToken = 'X';
		        	cGUI.jlblTitle.setText("Player 2 with token 'O'"); 
		        	cGUI.jlblStatus.setText("Waiting for player 1 to move");
		        }
		        while(cGUI.continueToPlay) {
					if (player == PLAYER1) {
		                waitForPlayerAction();
		                sendMove();
		                receiveInfoFromServer();
		            } else if (player == PLAYER2) {
		                receiveInfoFromServer();
		                waitForPlayerAction();
		                sendMove();
		            }
		        }
		        System.out.println("Imhereee");
		        Thread.sleep(1000);
		    	fromServer.close();
		    	toServer.close();
		    	socket.close();
		    	
		    	

			}catch(IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e3) {
				e3.printStackTrace();
			}
			
		}
		
		
	}
	class ServerListenerThread extends Thread {
		@Override
		public void run() {
			while(true) {
				try {		
					String msg = (String) chatInput.readObject();
					cGUI.appendMessage(msg);
			        
				}catch(IOException e) {
					e.printStackTrace();
					display("Server has closed the connection: " + e);
					if(cGUI != null) 
						cGUI.connectionFailed();
					break;
				} catch(ClassNotFoundException e2) { 
					e2.printStackTrace();
				}
			}
		}
	}

	private void waitForPlayerAction() throws InterruptedException { 
		while (cGUI.waiting) {
			Thread.sleep(100); 
		}
		cGUI.waiting = true; 
	}

	private void sendMove() throws IOException { 
		toServer.writeInt(cGUI.rowSelected); // Send the selected row 
		toServer.writeInt(cGUI.columnSelected); // Send the selected column
	}

	public void receiveInfoFromServer() throws IOException { // Receive game status
	    int status = fromServer.readInt();
	    if (status == PLAYER1_WON) {
	        // Player 1 won, stop playing 
	    	cGUI.continueToPlay = false;
	        if (cGUI.myToken == 'X') {
	        	cGUI.jlblStatus.setText("I won! (X)"); 
	        } else if (cGUI.myToken == 'O') {
	        	cGUI.jlblStatus.setText("Player 1 (X) has won!");
	            receiveMove();
	        }
	    } else if (status == PLAYER2_WON) {
	        // Player 2 won, stop playing
	    	cGUI.continueToPlay = false; 
	        if (cGUI.myToken == 'O') {
	        	cGUI.jlblStatus.setText("I won! (O)"); 
	   		} else if (cGUI.myToken == 'X') { 
	   			cGUI.jlblStatus.setText("Player 2 (O) has won!"); 
	   		 	receiveMove();
	        } 
	    } else if (status == DRAW) {
	        // No winner, game is over
	    	cGUI.continueToPlay = false; 
	    	cGUI.jlblStatus.setText("Game is over, no winner!");
	        if (cGUI.myToken == 'O') { 
	        	receiveMove();
	        } 
	    } else {
	        receiveMove(); 
	        cGUI.jlblStatus.setText("My turn"); 
	        cGUI.myTurn = true; // It is my turn
	    } 
	}


	private void receiveMove() throws IOException { // Get the other player's move
		int row = fromServer.readInt();
		int column = fromServer.readInt(); 
		cGUI.cell[row][column].setToken(cGUI.otherToken);
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	
	
}
