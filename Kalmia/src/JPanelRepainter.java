import javax.swing.JPanel;


public class JPanelRepainter extends Thread{
	private JPanel map;
	private Long sleepTime = (long) 60000;//1m
	public JPanelRepainter(JPanel m){
		map = m;
	}
	public void run(){
		while(true){
			map.repaint();
			//System.out.println("repainting");
			try {
				//System.out.println("repaint going to sleep");
				sleep(sleepTime);
				//System.out.println("repaint awake");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}
	public void setRefresh(boolean quick){
		if(quick) sleepTime = (long) 1000; //1s
		else sleepTime = (long) 60000;//1m
	}
}
