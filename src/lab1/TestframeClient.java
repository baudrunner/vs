package lab1;

public class TestframeClient {
	public static void main(String[] args){
		
		ClientGui gui = new ClientGui();
		
		//141.22.89.171
		MessageServiceClient client= new MessageServiceClient("localhost");
		gui.setClient(client);
		gui.setConnected(client.isConnected());
		
		
	}
}
