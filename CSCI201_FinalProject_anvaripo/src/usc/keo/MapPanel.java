package usc.keo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;
import javax.swing.Timer;


public class MapPanel extends JPanel implements ActionListener, KeyListener {
	Timer t = new Timer(5, this);
	boolean bullet = false;
	int x = 300, y = 300, ovalX = 318, ovalY = 335, bulletX = 318, bulletY = 335,  lineTopY = 315, w = 15, h = 20, w2 = 50, h2 =60, changeX = 0, changeY = 0;
	//AffineTransform scaleMatrix;
	public MapPanel(){
		t.start();
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
	}
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		g.setColor(Color.orange);
		Polygon poly = new Polygon();
		poly.addPoint(230, 40);
		poly.addPoint(270, 40);
		poly.addPoint(290, 80);
		poly.addPoint(290, 108);
		poly.addPoint(270, 148);
		poly.addPoint(230, 148);
		poly.addPoint(210, 108);
		poly.addPoint(210, 80);
		g.drawPolygon(poly);
		g.fillPolygon(poly);
		Graphics2D g2d = (Graphics2D)g;
		g.setColor(Color.blue);
		g.fillOval(ovalX, ovalY, w, h);
		//g.drawLine(lineX, ovalX, lineX, lineTopY);
		
		Area outter = new Area(new Rectangle(0, 0, getWidth(), getHeight()));
		Ellipse2D inner = new Ellipse2D.Double(x, y, w2, h2);
        
        outter.subtract(new Area(inner));

        g2d.setColor(new Color(0, 0, 0));
        g2d.fill(outter);
        
    	g.fillOval(bulletX, bulletY, 8, 8);
        
	}
	
	public void actionPerformed(ActionEvent e) {
		repaint();
		x += changeX; 
		y += changeY; 
		ovalX += changeX; 
		ovalY += changeY;  
		lineTopY += changeY;
		bulletX = ovalX + w/4;
		bulletY = ovalY + h/4;
		
		
	}
	public void up(){
		changeX = 0;
		changeY = -1;
		rotateUp();
		
	}
	public void down(){
		changeX = 0;
		changeY = 1;
		rotateDown();
		
	}
	public void left(){
		changeX = -1;
		changeY = 0;
		rotateLeft();
		
		
	}
	public void right(){
		changeX = 1;
		changeY = 0;
		rotateRight();
		
	}
	public void keyTyped(KeyEvent e) {
		
	}
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_UP)
			up();
		if (code == KeyEvent.VK_DOWN)
			down();
		if (code == KeyEvent.VK_RIGHT)
			right();
		if (code == KeyEvent.VK_LEFT)
			left();
		if(code == KeyEvent.VK_SPACE){
		}
		
	}
	public void keyReleased(KeyEvent e) {
		
	}
	
	public void rotateRight(){
		if(w>h){
			if(ovalX-x<30)
				return;
			else{
				ovalX = ovalX-30;
			}	
		}
		else{
			int tempw = w;
			w=h;
			h=tempw;
			int tempw2 = w2;
			w2=h2;
			h2=tempw2;
			if(ovalY - y < 30){
				ovalX = ovalX-15;

				ovalY = ovalY+15;
			}
			else{
		
				ovalX = ovalX-15;

				ovalY = ovalY- 15;
			}
		}
		
	}
	public void rotateLeft(){
		if(w>h){
			if(ovalX-x<30)
				ovalX = ovalX+30;
			else{
				return;
			}	
		}
		else{
			int tempw = w;
			w=h;
			h=tempw;
			int tempw2 = w2;
			w2=h2;
			h2=tempw2;
			if(ovalY - y < 30){
				ovalX = ovalX+15;

				ovalY = ovalY+15;
			}
			else{
		
				ovalX = ovalX+15;

				ovalY = ovalY-15;
			}
		}
		
	}
	public void rotateUp(){
		if(w>h){
			int tempw = w;
			w=h;
			h=tempw;
			int tempw2 = w2;
			w2=h2;
			h2=tempw2;
			if(ovalX-x<30){
				ovalX = ovalX+15;
				ovalY = ovalY+15;
			}
			else{
				ovalX = ovalX-15;
				ovalY = ovalY+15;
			}	
		}
		else{
			
			if(ovalY - y < 30){
				ovalY=ovalY+30;
			}
			else{
		
				
			}
		}
		
	}
	public void rotateDown(){
		if(w>h){
			int tempw = w;
			w=h;
			h=tempw;
			int tempw2 = w2;
			w2=h2;
			h2=tempw2;
			if(ovalX-x<30){
				ovalX = ovalX+15;
				ovalY = ovalY-15;
			}
			else{
				ovalX = ovalX-15;
				ovalY = ovalY-15;
			}	
		}
		else{
			
			if(ovalY - y < 30){
				return;
			}
			else{
				ovalY=ovalY-30;
				
			}
		}
		
	}
	
}
