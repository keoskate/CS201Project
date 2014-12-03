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
	
//	private ObjectInputStream fromServer;
//	private ObjectOutputStream toServer;

	Client(String username, ClientGUI cGUI) {
		this.user = username;
		this.cGUI = cGUI;
	}
	Client(ClientGUI cGUI) {
		this.cGUI = cGUI;
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
			chatOutput.writeObject(user);
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
	
	class ServerListenerThread extends Thread {
		@Override
		public void run() {
			try {
				 // Get notification from the server
		        String player = (String)chatInput.readObject();
				
		        // Am I player 1 or 2?
		        if (player.equals(PLAYER1)) {
		        	cGUI.myToken = 'X';
		        	cGUI.otherToken = 'O';
		        	cGUI.jlblTitle.setText("Player 1 with token 'X'"); 
		        	cGUI.jlblStatus.setText("Waiting for player 2 to join");
		            // Receive startup notification from the server
		        	chatInput.readObject(); // Whatever read is ignored        
		            cGUI.jlblStatus.setText("Player 2 has joined. I start first");
		            cGUI.myTurn = true; 
		        } else if (player.equals(PLAYER2)) {
		        	System.out.println("Test");
		        	cGUI.myToken = 'O';
		        	cGUI.otherToken = 'X';
		        	cGUI.jlblTitle.setText("Player 2 with token 'O'"); 
		        	cGUI.jlblStatus.setText("Waiting for player 1 to move");
		        }
		        while(cGUI.continueToPlay) {
		        	String msg = (String) chatInput.readObject();
					cGUI.appendMessage(msg);
					
					if (player.equals(PLAYER1)) {
		                waitForPlayerAction();
		                sendMove();
		                receiveInfoFromServer();
		            } else if (player.equals(PLAYER2)) {
		                receiveInfoFromServer();
		                waitForPlayerAction();
		                sendMove();
		            }
		        }
			}catch(IOException e) {
				e.printStackTrace();
				display("Server has closed the connection: " + e);
				if(cGUI != null) 
					cGUI.connectionFailed();
				cGUI.continueToPlay = false;
			} catch(ClassNotFoundException e2) { 
				e2.printStackTrace();
			} catch (InterruptedException e3) {
				e3.printStackTrace();
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
		chatOutput.writeObject(cGUI.rowSelected); // Send the selected row 
		chatOutput.writeObject(cGUI.columnSelected); // Send the selected column
	}

	public void receiveInfoFromServer() throws IOException, ClassNotFoundException { // Receive game status
	    String status = (String) chatInput.readObject();
	    if (status.equals(PLAYER1_WON)) {
	        // Player 1 won, stop playing continueToPlay = false;
	        if (cGUI.myToken == 'X') {
	        	cGUI.jlblStatus.setText("I won! (X)"); 
	        } else if (cGUI.myToken == 'O') {
	        	cGUI.jlblStatus.setText("Player 1 (X) has won!");
	            receiveMove();
	        }
	    } else if (status.equals(PLAYER2_WON)) {
	        // Player 2 won, stop playing
	    	cGUI.continueToPlay = false; 
	        if (cGUI.myToken == 'O') {
	        	cGUI.jlblStatus.setText("I won! (O)"); 
	   		} else if (cGUI.myToken == 'X') { 
	   			cGUI.jlblStatus.setText("Player 2 (O) has won!"); 
	   		 	receiveMove();
	        } 
	    } else if (status.equals(DRAW)) {
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


	private void receiveMove() throws IOException, ClassNotFoundException { // Get the other player's move
		String row = (String)chatInput.readObject();
		String column = (String)chatInput.readObject();
		cGUI.cell[Integer.parseInt(row)][Integer.parseInt(column)].setToken(cGUI.otherToken);
	}
	
	
}
