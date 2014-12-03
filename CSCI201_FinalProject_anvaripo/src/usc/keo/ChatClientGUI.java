package usc.keo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class ChatClientGUI extends JFrame implements ActionListener, KeoConstants {
	private static final long serialVersionUID = 1L;

	private JLabel label;
	private JTextField tf;
	private JTextField tfServer, tfPort;
	private JButton login, logout, PlayerList;
	private JTextArea ta;
	private boolean connected;
	private Client client;
	private int defaultPort;
	private String defaultHost;


	ChatClientGUI(String host, int port) {
		super("Chat Client");
		defaultPort = port;
		defaultHost = host;
		
		JPanel northPanel = new JPanel(new GridLayout(3,1));
		JPanel serverAndPort = new JPanel(new GridLayout(1, 5, 1, 3));
		
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

		serverAndPort.add(new JLabel("Server Address:  "));
		serverAndPort.add(tfServer);
		serverAndPort.add(new JLabel("Port Number:  "));
		serverAndPort.add(tfPort);
		serverAndPort.add(new JLabel(""));

		northPanel.add(serverAndPort);

		label = new JLabel("Enter your username below", SwingConstants.LEFT);
		northPanel.add(label);
		tf = new JTextField("Anonymous");
		tf.setBackground(Color.WHITE);
		northPanel.add(tf);
		add(northPanel, BorderLayout.NORTH);

		ta = new JTextArea("Welcome to the Chat room\n", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(ta));
		ta.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);

		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);		
		PlayerList = new JButton("Player List");
		PlayerList.addActionListener(this);
		PlayerList.setEnabled(false);		

		JPanel southPanel = new JPanel();
		southPanel.add(login);
		southPanel.add(logout);
		southPanel.add(PlayerList);
		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		tf.requestFocus();
	}


	public void appendMessage(String str) {
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
	}

	public void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		PlayerList.setEnabled(false);
		label.setText("Enter your username below");
		tf.setText("Anonymous");

		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);

		tfServer.setEditable(false);
		tfPort.setEditable(false);

		tf.removeActionListener(this);
		connected = false;
	}
		
	//hey
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		if(o == logout) {
			client.sendMessage(new ClientMessage(ClientMessage.LOGOUT, ""));
			return;
		}

		if(o == PlayerList) {
			client.sendMessage(new ClientMessage(ClientMessage.LIST, ""));				
			return;
		}


		if(connected) {
			//tf.setForeground(Color.RED);
			client.sendMessage(new ClientMessage(ClientMessage.MESSAGE, tf.getText()));				
			tf.setText("");
			return;
		}
		

		if(o == login) {

			String username = tf.getText().trim();
			// empty username
			if(username.length() == 0)
				return;
			// empty serverAddress
			String server = tfServer.getText().trim();
			if(server.length() == 0)
				return;

			String portNumber = tfPort.getText().trim();
			if(portNumber.length() == 0)
				return;
			int port = 0;
			try {
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) {
				return;  
			}


			client = new Client(server, port, username, this);
	
			if(!client.initClient()) 
				return;
			tf.setText("");
			label.setText("Enter your message below");
			connected = true;
			
	
			login.setEnabled(false);

			logout.setEnabled(true);
			PlayerList.setEnabled(true);
	
			tfServer.setEditable(false);
			tfPort.setEditable(false);

			tf.addActionListener(this);
		}

	}


	public static void main(String[] args) {
		new ChatClientGUI("localhost", 1500);
	}

}