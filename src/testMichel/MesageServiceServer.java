package testMichel;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
//import java.rmi.server.RMIClientSocketFactory;
//import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

public class MesageServiceServer extends UnicastRemoteObject implements MessageService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2474050232696591221L;

	public MesageServiceServer() throws RemoteException {
		super();
	}

	@Override
	public String nextMessage(String clientID) throws RemoteException {
		// TODO Auto-generated method stub
		return "Hier koennte die sinnvolle Message stehen";
	}

	@Override
	public void newMessage(String clientID, String message) throws RemoteException {
		System.out.println("<messageID> " + clientID + ":" + message + " " + new Date());
	}
	
//	@Override
//	public Date getDate() throws RemoteException {
//		Date date = new Date();
//		System.out.println("getDate() wurde vom Client aufgerufen " + date );
//		return date;
//	}

	public static void main(String[] args) {
		
		String registryServiceName = "MessageServiceMichel";
		
		
		try{
			MessageService msgService = new MesageServiceServer();
			//Registrieren des DAtum-Objektes
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
