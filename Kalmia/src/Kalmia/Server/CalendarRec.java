package Kalmia.Server;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.csvreader.CsvReader;


public class CalendarRec{
	private String title = null;
	private URL calendarURL = null;
	
	public void setTitle(String t){
		title = t;
	}
	public void setURL(URL u){
		calendarURL = u;
	}
	
	public String getTitle(){
		return title;
	}
	public URL getURL(){
		return calendarURL;
	}
	//Reads CSV Reader and initializes record
	public void readCSVRec(CsvReader r){
		try {
			setTitle(new String(r.get(0)));
		} catch (IOException e1) {
			System.out.println("Error: Calendar title could not be read!");
			//e1.printStackTrace();
		}
		try {
			setURL(new URL(r.get(1)));
		} catch (MalformedURLException e) {
			System.out.println("Error: Calendar URL is not valid!");
			//e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error: Calendar URL could not be read!");
			//e.printStackTrace();
		}
	}
}
