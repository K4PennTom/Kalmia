package Kalmia.Server;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JPanel;


public class StatusBar extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel statusText = new JLabel();
	private JLabel statusText2 = new JLabel();
	private JLabel temperature = new JLabel("Outside Temp: ");
	private JLabel time = new JLabel();
	private WeatherWatcher weather;
	private Calendar cal;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private StatusBarUpdater updater;
	private boolean weatherInitialized = false;
	public StatusBar(){
		setLayout(new GridLayout(1,1,1,1));
		add(statusText);
		add(statusText2);
		add(temperature);
		add(time);

		setPreferredSize(new Dimension(600,20));
		updater = new StatusBarUpdater(this);
		updater.start();
	}
	public void setStatusText(String s){
		statusText.setText(s);
	}
	public void setStatusText2(String s){
		statusText2.setText(s);
	}
	public void update(){
		if(weatherInitialized)
			temperature.setText(weather.getTemp()+"\u00B0 F, " + weather.getWeather());
		cal = Calendar.getInstance();
		time.setText(dateFormat.format(cal.getTime()));
	}
	public void addWeather(WeatherWatcher w){
		weather = w;
		weatherInitialized = true;
	}
}

