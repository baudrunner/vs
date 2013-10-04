package testChris;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {

	public static void main(String[] args) throws RemoteException, AlreadyBoundException {
		RemoteImpl impl = new RemoteImpl();
		Registry registry = LocateRegistry.createRegistry(Constant.RMI_PORT);
		registry.bind(Constant.RMI_ID, impl);
		System.out.println("server started");
	}
}