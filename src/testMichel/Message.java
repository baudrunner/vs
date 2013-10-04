package testMichel;

import java.util.Date;

public class Message {
	
	private static int idCounter;
	
	private final String clientID;
	private final Date arrivalTime;
	private final int msgID;
	private final String message;
	
	public Message(String clientId, String msg){
		arrivalTime = new Date();
		clientID = clientId;
		msgID= idCounter++;
		message = msg;
	}
	
	public String getFormatedDeliveryMessage(){
		return msgID + " " + clientID + ":" + message + " " + arrivalTime;
	}

}
