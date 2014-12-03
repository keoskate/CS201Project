package usc.keo;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerGUI extends JFrame implements ActionListener, WindowListener, KeoConstants {
	private static final long serialVersionUID = 1L;

	//Server Thread
	private Server server;
	
	//GUI
	private JButton stopStart;
	private JTextArea chatTextArea;
	private JTextField portTextField;

	ServerGUI(int defaultPort) {
		super("The Server");
		server = null;

		//North Panel 
		JPanel north = new JPanel();
		north.add(new JLabel("Port number: "));
		portTextField = new JTextField("  " + defaultPort);
		north.add(portTextField);

		stopStart = new JButton("Start");
		stopStart.addActionListener(this);
		north.add(stopStart);
		add(north, BorderLayout.NORTH);
		
		//Text Area 
		JPanel center = new JPanel(new GridLayout(1,1));
		chatTextArea = new JTextArea(80,80);
		chatTextArea.setEditable(false);
		appendText("Chat room.\n");
		center.add(new JScrollPane(chatTextArea));
		add(center);
		
		addWindowListener(this);
		setSize(400, 600);
		setVisible(true);
	}		
	
	class RunServer extends Thread {
		public void run() {
			server.startServer();
			stopStart.setText("Start");
			portTextField.setEditable(true);
			System.out.println("Server Stopped\n");
			server = null;
		}
	}
		
	//Start or Stop Pressed
	public void actionPerformed(ActionEvent e) {
		if(server != null) {
			server.stopServer();
			server = null;
			portTextField.setEditable(true);
			stopStart.setText("Start");
			return;
		}

		int portNumber;
		try {
			portNumber = Integer.parseInt(portTextField.getText().trim());
		}catch(Exception er) {
			appendText("Invalid port number");
			return;
		}

		server = new Server(portNumber, this);
		new RunServer().start();  //Run Server thread
		
		stopStart.setText("Stop");
		portTextField.setEditable(false); //don't need this line
	}
	
	//Appends Text to Text Area
	void appendText(String str) {
		chatTextArea.append(str);
		chatTextArea.setCaretPosition(chatTextArea.getText().length() - 1);
	}

	//If hit close button, stop server 
	public void windowClosing(WindowEvent e) {
		if(server != null) {
			try {
				server.stopServer();			
			}catch(Exception eClose) { 
				eClose.printStackTrace();  
			}finally {
				server = null;
			}
		}
		dispose();
		System.exit(0);
	}

	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	
	public static void main(String[] arg) throws UnknownHostException {
		InetAddress IP= InetAddress.getLocalHost();
		System.out.println("IP of my system is := "+IP.getHostAddress());
		
		new ServerGUI(PORT);
	}
}

