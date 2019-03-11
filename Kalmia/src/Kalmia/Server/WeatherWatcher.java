package Kalmia.Server;
import java.text.DecimalFormat;

import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.core.OWM.Unit;
import net.aksingh.owmjapis.api.APIException;
import net.aksingh.owmjapis.model.CurrentWeather;

public class WeatherWatcher extends Thread{ 
	private CurrentWeather conditions;
	OWM owm = new OWM("638fc6615ab6129886b6a6b5e4dd3447");
	public WeatherWatcher (){
		
		//System.out.println("Weather is " + conditions.getWeather());
		//System.out.println("Current OAT: " + getTemp() +"\u00B0 F.");
		owm.setUnit(Unit.IMPERIAL);
        // getting current weather data
		query();

        //printing city name from the retrieved data
        System.out.println("City: " + conditions.getCityName());

        // printing the max./min. temperature
        System.out.println("Temperature: " + conditions.getMainData().getTempMax()
                            + "/" + conditions.getMainData().getTempMin() + "\'K");
	}
	public void run(){
		while(true){
			query();
			try {
				sleep(1800000);//sleep for a half hour
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
		}
	}
	private void query(){
        try {
			conditions = owm.currentWeatherByZipCode(19803);
		} catch (APIException e) {
			// TODO Auto-generated catch block
			System.err.println("Error getting weather");
			e.printStackTrace();
		}
	}
	public String getTemp(){
		Double temp = conditions.getMainData().getTemp();
		DecimalFormat df = new DecimalFormat("#.#");
		return df.format(temp);
	}
	public String getWeather(){
		return conditions.getWeatherList().get(0).getMainInfo()+" - "+conditions.getWeatherList().get(0).getDescription();
	}
}
