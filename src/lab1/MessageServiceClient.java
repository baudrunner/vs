package lab1;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;

import server.MessageService;;


public class MessageServiceClient implements Runnable{
	
	private final String msgServiceName = "MessageService";
	private MessageService msgService;
	static int timeOut = 5000;	// 5 Sek bis Sende- Emfpangs-Versuch abgebrochen wird
	private String hostName;
	private String serverHostName;
	private boolean isConnected = false;
	private boolean running = false;

	public MessageServiceClient(String serverHost){
		
		serverHostName = serverHost; // fuer spaetere verwendung (reconnect) zwischenspeichern
		
		try {
			hostName = InetAddress.getLocalHost().getHostName();
			System.out.println("HostName: " + hostName);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.setProperty("java.security.policy","allesErlaubt.policy");
		
		if (System.getSecurityManager() == null) {
		    System.setSecurityManager(new SecurityManager());
		}
		
		long stamp = new Date().getTime();
		while (new Date().getTime() - stamp < timeOut){
		
			msgService = tryToGetMessageService(serverHostName,msgServiceName);
			if(msgService != null){ isConnected = true; break; }
		}
				
	}

	
	public void sendMessage(String message){
				
		long stamp = new Date().getTime();
		//innerhalb des Zeitintervalls "timeOut" versuchen, eine Nachricht zu senden
		//...falls dies Fehlschlaegt wird versucht die Verbindung erneut aufzubauen
		//...falls dies auch fehlschlaegt ignorieren
		while (new Date().getTime() - stamp < timeOut){		
			try {
				msgService.newMessage(hostName, message);
				System.out.println(message + " gesendet");
				break;
			} catch (RemoteException e) {
				e.printStackTrace();
				System.err.println("message-Send fehlgeschlagen!...");
				msgService = tryToGetMessageService(serverHostName, msgServiceName);
			} catch (NullPointerException e){
				System.err.println("Letzter versuch den Server zu erreichen fehlgeschlagen -> versuche es erneut...");
				msgService = tryToGetMessageService(serverHostName, msgServiceName);
			}
			
		}
	}
	
	public String getMessage(){
		
		long stamp = new Date().getTime();
		String message = null;
		
		//innerhalb des Zeitintervalls "timeOut" versuchen, eine Nachricht abzuholen
		//...falls dies Fehlschlaegt wird versucht die Verbindung erneut aufzubauen
		//...falls dies auch fehlschlaegt wird null zurueckgegeben
		while (new Date().getTime() - stamp < timeOut){		
			try {
				message = msgService.nextMessage(hostName);
				return message;
			} catch (RemoteException e) {
				e.printStackTrace();
				msgService = tryToGetMessageService(serverHostName, msgServiceName);
			} catch (NullPointerException e){
				System.err.println("Letzter versuch den Server zu erreichen fehlgeschlagen -> versuche es erneut...");
				msgService = tryToGetMessageService(serverHostName, msgServiceName);
			}

// Die Folgenden Auskommentierten Zeilen erlauben es auf Systemen bei denen der entfernte aufruf dauerhaft blockiert (aufgetreten bei Linux Mint 14) 
// Den Aufruf in einem Thread zu versuchen, und diesen Versuch innerhalb einer konfigurierbaren Zeit abzubrechen
			
//			ConcurrentMessageGetter cmg = new ConcurrentMessageGetter();
//			Thread getMsg = new Thread(cmg);
//			getMsg.start();	
//			
//			try {
//				getMsg.join(1000L);
//				if(getMsg.isAlive()){
//					getMsg.interrupt();
//				}else{
//					return cmg.getMessage();
//				}
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
		}
		
		System.err.println("ERROR: Server nicht erreichbar?");
		return null;
	}
	
	public boolean isConnected(){
		return isConnected;
	}
	
	
	//Thread Implementierung um Nachrichten nebenl�ufig abzuholen
	public void run() {
		while (running) {		
			String msg = getMessage();
			
			if(msg != null){
				ClientGui.setText(msg);
			}else{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * um von der Gui aus, das automatische Abholen der Nachrichten zu aktivieren
	 * @param running
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	
	/**
	 * versucht eine Referenz auf ein entferntes MessageService-Objekt vom Server zu holen
	 * @param srvHost
	 * @param serviceName
	 * @return
	 */
	private MessageService tryToGetMessageService(String srvHost, String serviceName){
		Registry registry;
		MessageService msgServiceObj;
		try {
			registry = LocateRegistry.getRegistry(srvHost);
			msgServiceObj = (MessageService)registry.lookup(serviceName);
			return msgServiceObj;
		} catch (NotBoundException e) {
			System.err.println("Couldn't find remote object in registry");
			//e.printStackTrace();
		} catch (RemoteException e) {
			System.err.println("Server not found");
			//e.printStackTrace();
		}
		
		return null;
	}
	
	
// auf manchen Systemen als workaround einsetzbar -> siehe getMessage() 
//	/**
//	 * einfaches Runnable, das erlaubt Messages Abzuholen
//	 * @author Michel
//	 *
//	 */
//	public class ConcurrentMessageGetter implements Runnable{
//		
//		String msg = null;
//		@Override
//		public void run() {
//			try {
//				msg = MessageServiceClient.this.msgService.nextMessage(hostName);
//				//System.out.println("Hurra thread: " + msg);
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}	
//		public String getMessage(){ return msg;}
//	}
		
}