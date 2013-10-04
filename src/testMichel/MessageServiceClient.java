package testMichel;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;

public class MessageServiceClient {
	private MessageService msgService;

	//sucht in der RMI Registry nach der "MessageService"-Instanz
	public MessageServiceClient() throws RemoteException, NotBoundException{
		Registry registry = LocateRegistry.getRegistry("WOOD");
		msgService = (MessageService)registry.lookup("MessageService");
	}

	public void printServerMessage() throws RemoteException{

		//sendet 4 Nachrichten an den Server
		for(int i = 0; i < 4; i++){
			msgService.newMessage("Client1", "voll tolle Nachricht " + i);
		}
		
		//holt drei nachrichten vom Server
		for(int i = 0; i < 3; i++){
			String msg = msgService.nextMessage("Client1");
			System.out.println("Message from Server:" + msg);
		}
		
	}
	
	public static void main(String[] args) {
		try{
			MessageServiceClient msgClient = new MessageServiceClient();
			msgClient.printServerMessage();
		}catch(NotBoundException e){
			System.err.println("Couldn't find remote object in registry");
			e.printStackTrace();
		}catch(RemoteException e){
			System.err.println("Error while Communicating with RMI-Server");
			System.err.println();
		}

	}

}
