package testMichel;

public class TestframeClient {
	public static void main(String[] args){
		
		ClientGui gui = new ClientGui();
		
		
		MessageServiceClient client= new MessageServiceClient("localhost");
		gui.setClient(client);
		gui.setConnected(client.isConnected());
		
		
	}
}
