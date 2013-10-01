package testMichel;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

public class DatumImpl extends UnicastRemoteObject implements Datum {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2474050232696591221L;

	public DatumImpl() throws RemoteException {
		super();
	}

	@Override
	public Date getDate() throws RemoteException {
		Date date = new Date();
		System.out.println("getDate() wurde vom Client aufgerufen " + date );
		return date;
	}

	public static void main(String[] args) {
		try{
			Datum datum = new DatumImpl();
			//Registrieren des DAtum-Objektes
			//LocateRegistry.createRegistry(Registry.REGISTRY_PORT); //RMI-Port 1099
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("datum", datum);
			System.out.println("Server initialisiert! :D yay!");
			
		}catch(RemoteException e){
			System.err.println("Fehler bei Initialisierung des Servers:");
			e.printStackTrace();
		}

	}

}
