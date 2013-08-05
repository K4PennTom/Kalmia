import java.text.DecimalFormat;

import com.amphibian.weather.request.Feature;
import com.amphibian.weather.request.WeatherRequest;
import com.amphibian.weather.response.Conditions;
import com.amphibian.weather.response.WeatherResponse;

public class WeatherWatcher extends Thread{ 
	private WeatherRequest request;
	private Conditions conditions;
	public WeatherWatcher (){
		
		request = new WeatherRequest();
		request.setApiKey("8691d2505af7dac2");
		request.addFeature(Feature.CONDITIONS);
		WeatherResponse resp = request.query("19803");
		conditions = resp.getConditions();
		//System.out.println("Weather is " + conditions.getWeather());
		//System.out.println("Current OAT: " + getTemp() +"\u00B0 F.");
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
		WeatherResponse resp = request.query("19803");
		conditions = resp.getConditions();
	}
	public String getTemp(){
		Double temp = new Double(conditions.getTempF());
		DecimalFormat df = new DecimalFormat("#.#");
		return df.format(temp);
	}
	public String getWeather(){
		return conditions.getWeather();
	}
}
