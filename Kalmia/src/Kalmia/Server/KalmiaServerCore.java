package Kalmia.Server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.LinkedList;

import javafx.application.Application;
import javafx.stage.Stage;



import Kalmia.Server.CalendarLibrary;
import Kalmia.Server.CalendarListener;
import Kalmia.Server.ISYErrorHandler;
import Kalmia.Server.KalmiaISYClient;
import Kalmia.Server.SwitchLibrary;
import Kalmia.Server.WeatherWatcher;

import com.universaldevices.resources.errormessages.Errors;

public class KalmiaServerCore extends Application{
	
	private CalendarLibrary calendarLib;
	private SwitchLibrary switcheLib;
	private String homePath = "/home/thughes/resource_library/";
	private String calendarsFile = "calendars.csv";
	private String switchesFile = "switches.csv";
	private LinkedList<SwitchController> switchControllers = new LinkedList<SwitchController>();
	private LinkedList<CalendarListener> listeners = new LinkedList<CalendarListener>();
	private KalmiaISYClient myISY = null;
	private ServerConsole uConsole;
	private WeatherWatcher weather = new WeatherWatcher();
	
	private static final int PORT = 9027;
	private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		launch(args);
		new KalmiaServerCore();	
		ServerSocket listener = new ServerSocket(PORT);
		try {
            while (true) {
                new ClientHandler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
	}

	public static class ClientHandler extends Thread{
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {

                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String input;
                while ((input = in.readLine())!=null) {
                	if(input.compareTo("initialize")==0){
                		System.out.println("initialize new client");
                	}
                	out.println("I hear you!");
                }
            } catch (Exception e) {
				e.printStackTrace();
			} finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }

	public void start(Stage primaryStage) throws Exception {
		uConsole = new ServerConsole(primaryStage,homePath);
		// Create CalendarService and authenticate using ClientLogin
		//uConsole.setStatus("Authenticating to Calendar Service");
		//CalendarService service = new CalendarService("BBCDE-Calendartest-1");
		/*try {
			service.setUserCredentials(userName, userPassword);
		} catch (AuthenticationException e) {
			// Invalid credentials
			e.printStackTrace();
		}*/		
/*
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
			myISY.start("uuid:00:21:b9:00:e1:37","http://192.168.1.80"); 		
		}catch(Exception e){
			e.printStackTrace();
		}
		while(!myISY.isISYReady()){

			try {
				Thread.sleep(500);
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
						Thread.sleep(5);
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
}
