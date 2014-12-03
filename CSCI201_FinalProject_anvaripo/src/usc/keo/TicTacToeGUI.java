
package usc.keo;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class TicTacToeGUI extends JFrame implements ActionListener {
    
    static final long serialVersionUID = 1;
    String letter = "";
    public int count = 0;
    public boolean win = false;
    
    JButton button1 = new JButton("");
    JButton button2 = new JButton("");
    JButton button3 = new JButton("");
    JButton button4 = new JButton("");
    JButton button5 = new JButton("");
    JButton button6 = new JButton("");
    JButton button7 = new JButton("");
    JButton button8 = new JButton("");
    JButton button9 = new JButton("");
    JButton restartButton = new JButton("Resart Game");
    JLabel  statusLabel = new JLabel("Start!", SwingConstants.CENTER);
    
   public final void setUpGUI(){
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(3,3));
        centerPanel.add(button1);
        centerPanel.add(button2);
        centerPanel.add(button3);
        centerPanel.add(button4);
        centerPanel.add(button5);
        centerPanel.add(button6);
        centerPanel.add(button7);
        centerPanel.add(button8);
        centerPanel.add(button9);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(restartButton);
        add(bottomPanel, BorderLayout.SOUTH);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(statusLabel);
        add(topPanel, BorderLayout.NORTH);
        
       button1.addActionListener((ActionListener) this);
       button2.addActionListener((ActionListener) this);
       button3.addActionListener((ActionListener) this);
       button4.addActionListener((ActionListener) this);
       button5.addActionListener((ActionListener) this);
       button6.addActionListener((ActionListener) this);
       button7.addActionListener((ActionListener) this);
       button8.addActionListener((ActionListener) this);
       button9.addActionListener((ActionListener) this);
       
       add(centerPanel);
       setVisible(true);
    
    }   
    
    public TicTacToeGUI() {
        super("Tic-Tac-Toe");
        setSize(500, 500);
        setLocation(20, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setUpGUI();

       restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                count = 0;
                statusLabel.setText("Start");
                button1.setText("");
                button1.setEnabled(true);
                button2.setText("");
                button2.setEnabled(true);
                button3.setText("");
                button3.setEnabled(true);
                button4.setText("");
                button4.setEnabled(true);
                button5.setText("");
                button5.setEnabled(true);
                button6.setText("");
                button6.setEnabled(true);
                button7.setText("");
                button7.setEnabled(true);
                button8.setText("");
                button8.setEnabled(true);
                button9.setText("");
                button9.setEnabled(true);
            }
        });
        

    }
    public void actionPerformed(ActionEvent a) {
        count++;
        if (count == 1 || count == 3 || count == 5 || count == 7 || count == 9) {
            statusLabel.setText("Current Player: 2");
            letter = "X";
        } else if (count == 2 || count == 4 || count == 6 || count == 8 || count == 10) {
            statusLabel.setText("Current Player: 1"); 
            letter = "O";
        }
        if (a.getSource() == button1) {
            button1.setText(letter);
            button1.setEnabled(false);
        } else if (a.getSource() == button2) {
            button2.setText(letter);
            button2.setEnabled(false);
        } else if (a.getSource() == button3) {
            button3.setText(letter);
            button3.setEnabled(false);
        } else if (a.getSource() == button4) {
            button4.setText(letter);
            button4.setEnabled(false);
        } else if (a.getSource() == button5) {
            button5.setText(letter);
            button5.setEnabled(false);
        } else if (a.getSource() == button6) {
            button6.setText(letter);
            button6.setEnabled(false);
        } else if (a.getSource() == button7) {
            button7.setText(letter);
            button7.setEnabled(false);
        } else if (a.getSource() == button8) {
            button8.setText(letter);
            button8.setEnabled(false);
        } else if (a.getSource() == button9) {
            button9.setText(letter);
            button9.setEnabled(false);
        }/*Determine who won*/
//horizontal wins

        if (button1.getText().equals(button2.getText()) && button2.getText().equals(button3.getText()) && !button1.getText().equals("")) {
            win = true;
        } else if (button4.getText().equals(button5.getText()) && button5.getText().equals(button6.getText()) && !button4.getText().equals("")) {
            win = true;
        } else if (button7.getText().equals(button8.getText()) && button8.getText().equals(button9.getText()) && !button7.getText().equals("")) {
            win = true;
        }//virticle wins
        else if (button1.getText().equals(button4.getText()) && button4.getText().equals(button7.getText()) && !button1.getText().equals("")) {
            win = true;
        } else if (button2.getText().equals(button5.getText()) && button5.getText().equals(button8.getText()) && !button2.getText().equals("")) {
            win = true;
        } else if (button3.getText().equals(button6.getText()) && button6.getText().equals(button9.getText()) && !button3.getText().equals("")) {
            win = true;
        }//diagonal wins
        else if (button1.getText().equals(button5.getText()) && button5.getText().equals(button9.getText()) && !button1.getText().equals("")) {
            win = true;
        } else if (button3.getText().equals(button5.getText()) && button5.getText().equals(button7.getText()) && !button3.getText().equals("")) {
            win = true;
        } else {
            win = false;
        }

        if (win == true) {
            statusLabel.setText("Player " + letter + " Wins!");
            button1.setEnabled(false);
            button2.setEnabled(false);
            button3.setEnabled(false);
            button4.setEnabled(false);
            button5.setEnabled(false);
            button6.setEnabled(false);
            button7.setEnabled(false);
            button8.setEnabled(false);
            button9.setEnabled(false);
            
            
            
        } else if (count == 9 && win == false) {
            statusLabel.setText("Tie Game");
            //
        }

    }

    public static void main(String[] args) {
        // TODO code application logic here
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (UnsupportedLookAndFeelException e) {
        }

        new TicTacToeGUI();
    }
    
}
