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
		msgService = (MessageService)registry.lookup("MessageServiceMichel");
	}

	//Gibt das Datum des gerufenen Servers auf der Konsole aus.
	public void printServerMessage() throws RemoteException{
		String msg = msgService.nextMessage("Client1oderso");
		System.out.println("Message from Server:" + msg);
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
