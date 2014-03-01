package Kalmia.Server;
import java.io.IOException;
import java.net.URL;

import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.ServiceException;


public class CalendarListener extends Thread{
	private URL calendarURL = null;
	private boolean thermOccupied = false;
	private boolean lightOccupied = false;
	private String listenerTitle = null;
	//private CalendarService service = null;
	//private static Long fifteenMin = new Long(900000);
	private static Long tenMin = new Long(600000);
	private static Long halfHour = new Long(1800000);
	private static Long oneHour = new Long(3600000);
	//private static Long twoHours = new Long(7200000);
	private DateTime lastRun = new DateTime(0);
	//private Long[] thermParameters = {oneHour,fifteenMin,twoHours}; //{unOcc_Look_Ahead,Occ_Look_Back,Occ_Look_Ahead}
	//private Long[] lightParameters = {fifteenMin,fifteenMin,halfHour}; //{unOcc_Look_Ahead,Occ_Look_Back,Occ_Look_Ahead}
	private boolean listenerReady = false;
	private Long sleepTime = tenMin;
	//private WeatherWatcher weather;
	private String nextEventTitle = null;
	private When nextEventTimes = null;
	private CalendarEventFeed currentEvents = null;
	
	public CalendarListener(CalendarRec cr){
		listenerTitle = cr.getTitle();
		calendarURL = cr.getURL();
		//service = cs;
		listenerReady = true;
		lastRun.setTzShift(-240);
	}
	/*public boolean getOccupied(String type, String factor){
		if(type.compareTo("thermostat")==0)
			return thermOccupied;
		else if(type.compareTo("light")==0)
			return lightOccupied;
		else
			return false;
	}*/
	/*public void setWeatherWatcher(WeatherWatcher w){
		weather = w;	
	}*/
	public boolean isReady(){
		return listenerReady;
	}
	public DateTime getLastRun(){
		lastRun.setTzShift(-240);
		return lastRun;
	}
	public DateTime getNextRun(){
		lastRun.setTzShift(-240);
		return new DateTime(lastRun.getValue()+sleepTime);
	}
	public String getNextEventTitle(){
		if (nextEventTitle!=null)
			return nextEventTitle;
		else
			return "";
	}
	public String getNextEventST(){
		if (nextEventTimes!=null){
			DateTime time = nextEventTimes.getStartTime();
			time.setTzShift(-240);
			return time.toUiString();
		}else
			return "";
	}
	public String getTitle(){
		return listenerTitle;
	}
	public CalendarEventFeed getCurrentEvents(){
		return currentEvents;
	}
	public boolean isThermOccupied(){
		return thermOccupied;
	}
	public boolean isLightOccupied(){
		return lightOccupied;
	}
	/*public void query(){
		//check thermostats
		//thermOccupied = calendarQuery(thermOccupied,"thermostat");
		//now check lights
		//lightOccupied = calendarQuery(lightOccupied,"light");
		
		lastRun = DateTime.now();
	}*/
	/*public long calcLookAheadTime(String type,String param){
		if(type.compareTo("thermostat")==0){
			Double temp = Double.parseDouble(weather.getTemp());
			Double partOne = Math.abs(temp-60)+10;
			long result = (long) ((partOne/10)*3600000);
			if(param.compareTo("unocc_lookahead")==0){
				return result;
			} else if(param.compareTo("occ_lookback")==0){
				return fifteenMin;
			} else if(param.compareTo("occ_lookahead")==0){
				return result*2;
			}
		}else if(type.compareTo("light")==0){
			if(param.compareTo("unocc_lookahead")==0){
				return fifteenMin;
			} else if(param.compareTo("occ_lookback")==0){
				return halfHour;
			} else if(param.compareTo("occ_lookahead")==0){
				return oneHour;
			}
		}
		return fifteenMin;
	}*/
	public void queryCalendar(){
		//boolean result = false;
		CalendarService service = new CalendarService("BBCDE-Calendartest-1");
		try {
			CalendarQuery myQuery = new CalendarQuery(calendarURL);
			//check thermostats
			/*if(!occupied){
				myQuery.setMinimumStartTime(DateTime.now());
				myQuery.setMaximumStartTime(new DateTime(DateTime.now().getValue()+calcLookAheadTime(type, "unocc_lookahead")));
			}else{
				myQuery.setMinimumStartTime(new DateTime(DateTime.now().getValue()-calcLookAheadTime(type, "occ_lookback")));
				myQuery.setMaximumStartTime(new DateTime(DateTime.now().getValue()+calcLookAheadTime(type, "occ_lookahead")));
			}*/
			
			myQuery.setMinimumStartTime(new DateTime(DateTime.now().getValue()-halfHour));
			myQuery.setMaximumStartTime(new DateTime(DateTime.now().getValue()+oneHour*8));
			// Send the request and receive the response:
			currentEvents = service.query(myQuery, CalendarEventFeed.class);
			listenerTitle = currentEvents.getTitle().getPlainText();
			//System.out.println("number of events found on calendar "+ listenerTitle +": " + resultFeeds.getEntries().size());
				
			if(currentEvents.getEntries().size()>0){
				nextEventTitle = currentEvents.getEntries().get(0).getTitle().getPlainText();
				nextEventTimes = currentEvents.getEntries().get(0).getTimes().get(0);
				//return true;
			}else{	
				nextEventTitle = null;
				nextEventTimes = null;
				//return false;
			}
			
		} catch (IOException e) {
			// Communications error
			System.err.println("Error: CalendarQuery: There was a problem communicating with the service.");
			//e.printStackTrace();
		} catch (ServiceException e) {
			// Server side error
			System.err.println("Error: CalendarQuery: The server had a problem handling your request.");
			//e.printStackTrace();
		}
		//return result;
		lastRun = DateTime.now();
		lastRun.setTzShift(-240);
	}
	
	public void run(){
		while(true){
			//System.out.println("Checking "+listenerTitle);
			queryCalendar();
			try {
				sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}
	public void setRefresh(boolean quick){
		if(quick){
			sleepTime = (long) 10000;//10seconds
		}else{
			sleepTime = tenMin;
		}
	}
}
