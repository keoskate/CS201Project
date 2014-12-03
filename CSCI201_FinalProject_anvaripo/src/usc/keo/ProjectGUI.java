package usc.keo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ProjectGUI extends JFrame {
	ProjectGUI(){
		super("DAGAME");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		

		JPanel leftColumn = new JPanel(); //set blayout to vertical
		leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
		leftColumn.setBorder(BorderFactory.createLineBorder(Color.black));
		leftColumn.setPreferredSize(new Dimension(100, 30));
		JPanel mainPanel = new MapPanel();
		//mainPanel.setBackground(Color.black);
		add(mainPanel, BorderLayout.CENTER);
		//JPanel myStats = new JPanel();
		//myStats.setLayout(new BoxLayout(myStats, BoxLayout.Y_AXIS));
		//myStats.setBorder(BorderFactory.createLineBorder(Color.blue));

		//leftColumn.add(myStats);
		HealthPanel players[]= new HealthPanel[4];
		
		for(int i = 0; i<4; i++){
			players[i] = new HealthPanel();
			//players[i].setPreferredSize(new Dimension(100, 30));
			players[i].setBorder(BorderFactory.createLineBorder(Color.blue));
			leftColumn.add(players[i]);
		}
		add(leftColumn, BorderLayout.WEST);
		setVisible(true);
	
	}
	public static void main(String[]args){
		ProjectGUI pg = new ProjectGUI();
	}
	
	
}

