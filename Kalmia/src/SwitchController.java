import java.awt.Polygon;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.udi.insteon.client.InsteonConstants;
import com.universaldevices.client.NoDeviceException;

public class SwitchController extends Thread{
	private boolean switchIsOn = false;
	private static Long oneMin = new Long(60000);
	private static Long oneHour = new Long(3600000);
	private LinkedList<CalendarListener> mylisteners = new LinkedList<CalendarListener>();//a list of calendars that this switch responds to
	private KalmiaISYClient myISY;
	private SwitchRec switchInfo;
	private boolean switchReady = false;
	//private WeatherWatcher weather;
	private Long sleepTime = oneMin;
	private boolean keepLog = true;
	private String logPath;
	private File logFile;
	private boolean switchingMode = true;
	private boolean heatOrAC = false; //heat = true; cool = false
	private DateTime lastRun = new DateTime(0);
	private WeatherWatcher weather;
	
	public String getTitle(){
		return switchInfo.getTitle();
	}
	public SwitchController(SwitchRec rec, LinkedList<CalendarListener> allListeners,KalmiaISYClient isy,String hp){
		switchInfo = rec;
		myISY = isy;
		logPath = hp+"log/";
		//weather = w;
		//System.out.println("Initializing Switch: "+switchInfo.getTitle());
		LinkedList<String> calendarTitles = rec.getCalendarTitles();
		for(int i=0;i<calendarTitles.size();i++){
			for(int j=0; j<allListeners.size(); j++){
				if(calendarTitles.get(i).compareTo(allListeners.get(j).getTitle())==0){
					mylisteners.add(allListeners.get(j));
				}
			}			
		}
		/*while(getStatus()==null){
			try {
				sleep(5);
			} catch (InterruptedException e) {}
		}*/
		
		switchReady = true;
	}
	public DateTime getLastRun(){
		return lastRun;
	}
	public DateTime getNextRun(){
		return new DateTime(lastRun.getValue()+sleepTime);
	}
	public String getStatus(){
		//System.out.println("getStatus called on: " + getTitle());
		//if(switchInfo.getType().compareTo("thermostat")==0){
			try {
				return (String) myISY.getCurrValue( myISY.getNodes().get(switchInfo.getSwitchAddress()), InsteonConstants.DEVICE_STATUS);
			} catch (NoDeviceException e) {
				e.printStackTrace();
			}
		//}else if(switchInfo.getType().compareTo("light")==0){
		//	try {
		//		return(String) myISY.getCurrValue( myISY.getNodes().get(switchInfo.getSwitchAddress()), InsteonConstants.DEVICE_STATUS);
		//	} catch (NoDeviceException e) {
		//		e.printStackTrace();
		//	} catch (NullPointerException e){}
		//}
		return "";
	}
	public String getUiStatus(){
		//System.out.println("getUiStatus called on: " + getTitle());
		if(switchInfo.getType().compareTo("thermostat")==0){
			try {
				Double tmp = new Double((String) myISY.getCurrValue( myISY.getNodes().get(switchInfo.getSwitchAddress()), InsteonConstants.DEVICE_STATUS));
				tmp = tmp/2;
				return tmp.toString();
			} catch (NoDeviceException e) {
				e.printStackTrace();
			} catch (NullPointerException e){}
		}else if((switchInfo.getType().compareTo("light")==0)||(switchInfo.getType().compareTo("circulator")==0)){
			try {
				Double tmp = new Double((String) myISY.getCurrValue( myISY.getNodes().get(switchInfo.getSwitchAddress()), InsteonConstants.DEVICE_STATUS));
				tmp = tmp/255*100;
				if (tmp == 0) return "OFF";
				else if(tmp == 100) return "ON";
				return "ON " + tmp + "%";
			} catch (NoDeviceException e) {
				e.printStackTrace();
			} catch (NullPointerException e){}
		}
		return "";
	}
	public String getSP(){
		if(switchInfo.getType().compareTo("thermostat")==0){
			try {
				if(heatOrAC){
					return (String) myISY.getCurrValue( myISY.getNodes().get(switchInfo.getSwitchAddress()), InsteonConstants.CLIMATE_HEAT_SETPOINT);
				}else{
					return (String) myISY.getCurrValue( myISY.getNodes().get(switchInfo.getSwitchAddress()), InsteonConstants.CLIMATE_COOL_SETPOINT);
				}
			} catch (NoDeviceException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	public String getUiSP(){
		if(switchInfo.getType().compareTo("thermostat")==0){
			try {
				if(heatOrAC){
					Double tmp = new Double((String) myISY.getCurrValue( myISY.getNodes().get(switchInfo.getSwitchAddress()), InsteonConstants.CLIMATE_HEAT_SETPOINT));
					tmp = tmp/2;
					return tmp.toString();
				}else{
					Double tmp = new Double((String) myISY.getCurrValue( myISY.getNodes().get(switchInfo.getSwitchAddress()), InsteonConstants.CLIMATE_COOL_SETPOINT));
					tmp = tmp/2;
					return tmp.toString();
				}
			} catch (NoDeviceException e) {
				e.printStackTrace();
			} catch(NullPointerException e){}
		}
		return "";
	}
	public String getHumidity(){
		if(switchInfo.getType().compareTo("thermostat")==0){
			try {
				return (String) myISY.getCurrValue( myISY.getNodes().get(switchInfo.getSwitchAddress()), InsteonConstants.CLIMATE_HUMIDITY);
			} catch (NoDeviceException e) {
				e.printStackTrace();
			} catch(NullPointerException e){}
		}
		return "";
	}
	public String getUiHumidity(){
		if(switchInfo.getType().compareTo("thermostat")==0){
			try {
				return (String) myISY.getCurrValue( myISY.getNodes().get(switchInfo.getSwitchAddress()), InsteonConstants.CLIMATE_HUMIDITY)+"%";
			} catch (NoDeviceException e) {
				e.printStackTrace();
			} catch(NullPointerException e){}
		}
		return "";
	}
	public String getThermoMode(){
		if(switchInfo.getType().compareTo("thermostat")==0){
			try {
				return (String) myISY.getCurrValue( myISY.getNodes().get(switchInfo.getSwitchAddress()), InsteonConstants.CLIMATE_MODE);
			} catch (NoDeviceException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	public String getUiThermoMode(){
		if(switchInfo.getType().compareTo("thermostat")==0){
			try {
				String tmp = (String) myISY.getCurrValue( myISY.getNodes().get(switchInfo.getSwitchAddress()), InsteonConstants.CLIMATE_MODE);
					
				switch (Integer.parseInt(tmp)){
				case 0:
					return "Off";
				case 1:
					return "Heat";
				case 2:
					return "Cool";
				case 3:
					return "Auto";
				case 4:
					return "Fan";
				case 5:
					return "Program Auto";
				case 6:
					return "Program Heat";
				case 7:
					return "Program Cool";
				default:
					return "";
				}
			} catch (NoDeviceException e) {
				e.printStackTrace();
			} catch(NullPointerException e){}
			catch(NumberFormatException e){}
		}
		return "";
	}
	public boolean getSwitchState(){
		return switchIsOn;
	}
	public void evaluateState() {
		boolean occupied=false;
		long lookAhead = 0, lookBack = 0;
		Double temp = Double.parseDouble(weather.getTemp());
		
		if((switchInfo.getType().compareTo("thermostat")==0)||(switchInfo.getType().compareTo("circulator")==0)){
			
			Double partOne = Math.abs(temp-60)+10;
			lookAhead = (long) ((partOne/10)*3600000*Double.parseDouble(switchInfo.getFactor()));
			lookBack  = (long) (oneHour*0.25);
		}else if(switchInfo.getType().compareTo("light")==0){
			lookAhead = (long) (oneHour*0.25*Double.parseDouble(switchInfo.getFactor()));
			lookBack  = (long) (oneHour*0.5);
		}
		
		if(switchIsOn) lookAhead = lookAhead*2;

		for(int i=0; i<mylisteners.size() && !occupied;i++){
			try{
				List<CalendarEventEntry> events = mylisteners.get(i).getCurrentEvents().getEntries();
				for(int j=0; j<events.size();j++){
					if(((DateTime.now().getValue()+lookAhead) > events.get(j).getTimes().get(0).getStartTime().getValue()) && 
							((DateTime.now().getValue()-lookBack) < events.get(j).getTimes().get(0).getEndTime().getValue())){
						occupied = true;
						break;
					}
				}
			}catch(NullPointerException e){}
			catch(IndexOutOfBoundsException e){}
		}
		
		
		if((switchInfo.getType().compareTo("circulator")==0) &&
				(temp < Double.parseDouble(switchInfo.getCirculatorMin()))){
			occupied = true;
			//System.out.println(temp + " " + Double.parseDouble(switchInfo.getCirculatorMin()));
		}
		
		if((switchInfo.getType().compareTo("circulator")==0) &&
				(temp > Double.parseDouble(switchInfo.getCirculatorMax()))){
			occupied = false;	
			//System.out.println(temp + " " + Double.parseDouble(switchInfo.getCirculatorMax()));
		}
		
		
		if(!switchIsOn&&occupied){
			turnSwitchOn();			
		}else if(switchIsOn&&!occupied){
			//turn switch off
			turnSwitchOff();		
		}
		lastRun = DateTime.now();
		if(keepLog){
			logStatus();
		}
	}
	public void run(){
		while(true){
			evaluateState();
			try {
				sleep(sleepTime);
			} catch (InterruptedException e) {}
		}
	}
	private void logStatus(){

		try {
			Calendar cal = Calendar.getInstance();
			DateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			logFile = new File(logPath+fileDateFormat.format(cal.getTime())+" SwitchController "+switchInfo.getTitle()+".LOG.csv");
			
			// if file doesn't exists, then create it
			if (!logFile.exists()) 
				logFile.createNewFile();
						
    		FileWriter fileWritter = new FileWriter(logFile.getAbsolutePath(),true);
    	    BufferedWriter outFile = new BufferedWriter(fileWritter);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			
			outFile.write(dateFormat.format(cal.getTime()));
			outFile.write(","+getUiStatus());
			if(getSwitchState())
				outFile.write(",occupied");
			else
				outFile.write(",unoccupied");
			outFile.write(",");
			try{
				outFile.write(weather.getTemp());
			}catch(NullPointerException e){}
			if(switchInfo.getType().compareTo("thermostat")==0){
				outFile.write(","+getUiThermoMode());
				outFile.write(","+getUiSP());
				outFile.write(","+getUiHumidity());
			}
			outFile.write("\n");
			outFile.close();
		} catch (IOException e) {
			System.out.println("SwitchController Error: Can not write to file: "+logFile.getAbsolutePath());
		}
	}
	public boolean isReady(){
		return switchReady;
	}
	public SwitchRec getSwitchRec(){
		return switchInfo;
	}
	public Polygon getShape(){
		return switchInfo.getShape();
	}
	public void setRefresh(boolean quick){
		if(quick) sleepTime = (long) 1000;
		else sleepTime = oneMin;
	}
	public void setSwitchMode(boolean m){
		switchingMode = m;
	}
	public void setHeatOrAC(boolean b){
		heatOrAC = b;
	}
	public void setWeatherWatcher(WeatherWatcher w){
		weather = w;	
	}
	public boolean turnSwitchOn(){
		//turn switch on
		int counter1=0;
		int counter2=0;
		int timeout_limit = 5;
		

		
		if(switchInfo.getType().compareTo("thermostat")==0){
			String CLISP_PROP, CLISP_VAL, CLIMD_VAL;
			
			if(heatOrAC){
				CLISP_PROP="CLISPH";
				CLISP_VAL = switchInfo.getParams().get(0);
				CLIMD_VAL = "1";
			}else{
				CLISP_PROP="CLISPC";
				CLISP_VAL = switchInfo.getParams().get(2);
				CLIMD_VAL = "2";
			}
			System.out.println("Turn thermostat " +switchInfo.getTitle()+" at address '"+switchInfo.getSwitchAddress()+"' ON");
			
				try{
					//if(getHeatSP().compareTo(switchInfo.getParams().get(0))!=0)
						while(!myISY.changeNodeState(CLISP_PROP, CLISP_VAL, switchInfo.getSwitchAddress())&&counter1<timeout_limit&&(getSP().compareTo(CLISP_VAL)!=0)){
							try {
								System.out.println("Switch "+switchInfo.getTitle()+" is going to sleep.1");
								sleep(5000);
								counter1++;
							} catch (InterruptedException e) {}

						}
					//if(getThermoMode().compareTo("1")!=0)
						while(!myISY.changeNodeState("CLIMD", CLIMD_VAL, switchInfo.getSwitchAddress())&&counter2<timeout_limit&&(getThermoMode().compareTo(CLIMD_VAL)!=0)){
							try {
								System.out.println("Switch "+switchInfo.getTitle()+" is going to sleep.2");
								sleep(5000);
								counter2++;
							} catch (InterruptedException e) {}

						}

					if(counter1<timeout_limit && counter2<timeout_limit){
						System.out.println("Switch "+switchInfo.getTitle()+" is now On.1");
						switchIsOn = true;//be careful not to set this until Insteon is actually confirmed ON
						return true;

					}
				}catch (NullPointerException e){
					System.out.println("Thermostat " + switchInfo.getTitle() + " is not initialized yet!");
				}

		}else if(switchInfo.getType().compareTo("light")==0){
			System.out.println("Turn " +switchInfo.getTitle()+" at address '"+switchInfo.getSwitchAddress()+"' ON");
			
				try{
					System.out.println("STATUS " + switchInfo.getTitle() + " " + getStatus());
					//if(getStatus().compareTo("DFON")!=0)
						while(!myISY.changeNodeState("DFON", null, switchInfo.getSwitchAddress())&&counter1<timeout_limit&&(getStatus().compareTo("DFON")!=0)){
							try {
								System.out.println("Switch "+switchInfo.getTitle()+" is going to sleep.3");
								sleep(5000);
								counter1++;
							} catch (InterruptedException e) {}

						}

					if(counter1<timeout_limit){
						System.out.println("Switch "+switchInfo.getTitle()+" is now On.4");
						switchIsOn = true;//be careful not to set this until Insteon is actually confirmed ON
						return true;
					}
				}catch (NullPointerException e) {
					System.out.println("Switch " + switchInfo.getTitle()+" is not initialized yet!");
				}
			
		}else if(switchInfo.getType().compareTo("circulator")==0){
			System.out.println("Turn " +switchInfo.getTitle()+" at address '"+switchInfo.getSwitchAddress()+"' ON");

			try{
				System.out.println("STATUS " + switchInfo.getTitle() + " " + getStatus());
				//if(getStatus().compareTo("255")!=0)
					while(!myISY.changeNodeState("DFON", null, switchInfo.getSwitchAddress())&&counter1<timeout_limit&&(getStatus().compareTo("255")!=0)){
						try {
							System.out.println("Switch "+switchInfo.getTitle()+" is going to sleep.3");
							sleep(5000);
							counter1++;
						} catch (InterruptedException e) {}

					}

				if(counter1<timeout_limit){
					System.out.println("Switch "+switchInfo.getTitle()+" is now On.4");
					switchIsOn = true;//be careful not to set this until Insteon is actually confirmed ON
					return true;
				}
			}catch (NullPointerException e) {
				System.out.println("Switch " + switchInfo.getTitle()+" is not initialized yet!");
			}
		}
		return false;
	}
	public boolean turnSwitchOff(){
		//turn switch off
		int counter1=0;
		int counter2=0;
		int timeout_limit = 5;
		boolean result1;
		if(switchInfo.getType().compareTo("thermostat")==0){
			System.out.println("Turn thermostat " +switchInfo.getTitle()+" at address '"+switchInfo.getSwitchAddress()+"' OFF");
			String CLISP_PROP, CLISP_VAL, CLIMD_VAL;
			
			if(heatOrAC){
				CLISP_PROP="CLISPH";
				CLISP_VAL = switchInfo.getParams().get(1);
				CLIMD_VAL = "1";
			}else{
				CLISP_PROP="CLISPC";
				CLISP_VAL = switchInfo.getParams().get(3);
				CLIMD_VAL = "2";
			}
			
				try{
					//if(getHeatSP().compareTo(switchInfo.getParams().get(1))!=0)
						while(!myISY.changeNodeState(CLISP_PROP, CLISP_VAL, switchInfo.getSwitchAddress())&&counter1<timeout_limit&&(getSP().compareTo(CLISP_VAL)!=0)){
							try {
								System.out.println("Swith "+switchInfo.getTitle()+" is going to sleep.4");
								sleep(5000);
								counter1++;
							} catch (InterruptedException e) {}

						}
					//if(getThermoMode().compareTo("1")!=0)
						while(!myISY.changeNodeState("CLIMD", CLIMD_VAL, switchInfo.getSwitchAddress())&&counter2<timeout_limit&&(getThermoMode().compareTo(CLIMD_VAL)!=0)){
							try {
								System.out.println("Swith "+switchInfo.getTitle()+" is going to sleep.5");
								sleep(5000);
								counter2++;
							} catch (InterruptedException e) {}

						}
					if(counter1<timeout_limit && counter2<timeout_limit){
						System.out.println("Swith "+switchInfo.getTitle()+" is now Off.2");
						switchIsOn = false;//be careful not to set this until Insteon is actually confirmed ON
						return true;
					}
				} catch (NullPointerException e) {
					System.out.println("Thermostat " + switchInfo.getTitle() + " not initialized yet!");
				}

		}else if(switchInfo.getType().compareTo("light")==0){
			System.out.println("Turn " +switchInfo.getTitle()+" at address '"+switchInfo.getSwitchAddress()+"' OFF");
			
				try{
					//if(getStatus().compareTo("DFOF")!=0)
						while(!myISY.changeNodeState("DFOF", null, switchInfo.getSwitchAddress())&&counter1<timeout_limit&&(getStatus().compareTo("DFOF")!=0)){
							try {
								System.out.println("Switch "+switchInfo.getTitle()+" is going to sleep.6");
								sleep(5000);
								counter1++;
							} catch (InterruptedException e) {}
						}
					if(counter1<timeout_limit){
						System.out.println("Switch "+switchInfo.getTitle()+" is now Off.3");
						switchIsOn = false;//be careful not to set this until Insteon is actually confirmed ON
						return true;
					}
				} catch (NullPointerException e) {
					System.out.println("Light switch " + switchInfo.getTitle() + " not initialized yet!");
				}
			
		}else if(switchInfo.getType().compareTo("circulator")==0){
			System.out.println("Turn " +switchInfo.getTitle()+" at address '"+switchInfo.getSwitchAddress()+"' OFF");
			if(switchingMode){
				try{
					//if(getStatus().compareTo("0")!=0)
						while(!myISY.changeNodeState("DFOF", null, switchInfo.getSwitchAddress())&&counter1<timeout_limit&&(getStatus().compareTo("0")!=0)){
							try {
								System.out.println("Switch "+switchInfo.getTitle()+" is going to sleep.6");
								sleep(5000);
								counter1++;
							} catch (InterruptedException e) {}
						}
					if(counter1<timeout_limit){
						System.out.println("Switch "+switchInfo.getTitle()+" is now Off.3");
						switchIsOn = false;//be careful not to set this until Insteon is actually confirmed ON
						return true;
					}
				} catch (NullPointerException e) {
					System.out.println("Circulator " + switchInfo.getTitle() + " not initialized yet!");
				}
			}else{
				result1 = myISY.changeNodeState("DFOF", null, switchInfo.getSwitchAddress());

				if(result1){
					System.out.println("Switch "+switchInfo.getTitle()+" is now On.3");
					switchIsOn = false;//be careful not to set this until Insteon is actually confirmed ON
					return true;
				}
			}
		}
		return false;
	}
}
