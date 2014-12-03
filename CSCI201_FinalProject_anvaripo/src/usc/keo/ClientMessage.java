package usc.keo;

import java.io.*;
//test github
public class ClientMessage implements Serializable, KeoConstants {
	private static final long serialVersionUID = 1L;

	private String message;
	private int type;
	
	ClientMessage(int type, String message) {
		this.type = type;
		this.message = message;
	}
	
	int getType() { return type; }
	String getMessage() { return message; }
	
}
