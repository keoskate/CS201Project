package usc.keo;

import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.*;

public class ClientGUI extends JFrame implements ActionListener, KeoConstants {
	private static final long serialVersionUID = 1L;

	private JLabel label;
	private JTextField textField;
	private JButton login, logout;
	private JTextArea clientTextArea;
	private boolean connected;
	private Client client;
		
	public Cell[][] cell = new Cell[3][3];
	public JLabel jlblTitle = new JLabel();
	public JLabel jlblStatus = new JLabel();

	public boolean second = true; 
	public boolean myTurn = false;
	public char myToken = ' ';
	public char otherToken = ' ';
	public boolean continueToPlay = true;
	public boolean waiting = true;
	public int rowSelected;
	public int columnSelected;
	
	ClientGUI() {
		super("Chat Client");
		setSize(600, 700);
		setLayout(null);
		
		JPanel textFieldPanel = new JPanel(new GridLayout(2,1));
		
		JPanel gamePanel = new JPanel();
		gamePanel.setLayout(new GridLayout(3, 3, 0, 0));

		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				gamePanel.add(cell[i][j] = new Cell(i, j));

		gamePanel.setBorder(new LineBorder(Color.black, 1));
		jlblTitle.setHorizontalAlignment(JLabel.CENTER);
		jlblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
		jlblTitle.setBorder(new LineBorder(Color.black, 1));
		jlblStatus.setBorder(new LineBorder(Color.black, 1));
		
		label = new JLabel("Enter your username below", SwingConstants.LEFT);
		textFieldPanel.add(label);
		textField = new JTextField("Anonymous");
		textField.setBackground(Color.WHITE);
		textFieldPanel.add(textField);	

		clientTextArea = new JTextArea("Welcome to the Chat room\n", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(clientTextArea));
		clientTextArea.setEditable(false);
		
		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);		
	
		JPanel southPanel = new JPanel();
		southPanel.add(login);
		southPanel.add(logout);
		
		jlblTitle.setBounds(10, 5, 580, 20);
		gamePanel.setBounds(140, 35, 250, 250);
		jlblStatus.setBounds(10, 290, 580, 20);
		
		textFieldPanel.setBounds(0, 300, 600, 100);
		centerPanel.setBounds(0, 400, 600, 230);
		southPanel.setBounds(0, 630, 600, 75);
		
		add(jlblTitle, jlblTitle.getPreferredSize());
		add(gamePanel, this.getSize());
		add(jlblStatus, this.getSize());
		add(textFieldPanel, this.getSize());
		add(centerPanel, this.getSize());
		add(southPanel, this.getSize());
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		textField.requestFocus();
		
		client = new Client(this);
		client.initClient2();
	}


	public void appendMessage(String str) {
		clientTextArea.append(str);
		clientTextArea.setCaretPosition(clientTextArea.getText().length() - 1);
	}

	public void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);

		label.setText("Enter your username below");
		textField.setText("Anonymous");

//		portField.setText("" + PORT);
//		serverField.setText(HOST);

		textField.removeActionListener(this);
		connected = false;
	}
		
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		if(o == logout) {
			client.sendMessage(new ClientMessage(ClientMessage.LOGOUT, ""));
			return;
		}
		//client.sendMessage(new ClientMessage(ClientMessage.LIST, ""));
		if(connected) {
			client.sendMessage(new ClientMessage(ClientMessage.MESSAGE, textField.getText()));				
			textField.setText("");
			return;
		}
	
		if(o == login) {
			if (!second) {
				String username = textField.getText().trim();

				if(username.length() == 0)
					return;

				client = new Client(username, this);
		
				if(!client.initClient()) 
					return;
				
				textField.setText("");
				label.setText("Enter your message below:");
				connected = true;
				
				login.setEnabled(false);
				logout.setEnabled(true);
				textField.addActionListener(this);
			}
			
		}

	}
	public class Cell extends JPanel {
		private static final long serialVersionUID = 1L;
		private int row;
		private int column;
		private char token = ' ';
		
		public Cell(int row, int column) { 
			this.row = row;
			this.column = column;
			setBorder(new LineBorder(Color.black, 1));
			addMouseListener(new ClickListener());
		}

		public char getToken() { return token; }
		public void setToken(char c) {
			token = c;
			repaint();
		}
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			 if (token == 'X') {
				 g.drawLine(10, 10, getWidth() - 10, getHeight() - 10);
				 g.drawLine(getWidth() - 10, 10, 10, getHeight() - 10);
			 } else if (token == 'O') {
				 g.drawOval(10, 10, getWidth() - 20, getHeight() - 20);
			 }
		 }

		private class ClickListener extends MouseAdapter {
			 @Override
			 public void mouseClicked(MouseEvent e) {
				 // If cell is not occupied and the player has the turn
				 if (token == ' ' && myTurn) {
					 setToken(myToken); // Set the player's token in the cell 256 myTurn = false;
					 rowSelected = row;
					 columnSelected = column;
					 jlblStatus.setText("Waiting for the other player to move");
					 waiting = false; // Just completed a successful move
				 }
			 }
		 }
	}
	
	public static void main(String[] args) {
		new ClientGUI();
	}

}