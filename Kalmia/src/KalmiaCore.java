

import java.util.LinkedList;

import Kalmia.Server.CalendarLibrary;
import Kalmia.Server.CalendarListener;
import Kalmia.Server.ISYErrorHandler;
import Kalmia.Server.KalmiaISYClient;
import Kalmia.Server.SwitchController;
import Kalmia.Server.SwitchLibrary;
import Kalmia.Server.WeatherWatcher;

import com.universaldevices.resources.errormessages.Errors;

public class KalmiaCore extends Thread{
	
	private CalendarLibrary calendarLib;
	private SwitchLibrary switcheLib;
	private String homePath = "/Kalmia/resource_library/";
	private String calendarsFile = "calendars.csv";
	private String switchesFile = "switches.csv";
	private LinkedList<SwitchController> switchControllers = new LinkedList<SwitchController>();
	private LinkedList<CalendarListener> listeners = new LinkedList<CalendarListener>();
	private KalmiaISYClient myISY = null;
	private UserConsole uConsole;
	private WeatherWatcher weather = new WeatherWatcher();
	
	public KalmiaCore(){
		uConsole = new UserConsole(homePath);
		
		
		// Create CalendarService and authenticate using ClientLogin
		//uConsole.setStatus("Authenticating to Calendar Service");
		//CalendarService service = new CalendarService("BBCDE-Calendartest-1");
		/*try {
			service.setUserCredentials(userName, userPassword);
		} catch (AuthenticationException e) {
			// Invalid credentials
			e.printStackTrace();
		}*/		

		//import libraries
		uConsole.setStatus("Gathering calendar information...");
		calendarLib = new CalendarLibrary(homePath + calendarsFile);
		uConsole.setStatus("Gathering swtich information...");
		switcheLib = new SwitchLibrary(homePath + switchesFile);
		//Initialize Weather
		uConsole.setStatus("Initializing Weather...");
		weather.start();
		uConsole.addWeather(weather);
		
		//initialize all calendar listeners
		uConsole.setStatus("Initializing Calendars...");
		for(int i=0; i<calendarLib.size();i++){
			CalendarListener tmp = new CalendarListener(calendarLib.get(i));
			//tmp.setWeatherWatcher(weather);
			tmp.start();
			listeners.add(tmp);
			//System.out.println("added calendar "+calendarLib.get(i).getTitle());
		}

		uConsole.setStatus("Initializing ISY Controller...");
		myISY=new KalmiaISYClient();
		Errors.addErrorListener(new ISYErrorHandler());
		try{
			uConsole.setStatus("Starting ISY Controller...");
			myISY.start("uuid:00:21:b9:02:07:7d","http://192.168.1.80"); 		
		}catch(Exception e){
			e.printStackTrace();
		}
		while(!myISY.isISYReady()){

			try {
				sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//myISY.reboot();
		//System.out.println("Current Outside Air Temperature: " + weather.getTemp() + "Deg F.");
		//Initialize all Insteon switches
		uConsole.setStatus("Initializing Switches...");
		for(int i=0; i<switcheLib.size();i++){
				SwitchController tmp = new SwitchController(switcheLib.get(i),listeners, myISY,homePath);
				tmp.start();
				tmp.setWeatherWatcher(weather);
				switchControllers.add(tmp);
				while(!tmp.isReady()){
					try {
						sleep(5);
					} catch (InterruptedException e) {
					}
					//System.out.println("Switch Controller "+tmp.getTitle()+" not Ready yet!");
				}
				//System.out.println("Switch Controller "+tmp.getTitle()+" has been added.");
		}
		uConsole.addSwitches(switchControllers);
		uConsole.addCalendars(listeners);
		//initialization of Core is complete
		uConsole.coreInitialized();
		uConsole.setStatus("Welcome to Kalmia!");
		
		
		
		//uConsole.refresh();
		/*try {
			sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		//myISY.changeNodeState("DFON", null, "19 A2 D2 1");	
		//myISY.queryNode("19 A2 D2 1");

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new KalmiaCore();	
	}
}
