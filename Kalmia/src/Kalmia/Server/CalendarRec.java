package Kalmia.Server;
import java.io.IOException;
import com.csvreader.CsvReader;


public class CalendarRec{
	private String title = null;
	private String calendarID = null;
	
	public void setTitle(String t){
		title = t;
	}
	public void setID(String id){
		calendarID = id;
	}
	
	public String getTitle(){
		return title;
	}
	public String getID(){
		return calendarID;
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
			setID(new String(r.get(1)));
		} catch (IOException e) {
			System.out.println("Error: Calendar ID could not be read!");
			//e.printStackTrace();
		}
	}
}
