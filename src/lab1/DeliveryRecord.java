package lab1;

import java.util.Date;

public class DeliveryRecord {
	
	private final Date deliveryTime;
    private final Message msg;
	
    public DeliveryRecord(Message message){
    	deliveryTime = new Date();
    	msg = message;
    }
    
    public Message getMessage(){
    	return msg;
    }
    
    public Date getDeliveryTime(){
    	return deliveryTime;
    }

}
