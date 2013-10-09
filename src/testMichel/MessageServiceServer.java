package testMichel;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.*;

public class MessageServiceServer extends UnicastRemoteObject implements MessageService {

	/**
	 *  Auto generated serialVersionUID
	 */
	private static final long serialVersionUID = 2474050232696591221L;
	private static final int fifoSize = 100;
	private static final int waitingTime = 600000; // Merkzeit in ms
	private static Log log;
	private Queue<Message> DeliveryQueue = new LinkedList<Message>();
	private HashMap<String, DeliveryRecord> deliveryRecords = new HashMap<>();
	private Lock mutex = new ReentrantLock(true);
	

	public MessageServiceServer() throws RemoteException {
		super();
	}

	@Override
	public String nextMessage(String clientID) throws RemoteException {
		
		mutex.lock();
		
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
					    mutex.unlock();
					    return m.getFormatedDeliveryMessage();
					}else{
						System.out.println("DEBUG: Bereits übertragen: " + m.getFormatedDeliveryMessage());
					}
				}	
				
				mutex.unlock();
				return null;
			}
		} //..es muss also erst ein DR fuer diesen CLient angelegt werden
		
		
		if(msgToSend == null){ //wenn keine Nachricht mittels DR gefunden...
			msgToSend = DeliveryQueue.peek(); //..die aelteste aus der queue holen...
		}
				
		if(msgToSend != null){ //..und versenden + DR erstellen..
			System.out.println("DEBUG: erstelle deliveryRecord für client '" + clientID + "' Message[" + msgToSend.getMessageId() + "]");
			deliveryRecords.put(clientID, new DeliveryRecord(msgToSend));
			mutex.unlock();
			return msgToSend.getFormatedDeliveryMessage();
		}else{ //..ausser es gibt auch keine Nachricht in der queue 
			System.out.println("DEBUG: keine Nachricht für client '" + clientID + "'");
			mutex.unlock();
			return null;			
		}
	}

	@Override
	public void newMessage(String clientID, String message) throws RemoteException {
		
		//Toolkit.getDefaultToolkit().beep();
		mutex.lock();
		
		if(DeliveryQueue.size() >= fifoSize){
			Message delmsg = DeliveryQueue.remove();
			System.out.println("deleting Message: [ " + delmsg.getFormatedDeliveryMessage() + " ]");
		}
		Message m = new Message(clientID, message);
		DeliveryQueue.add(m);
		System.out.println("DEBUG: Message erhalten: " + m.getFormatedDeliveryMessage());
		
		mutex.unlock();
		
	}
	

	public static void main(String[] args) {
		
		String registryServiceName = "MessageService";
		Registry registry = null;
		
		try{
			log = new Log();
			MessageService msgService = new MessageServiceServer();
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT); //RMI-Port 1099
			registry = LocateRegistry.getRegistry();
			registry.rebind(registryServiceName, msgService);
			System.out.println("Server \"" + registryServiceName + "\" initialisiert!");
			log.append("SERVER gestartet! Service als '" + registryServiceName + "' in der RMI registry angemeldet");
			
			
			BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
			String zeile = null;
			
			do{
				zeile = console.readLine();
				//System.out.println("gelesen: " + zeile);
			}while( !zeile.equals("stop"));
			registry.unbind(registryServiceName);
			UnicastRemoteObject.unexportObject(msgService,true); 
			System.out.println("unbound: " + registryServiceName );
			
		}catch(RemoteException e){
			System.err.println("Fehler bei Initialisierung des Servers:");
			e.printStackTrace();
		}catch (IOException e) {
			System.err.println("ERROR: Fehler beim Initialisieren des Log's / beim lesen von der Konsole");
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.err.println("ERROR: unbind -> Registry wasn't bound");
			e.printStackTrace();
		} 


	}

}
