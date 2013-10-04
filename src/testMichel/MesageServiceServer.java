package testMichel;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

public class MesageServiceServer extends UnicastRemoteObject implements MessageService {

	/**
	 *  Auto generated serialVersionUID
	 */
	private static final long serialVersionUID = 2474050232696591221L;
	private static final int fifoSize = 3;
	private Queue<Message> DeliveryQueue = new LinkedList<Message>();
	

	public MesageServiceServer() throws RemoteException {
		super();
	}

	@Override
	public String nextMessage(String clientID) throws RemoteException {
		
		Message msg = DeliveryQueue.poll();
		if(msg != null){
			return msg.getFormatedDeliveryMessage();
		}else{
			return null;
		}
	}

	@Override
	public void newMessage(String clientID, String message) throws RemoteException {
		
		if(DeliveryQueue.size() >= fifoSize){
			Message delmsg = DeliveryQueue.remove();
			System.out.println("deleting Message: [ " + delmsg.getFormatedDeliveryMessage() + " ]");
		}
		DeliveryQueue.add(new Message(clientID, message));
		
	}
	

	public static void main(String[] args) {
		
		String registryServiceName = "MessageService";
		
		try{
			MessageService msgService = new MesageServiceServer();
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT); //RMI-Port 1099
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind(registryServiceName, msgService);
			System.out.println("Server \"" + registryServiceName + "\" initialisiert!");
			
		}catch(RemoteException e){
			System.err.println("Fehler bei Initialisierung des Servers:");
			e.printStackTrace();
		}

	}

}
