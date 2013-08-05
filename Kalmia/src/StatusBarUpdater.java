
public class StatusBarUpdater extends Thread{
	private StatusBar statusBar;
	private Long sleepTime = (long) 1000;
	public StatusBarUpdater(StatusBar s){
		statusBar = s;
	}
	public void run(){
		while (true){
			statusBar.update();
			try {
				//System.out.println("status sleep");
				sleep(sleepTime);
				//System.out.println("status wake");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
