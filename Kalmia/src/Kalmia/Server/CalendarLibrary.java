package Kalmia.Server;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import com.csvreader.CsvReader;


public class CalendarLibrary extends LinkedList<CalendarRec> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CalendarLibrary(String path){
		try {
			//create a new CSV reader
			CsvReader recordReader = new CsvReader(path);
			//Check that the library file has headers
			try {
				recordReader.readHeaders();
			} catch (IOException e) {
				
				System.out.println("Error: Calendar list file has no headers!");
				//e.printStackTrace();
			}
			//Read the records
			try {
				
				recordReader.readRecord();
				while(recordReader.get(0) != ""){
					CalendarRec tmp = new CalendarRec();
					tmp.readCSVRec(recordReader);
					add(tmp);
					recordReader.readRecord();
				}
				System.out.println(size() +" calendars read.");
				recordReader.close();
			} catch (IOException e) {
				System.out.println("Error: Calendar list file has no records!");
				//e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			System.out.println("Error: Calendar list file could not be found at location: " + path);
			//e1.printStackTrace();
		}
	}
	
	public int indexOfTitle(String title){
		for(int i=0;i<size();i++){
			if(title.compareTo(get(i).getTitle())==0)return i;
		}
		return -1;
	}
}
