package testMichel;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;

import server.MessageService;;


public class MessageServiceClient implements Runnable{
	
	public class ConcurrentMessageGetter implements Runnable{
		
		String msg = null;
		@Override
		public void run() {
			
			try {
				msg = MessageServiceClient.this.msgService.nextMessage(hostName);
				//System.out.println("Hurra thread: " + msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public String getMessage(){ return msg;}
	}
	
	private MessageService msgService;
	static int timeOut = 5000;	// 5 Sek bis Sende- Emfpangs-Versuch abgebrochen wird
	private String hostName;
	private boolean isConnected = false;
	private boolean running = false;

	//sucht in der RMI Registry nach der "MessageService"-Instanz
	public MessageServiceClient(String serverHost){

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
		
			Registry registry;
			try {
				registry = LocateRegistry.getRegistry(serverHost);
				msgService = (MessageService)registry.lookup("MessageService");
				isConnected = true;
				break;
			} catch (NotBoundException e) {
				System.err.println("Couldn't find remote object in registry");
				//e.printStackTrace();
			} catch (RemoteException e) {
				System.err.println("Server not found");
				e.printStackTrace();
			}
		}
		//ClientGui.setConnected(true);
		
	}

	
	public void sendMessage(String message){
				
		long stamp = new Date().getTime();
		while (new Date().getTime() - stamp < timeOut){		
		
			try {
				msgService.newMessage(hostName, message);
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
			ConcurrentMessageGetter cmg = new ConcurrentMessageGetter();
			Thread getMsg = new Thread(cmg);
			getMsg.start();	
			
			try {
				getMsg.join(1000L);
				if(getMsg.isAlive()){
					getMsg.interrupt();
				}else{
					return cmg.getMessage();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		System.err.println("ERROR: Server nicht erreichbar?");
		
		return message;
	}
	
	public boolean isConnected(){
		return isConnected;
	}
	

	
	//THREADKRAM  vv

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

	public void setRunning(boolean running) {
		this.running = running;
	}
		
}