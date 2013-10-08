package testMichel;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class MessageServiceServer extends UnicastRemoteObject implements MessageService {

	/**
	 *  Auto generated serialVersionUID
	 */
	private static final long serialVersionUID = 2474050232696591221L;
	private static final int fifoSize = 3;
	private static final int waitingTime = 6000; // Merkzeit in ms
	private static Log log;
	private Queue<Message> DeliveryQueue = new LinkedList<Message>();
	private HashMap<String, DeliveryRecord> deliveryRecords = new HashMap<>();
	

	public MessageServiceServer() throws RemoteException {
		super();
	}

	@Override
	public String nextMessage(String clientID) throws RemoteException {
		
		log.append("Client [" + clientID + "] Anforderung fuer Nachricht");
	
	    DeliveryRecord dr = deliveryRecords.get(clientID);
	    Message msgToSend = null;
	    	    
		if(dr != null){ //bereits eintrg fuer Client vorhanden vorhanden
			if(new Date().getTime() - dr.getDeliveryTime().getTime() > waitingTime){ //DR loeschen falls Merkzeit ueberschritten
				deliveryRecords.remove(clientID);
				System.out.println("DEBUG: lösche deliveryRecord für client '" + clientID + "'");
			}else{
				
				Iterator<Message> it = DeliveryQueue.iterator();
				
				for( Message m : DeliveryQueue){
					if(m.getMessageId() > dr.getMessage().getMessageId()){
					    msgToSend = m;
					    System.out.println("DEBUG: aktualisiere deliveryRecord für client '" + clientID + "'");
					    deliveryRecords.remove(clientID);
					    deliveryRecords.put(clientID, new DeliveryRecord(m));
					    return m.getFormatedDeliveryMessage();
					}else{
						System.out.println("DEBUG: Bereits übertragen: " + m.getFormatedDeliveryMessage());
					}
				}	
				
				return null;
			}
		} //..es muss also erst ein DR fuer diesen CLient angelegt werden
		
		
		if(msgToSend == null){ //wenn keine Nachricht mittels DR gefunden...
			msgToSend = DeliveryQueue.peek(); //..die aelteste aus der queue holen...
		}
				
		if(msgToSend != null){ //..und versenden + DR erstellen..
			System.out.println("DEBUG: erstelle deliveryRecord für client '" + clientID + "' Message[" + msgToSend.getMessageId() + "]");
			deliveryRecords.put(clientID, new DeliveryRecord(msgToSend));
			return msgToSend.getFormatedDeliveryMessage();
		}else{ //..ausser es gibt auch keine Nachricht in der queue 
			System.out.println("DEBUG: keine Nachricht für client '" + clientID + "'");
			return null;			
		}
	}

	@Override
	public void newMessage(String clientID, String message) throws RemoteException {
		
		if(DeliveryQueue.size() >= fifoSize){
			Message delmsg = DeliveryQueue.remove();
			System.out.println("deleting Message: [ " + delmsg.getFormatedDeliveryMessage() + " ]");
		}
		Message m = new Message(clientID, message);
		DeliveryQueue.add(m);
		System.out.println("DEBUG: Message erhalten: " + m.getFormatedDeliveryMessage());
		
	}
	

	public static void main(String[] args) {
		
		String registryServiceName = "MessageService";
		
		try{
			log = new Log();
			MessageService msgService = new MessageServiceServer();
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT); //RMI-Port 1099
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind(registryServiceName, msgService);
			System.out.println("Server \"" + registryServiceName + "\" initialisiert!");
			log.append("SERVER gestartet! Service als '" + registryServiceName + "' in der RMI registry angemeldet");
			
			
		}catch(RemoteException e){
			System.err.println("Fehler bei Initialisierung des Servers:");
			e.printStackTrace();
		}catch (IOException e) {
			System.err.println("ERROR: Fehler beim Initialisieren des Log's");
			e.printStackTrace();
		}
		
		

	}

}
