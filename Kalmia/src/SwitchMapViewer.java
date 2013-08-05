import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class SwitchMapViewer extends JPanel{
	/**
	 * 
	 */
	private String homePath;
	private StatusBar statusBar;
	private static final long serialVersionUID = 1L;
	BufferedImage img;
	private LinkedList<SwitchController> switchAreas = new LinkedList<SwitchController>();
	private boolean coreInitialized = false;
	private JPanelRepainter repainter = null;
	private boolean showXY=false;
	private int paintCount = 0;
	public static int CLIMATE_STATUS = 1;
	public static int CLIMATE_SET_POINT = 2;
	public static int LIGHTS_STATUS = 3;
	public static int LIGHTS_SET_POINT = 4;
	private int mode=0;
	private TempColorGenerator tempColor = new TempColorGenerator();

	public SwitchMapViewer(String hp,int m) {
		homePath=hp;
		
		mode = m;
		try {
			img = ImageIO.read(new File(homePath+"site_plan.JPG"));
		} catch (IOException e) {
		}
		//thermoAreas = new LinkedList<SwitchController>();
		addMouseMotionListener(new MouseMotionListener() {
			    public void mouseMoved(MouseEvent e) {
			    	if(showXY)statusBar.setStatusText2("("+e.getX()+","+e.getY()+")");
			    }

			    public void mouseDragged(MouseEvent e) {
			    	if(showXY)statusBar.setStatusText2("("+e.getX()+","+e.getY()+")");
			    }
			});
		
		repainter = new JPanelRepainter(this);
		repainter.start();
	}
	
	public void setRefresh(boolean quick){
		repainter.setRefresh(quick);
		repainter.interrupt();
	}
	public void addSwitches(LinkedList<SwitchController> s){
		if(mode==CLIMATE_STATUS||mode==CLIMATE_SET_POINT){
			for(SwitchController x:s){
				if (x.getSwitchRec().getType().compareTo("thermostat")==0){
					switchAreas.add(x);
				}
			}
		}else if(mode==LIGHTS_STATUS||mode==LIGHTS_SET_POINT){
			for(SwitchController x:s){
				if (x.getSwitchRec().getType().compareTo("light")==0){
					switchAreas.add(x);
				}
				if (x.getSwitchRec().getType().compareTo("circulator")==0){
					switchAreas.add(x);
				}
			}
		}	
	}
	public void paintComponent(Graphics g) {
		//System.out.println("painting");
		super.paintComponent(g);
		g.drawImage(img, 0, 0, null);
		//System.out.println("mode: " + mode + "Switch Areas#: " + switchAreas.size() + "Core init?:" +coreInitialized);
		if(coreInitialized){
			//System.out.println("drawing type: "+mode);
			Graphics2D g2d = (Graphics2D)g;
			//System.out.println("Switch areas #: " + switchAreas.size());
			for(int i=0; i<switchAreas.size(); i++) {
				//System.out.println("printing" + i);

				if(mode == CLIMATE_STATUS) drawPolygon(g2d, switchAreas.get(i).getShape(),switchAreas.get(i).getStatus());
				else if(mode == CLIMATE_SET_POINT){
					String tmp = switchAreas.get(i).getThermoMode();
					//System.out.println("Mode = " + switchAreas.get(i).getThermoMode());
					try{
						if(tmp.compareTo("1")==0||tmp.compareTo("2")==0){
							drawPolygon(g2d, switchAreas.get(i).getShape(),switchAreas.get(i).getSP());
						}
					}catch(NullPointerException e){}
				}
				else if(mode == LIGHTS_STATUS){
					//System.out.println("drawing light status");
					drawPolygon(g2d, switchAreas.get(i).getShape(),switchAreas.get(i).getStatus());
				}
				else if(mode == LIGHTS_SET_POINT){
					//System.out.println("drawing light SP");
					if(switchAreas.get(i).getSwitchState())	drawPolygon(g2d, switchAreas.get(i).getShape(),"255");
					else drawPolygon(g2d, switchAreas.get(i).getShape(),"0");
				}
				//draw temperature legend
				if((mode == CLIMATE_STATUS)||(mode == CLIMATE_SET_POINT)){
					//TODO
					drawLegend(g2d);
				}
				//System.out.println("drawing area.  mode = "+mode);

			}
		}
		paintCount++;
		statusBar.setStatusText("Paint Count: " +paintCount);
		/*if (thermoAreas.size()==0){
			//System.out.println("no controllers yet");
		}else{
			//System.out.println("painted " + thermoAreas.size() + " controllers.");
		}*/
		
	}
	public void coreInitialized(){
		coreInitialized = true;
	}
	public void addStatusBar(StatusBar s){
		statusBar = s;
	}
	public void setShowXY(boolean show){
		showXY = show;
	}
	public Dimension getPreferredSize() {
		if (img == null) {
			return new Dimension(100,100);
		} else {
			return new Dimension(img.getWidth(null), img.getHeight(null));
		}
	}
	private AlphaComposite makeComposite(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return(AlphaComposite.getInstance(type, alpha));
	}
	private void drawPolygon(Graphics2D g2d, Polygon shape, String t) {

		double transparancy = .85;
		Color paintColor = Color.gray;
		if(mode==CLIMATE_STATUS||mode==CLIMATE_SET_POINT){
			try{
				/*double red = 128;
				double green = 128;
				double blue = 128;
				double temp = Double.parseDouble(t)/2;
				double min = 55.0, max = 85.0;
				double range = max-min;
				double halfRange = range/2;
				blue = temp-(min+halfRange);
				if(blue<0)blue = 0.0;
				if(blue>halfRange)blue = halfRange;
				blue=halfRange - blue;
				blue=blue/halfRange;
				blue = blue*255;

				red = temp - (min);
				if(red<0)red = 0.0;
				if(red>halfRange)red = halfRange;
				red=red/halfRange;
				red = red*255;
				green = 0;
				paintColor = new Color((int)red,(int) green,(int) blue);*/
				paintColor = tempColor.calcColor(Double.parseDouble(t)/2);
			}catch(NumberFormatException n){
				
			}catch(NullPointerException n){

			}
		}else if(mode==LIGHTS_STATUS||mode==LIGHTS_SET_POINT){
			try{
				if(t.compareTo("255")==0){
					paintColor = new Color(255,255,0);
					transparancy = .25;
				}else if(t.compareTo("0")==0){
					paintColor = new Color(0,0,128);
					transparancy = .75;
				}
			}catch(NullPointerException e){
				paintColor = Color.GRAY;
				transparancy = .75;
			}
		}		
		
		g2d.setComposite(makeComposite((float)transparancy));
		g2d.setPaint(paintColor);
		g2d.fill(shape);
		//System.out.println("finish drawPolygon with "+shape.npoints + " points.");
	}
	private void drawLegend(Graphics2D g2d){
		int x = 30, y=740;
		double transparancy = .75;
		g2d.setComposite(makeComposite((float)transparancy));
		for (int i=0;i<500;i++){
			Double temp = (i/4.0)-5;
			g2d.setPaint(tempColor.calcColor(temp));
			g2d.fill(new Rectangle(x+i,y,1,20));
		}
	}
}
