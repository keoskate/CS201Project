package usc.keo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;	
import java.awt.Polygon;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class HealthPanel extends JPanel {
	JLabel name, score, health;
	
	public HealthPanel(){
		super();
		//setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		name = new JLabel("Name");
		name.setPreferredSize(new Dimension(90, 10));
		add(name);
		score = new JLabel("Score");
		score.setPreferredSize(new Dimension(90, 10));
		add(score);
		health = new JLabel("Health");
		health.setPreferredSize(new Dimension(90, 10));
		health.setText("Health");
		add(health);
		
	}
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Polygon poly = new Polygon();
		poly.addPoint(0, 50);
		poly.addPoint(0, 60);
		poly.addPoint(100, 60);
		poly.addPoint(100, 50);
		g.setColor(Color.green);
	
		g.drawPolygon(poly);
		//g.setColor(Color.green);
		g.fillPolygon(poly);
		}
	}

