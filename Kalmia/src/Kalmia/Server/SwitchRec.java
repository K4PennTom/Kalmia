package Kalmia.Server;
import java.awt.Polygon;
import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;

import com.csvreader.CsvReader;


public class SwitchRec{
	private String title = null;
	private String switchAddress = null;
	private String type = null;
	private LinkedList<String> params = new LinkedList<String>();
	private String factor = "1.0";
	private LinkedList<String> calendarTitles= new LinkedList<String>();
	private Polygon shape = new Polygon();
	private String circulatorMin = null;
	private String circulatorMax = null;

	public String getTitle() {
		return title;
	}
	public void setTitle(String t){
		title = t;
	}
	
	public String getSwitchAddress() {
		return switchAddress;
	}
	public void setSwitchAddress(String a){
		switchAddress = a;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String t){
		type = t;
	}
	
	public LinkedList<String> getParams(){
		return params;
	}
	public void setParams(String p){
		StringTokenizer t = new StringTokenizer(p,":");
		while(t.hasMoreTokens())
			params.add(t.nextToken());
	}
	
	public String getFactor() {
		return factor;
	}
	public void setFactor(String f){
		factor = f;
	}
	
	public LinkedList<String> getCalendarTitles() {
		return calendarTitles;
	}
	public void setCalendarTitles(String s){
		StringTokenizer t = new StringTokenizer(s,":");
		while(t.hasMoreTokens())
			calendarTitles.add(t.nextToken());
	}
	
	public Polygon getShape(){
		return shape;
	}
	public void setShape(String  p){
		shape = new Polygon();
		StringTokenizer t = new StringTokenizer(p,":");
		while(t.hasMoreTokens()){
			String[] xy = t.nextToken().split("'");
			if(xy.length==2){
				shape.addPoint(Integer.parseInt(xy[0]),Integer.parseInt(xy[1]));
			}else{
				System.out.println("Error: Point could not be read on shape: " + title);
			}
		}
	}
	
	public String getCirculatorMin() {
		return circulatorMin;
	}
	public void setCirculatorMin(String m){
		circulatorMin = m;
	}
	public String getCirculatorMax() {
		return circulatorMax;
	}
	public void setCirculatorMax(String m){
		circulatorMax = m;
	}
	

	//Reads CSV Reader and initializes record
	public boolean readCSVRec(CsvReader r){		
		try {
			setTitle(r.get(0));
		} catch (IOException e1) {
			System.out.println("Error: Switch title could not be read!");
			return false;
			//e1.printStackTrace();
		}
		
		try {
			setSwitchAddress(r.get(1));
		} catch (IOException e1) {
			System.out.println("Error: Switch ISY address could not be read!");
			return false;
			//e1.printStackTrace();
		}
		try {
			setType(r.get(2));
		} catch (IOException e1) {
			System.out.println("Error: Switch type could not be read!");
			return false;
			//e1.printStackTrace();
		}
		try {
			setParams(r.get(3));
		} catch (IOException e1) {
			System.out.println("Error: Switch parameters could not be read!");
			return false;
			//e1.printStackTrace();
		}
		try {
			setFactor(r.get(4));
		} catch (IOException e1) {
			System.out.println("Error: Switch factor could not be read!");
			return false;
			//e1.printStackTrace();
		}
		try {
			setCalendarTitles(r.get(5));
		} catch (IOException e1) {
			System.out.println("Error: Switch calendars could not be read!");
			return false;
			//e1.printStackTrace();
		}
		try {
			setShape(r.get(6));
		} catch (IOException e1) {
			System.out.println("Error: Switch polygon could not be read!");
			return false;
			//e1.printStackTrace();
		}
		try {
			setCirculatorMin(r.get(7));
		} catch (IOException e1) {
			System.out.println("Error: Switch circulatorMin could not be read!");
			return false;
			//e1.printStackTrace();
		}
		try {
			setCirculatorMax(r.get(8));
		} catch (IOException e1) {
			System.out.println("Error: Switch circulatorMax could not be read!");
			return false;
			//e1.printStackTrace();
		}
		return true;
	}
}
