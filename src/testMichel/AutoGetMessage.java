package testMichel;

public class AutoGetMessage implements Runnable {

	MessageServiceClient client;
	boolean running = true;

	public AutoGetMessage(MessageServiceClient c) {
		this.client = c;
		System.out.println("debug AutoGetMessage Konstruktor" + c);
	}

	public void run() {

		while (running) {
			
			String msg = client.getMessage();
			
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
