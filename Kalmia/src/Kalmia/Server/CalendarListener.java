package Kalmia.Server;

import java.io.IOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import java.util.List;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CalendarListener extends Thread {
	private String calendarID = null;
	private boolean thermOccupied = false;
	private boolean lightOccupied = false;
	private String listenerTitle = null;
	// private CalendarService service = null;
	// private static Long fifteenMin = new Long(900000);
	private static Long tenMin = new Long(600000);
	private static Long oneHour = new Long(3600000);
	// private static Long twoHours = new Long(7200000);
	private ZonedDateTime lastRun;
	// private Long[] thermParameters = {oneHour,fifteenMin,twoHours};
	// //{unOcc_Look_Ahead,Occ_Look_Back,Occ_Look_Ahead}
	// private Long[] lightParameters = {fifteenMin,fifteenMin,halfHour};
	// //{unOcc_Look_Ahead,Occ_Look_Back,Occ_Look_Ahead}
	private boolean listenerReady = false;
	private Long sleepTime = tenMin;
	// private WeatherWatcher weather;
	private String nextEventTitle = null;
	private ZonedDateTime nextEventStart = null;
	private List<Event> currentEvents = null;
	private Calendar calendarService = null;

	public CalendarListener(CalendarRec cr, Calendar c) {
		listenerTitle = cr.getTitle();
		calendarService = c;
		calendarID = cr.getID();
		// service = cs;
		listenerReady = true;
	}

	public boolean isReady() {
		return listenerReady;
	}

	public ZonedDateTime getLastRun() {
		return lastRun;
	}

	public ZonedDateTime getNextRun() {
		return lastRun.plusSeconds(sleepTime/1000);
	}

	public String getNextEventTitle() {
		if (nextEventTitle != null)
			return nextEventTitle;
		else
			return "";
	}

	public ZonedDateTime getNextEventST() {
			return nextEventStart;
	}

	public String getTitle() {
		return listenerTitle;
	}

	public List<Event> getCurrentEvents() {
		return currentEvents;
	}

	public boolean isThermOccupied() {
		return thermOccupied;
	}

	public boolean isLightOccupied() {
		return lightOccupied;
	}

	public void queryCalendar() {
		DateTime now = new DateTime(System.currentTimeMillis()-3600000);
		DateTime max = new DateTime(System.currentTimeMillis()+3600000*24);
		try {
			Events events = calendarService.events().list(calendarID).setTimeMin(now).setTimeMax(max)
					.setOrderBy("startTime").setSingleEvents(true).execute();
			currentEvents = events.getItems();
			if (!currentEvents.isEmpty()) {
				/*for (Event event : currentEvents) {
					DateTime start = event.getStart().getDateTime();
					if (start == null) {
						start = event.getStart().getDate();
					}
				}*/
				nextEventStart = ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentEvents.get(0).getStart().getDateTime().getValue()),ZoneId.systemDefault());
				nextEventTitle = currentEvents.get(0).getSummary();
				
				
			}else {
				nextEventStart = null;
				nextEventTitle = null;
			}

		} catch (IOException e) {
			// Communications error
			System.err.println("Error: CalendarQuery: There was a problem communicating with the service.");
			// e.printStackTrace();
		}
		// return result;
		lastRun = ZonedDateTime.now();
	}

	public void run() {
		while (true) {
			// System.out.println("Checking "+listenerTitle);
			queryCalendar();
			try {
				sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
	}

	public void setRefresh(boolean quick) {
		if (quick) {
			sleepTime = (long) 10000;// 10seconds
		} else {
			sleepTime = oneHour;
		}
	}
}
