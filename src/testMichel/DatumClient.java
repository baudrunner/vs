package testMichel;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;

public class DatumClient {
	private Datum datum;

	//sucht in der RMI Registry nach der "datum"-Instanz
	public DatumClient() throws RemoteException, NotBoundException{
		Registry registry = LocateRegistry.getRegistry("WOOD");
		datum = (Datum)registry.lookup("datum");
	}

	//Gibt das Datum des gerufenen Servers auf der Konsole aus.
	public void printServerDate() throws RemoteException{
		Date date = datum.getDate();
		System.out.println("Datum vom Zörvä:" + date);
	}
	
	public static void main(String[] args) {
		try{
			DatumClient datumClient = new DatumClient();
			datumClient.printServerDate();
		}catch(NotBoundException e){
			System.err.println("Das Remote Objekt konnte in der Registry nicht gefunden werden.");
			e.printStackTrace();
		}catch(RemoteException e){
			System.err.println("Fehler bei der Kommunikation mit dem RMI-Server");
			System.err.println();
		}

	}

}
