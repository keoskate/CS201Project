package usc.keo;

import java.text.SimpleDateFormat;

public interface KeoConstants {
	public final static int PORT = 1600;
    public final static String HOST = "localhost"; 
    public final static int LIST = 0, MESSAGE = 1, LOGOUT =  2;
    public final static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("HH:mm:ss");;
    
	public static int PLAYER1 = 1; // Indicate player 1
	public static int PLAYER2 = 2; // Indicate player 2
	public static int PLAYER1_WON = 1; // Indicate player 1 won
	public static int PLAYER2_WON = 2; // Indicate player 2 won
	public static int DRAW = 3; // Indicate a draw
	public static int CONTINUE = 4; // Indicate to continue
	
//	public static String PLAYER1 = "1"; // Indicate player 1
//	public static String PLAYER2 = "2"; // Indicate player 2
//	public static String PLAYER1_WON = "1"; // Indicate player 1 won
//	public static String PLAYER2_WON = "2"; // Indicate player 2 won
//	public static String DRAW = "3"; // Indicate a draw
//	public static String CONTINUE = "4"; // Indicate to continue
    
    //216.240.62.66  public IP
	//10.0.12.184    Host IP 
	
}
