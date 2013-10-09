package testMichel;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;

public class MessageServiceClient {
	private MessageService msgService;
	static int timeOut = 5000;	// 5 Sek bis Sende- Emfpangs-Versuch abgebrochen wird

	//sucht in der RMI Registry nach der "MessageService"-Instanz
	public MessageServiceClient() throws RemoteException, NotBoundException{
		Registry registry = LocateRegistry.getRegistry("141.22.87.238");
		msgService = (MessageService)registry.lookup("MessageService");
		
		
	}

	
	public void sendMessage(String message){
				
		long stamp = new Date().getTime();
		while (new Date().getTime() - stamp < timeOut){		
		
			try {
				msgService.newMessage("Client1", message);
				System.out.println(message + " gesendet");
				break;
			} catch (RemoteException e) {
				
			}
		}
	}
	
	public String getMessage(){
		
		long stamp = new Date().getTime();
		String message = null;
		
		while (new Date().getTime() - stamp < timeOut){		
		
			try {
				message = msgService.nextMessage("Client1");
				break;
			} catch (RemoteException e) {
				
			}
		}
		return message;
	}
	
}