package testMichel;

public class TestframeClient {
	public static void main(String[] args){
		
		ClientGui gui = new ClientGui();
		
		
		MessageServiceClient client= new MessageServiceClient();
		gui.setClient(client);
		gui.setConnected(client.isConnected());
		
		
	}
}
