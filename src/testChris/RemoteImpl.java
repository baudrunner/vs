package testChris;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteImpl extends UnicastRemoteObject implements TestRemote {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4003012279648710659L;

	protected RemoteImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isLoginValid(String username) throws RemoteException {
		if(username.equals("test")){
			return true;
		}
		return false;
	}

}
