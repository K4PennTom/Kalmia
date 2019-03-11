

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import Kalmia.Server.CalendarLibrary;
import Kalmia.Server.CalendarListener;
import Kalmia.Server.ISYErrorHandler;
import Kalmia.Server.KalmiaISYClient;
import Kalmia.Server.SwitchController;
import Kalmia.Server.SwitchLibrary;
import Kalmia.Server.WeatherWatcher;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import java.security.GeneralSecurityException;
import com.universaldevices.resources.errormessages.Errors;

public class KalmiaCore extends Thread{
	
	private CalendarLibrary calendarLib;
	private SwitchLibrary switcheLib;
	private static final String HOME_PATH = "/Kalmia/resource_library/";
	private String calendarsFile = "calendars.csv";
	private String switchesFile = "switches.csv";
	private LinkedList<SwitchController> switchControllers = new LinkedList<SwitchController>();
	private LinkedList<CalendarListener> calendarListeners = new LinkedList<CalendarListener>();
	private KalmiaISYClient myISY = null;
	private UserConsole uConsole;
	private WeatherWatcher weather = new WeatherWatcher();
    private static final String APPLICATION_NAME = "Kalmia 2.1";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "/Kalmia/resource_library/tokens";
    
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";
    private PrintStream logfile = new PrintStream(new File(HOME_PATH+"SystemLog.txt"));
    
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = new FileInputStream(new File(HOME_PATH+CREDENTIALS_FILE_PATH));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
	
	public KalmiaCore() throws IOException, GeneralSecurityException{
		
		System.setOut(logfile);
		System.setErr(logfile);
		System.out.println("application name: "+APPLICATION_NAME);
		uConsole = new UserConsole(HOME_PATH);
		
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar calendarService = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

		//import libraries
		//initialize all calendar listeners
		uConsole.setStatus("Initializing Calendars...");
		calendarLib = new CalendarLibrary(HOME_PATH + calendarsFile);
		uConsole.setStatus("Gathering swtich information...");
		switcheLib = new SwitchLibrary(HOME_PATH + switchesFile);
		//Initialize Weather
		uConsole.setStatus("Initializing Weather...");
		weather.start();
		uConsole.addWeather(weather);
		

		for(int i=0; i<calendarLib.size();i++){
			CalendarListener tmp = new CalendarListener(calendarLib.get(i),calendarService);
			tmp.start();
			calendarListeners.add(tmp);
		}

		uConsole.setStatus("Initializing ISY Controller...");
		myISY=new KalmiaISYClient();
		Errors.addErrorListener(new ISYErrorHandler());
		try{
			uConsole.setStatus("Starting ISY Controller...");
			myISY.start("uuid:00:21:b9:02:4e:ad","http://192.168.1.80"); 		
		}catch(Exception e){
			e.printStackTrace();
		}
		while(!myISY.isISYReady()){

			try {
				sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//myISY.reboot();
		//System.out.println("Current Outside Air Temperature: " + weather.getTemp() + "Deg F.");
		//Initialize all Insteon switches
		uConsole.setStatus("Initializing Switches...");
		for(int i=0; i<switcheLib.size();i++){
				SwitchController tmp = new SwitchController(switcheLib.get(i),calendarListeners, myISY,HOME_PATH);
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
		uConsole.addCalendars(calendarListeners);
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
	public static void main(String[] args) throws IOException, GeneralSecurityException {
		new KalmiaCore();	
	}
}
