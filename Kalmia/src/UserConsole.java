import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;


public class UserConsole extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private LinkedList<SwitchController> switchControllers;
	private LinkedList<CalendarListener> listeners;
	// Instance attributes used in this example
	private	SwitchMapViewer	thermoMap, thermoSPMap,lightMap,lightSPMap;
	private SwitchLibraryViewer switchLibView = new SwitchLibraryViewer();
	private CalenderLibraryViewer calendarLibView = new CalenderLibraryViewer();
	//private	JTable		table;
	//private	JScrollPane scrollPane;
	private JTabbedPane tabbedPane;
	private JMenuBar menuBar;
	private JMenu file, tools;
	private JMenuItem exit, settings,showXY,quickRefresh,switchingMode;
	private String homePath;
	private StatusBar statusBar;
	private boolean listenersInitialized = false,switchesInitialized = false;
	private WeatherWatcher weather;

	public UserConsole(String path){
		homePath = path;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1862,896));
		setResizable(false);
		exit=new JMenuItem("Exit");
		exit.setMnemonic('x');
		exit.addActionListener(this);
		settings = new JMenuItem("Settings");
		settings.setMnemonic('S');
		settings.addActionListener(this);
		showXY = new JMenuItem("Show XY");
		showXY.setMnemonic('Y');
		showXY.addActionListener(this);
		quickRefresh = new JMenuItem("Quick Refresh");
		quickRefresh.setMnemonic('R');
		quickRefresh.addActionListener(this);
		switchingMode = new JMenuItem("Lean Switching Mode");
		switchingMode.setMnemonic('L');
		switchingMode.addActionListener(this);
		file = new JMenu("File");
		file.setMnemonic('F');
		file.add(exit);
		tools = new JMenu("Tools");
		tools.setMnemonic('T');
		tools.add(settings);
		tools.add(showXY);
		tools.add(quickRefresh);
		tools.add(switchingMode);
		menuBar = new JMenuBar();
		menuBar.add(file);
		menuBar.add(tools);
		tabbedPane = new JTabbedPane();
		thermoMap = new SwitchMapViewer(homePath,SwitchMapViewer.CLIMATE_STATUS);
		thermoSPMap = new SwitchMapViewer(homePath,SwitchMapViewer.CLIMATE_SET_POINT);
		lightMap = new SwitchMapViewer(homePath,SwitchMapViewer.LIGHTS_STATUS);
		lightSPMap = new SwitchMapViewer(homePath,SwitchMapViewer.LIGHTS_SET_POINT);
		//thermoMap.addMouseMotionListener(new MouseMotionListener());
		statusBar = new StatusBar();
		
		thermoMap.addStatusBar(statusBar);
		thermoSPMap.addStatusBar(statusBar);
		lightMap.addStatusBar(statusBar);
		lightSPMap.addStatusBar(statusBar);
		
		tabbedPane.add(thermoMap,"Temp Status");
		tabbedPane.add(thermoSPMap,"Temp Set Point");
		tabbedPane.add(lightMap,"Lights Status");
		tabbedPane.add(lightSPMap,"Lights Set Point");	
		tabbedPane.add(switchLibView,"Switch Status");
		tabbedPane.add(calendarLibView,"Calendar Status");
		
		getContentPane().add(menuBar, BorderLayout.NORTH);
		getContentPane().add(tabbedPane,BorderLayout.CENTER);
		getContentPane().add(statusBar, BorderLayout.SOUTH);
		pack();
		//1845x805
		setVisible(true);
	}
	public void addSwitches(LinkedList<SwitchController> s){
		switchControllers=s;
		thermoMap.addSwitches(switchControllers);
		thermoSPMap.addSwitches(switchControllers);	
		lightMap.addSwitches(switchControllers);
		lightSPMap.addSwitches(switchControllers);
		switchLibView.addSwitches(switchControllers);
		switchesInitialized = true;
	}
	public void addCalendars(LinkedList<CalendarListener> c){
		listeners=c;
		calendarLibView.addListeners(listeners);
		listenersInitialized = true;
	}
	public void setStatus(String s){
		statusBar.setStatusText(s);
	}
	//method to be called after Controller Core is initialized
	public void coreInitialized(){
		thermoMap.coreInitialized();
		thermoMap.repaint();
		thermoSPMap.coreInitialized();
		thermoSPMap.repaint();
		lightMap.coreInitialized();
		lightMap.repaint();
		lightSPMap.coreInitialized();
		lightSPMap.repaint();
	}
	
	public void actionPerformed(ActionEvent e) {
		//System.out.println("action performed");
		if(e.getActionCommand().equals("Exit")) exit();
		else if(e.getActionCommand().equals("Settings")) settings();
		else if(e.getActionCommand().equals("Show XY")) showPoints();
		else if(e.getActionCommand().equals("Hide XY")) hidePoints();
		else if(e.getActionCommand().equals("Quick Refresh")) fastRefresh();
		else if(e.getActionCommand().equals("Normal Refresh")) normalRefresh();
		else if(e.getActionCommand().equals("Lean Switching Mode")) leanSwitching();
		else if(e.getActionCommand().equals("Normal Switching Mode")) normalSwitching();
		
	}
	private void exit(){
		//System.out.println("exiting");
		System.exit(NORMAL);
	}
	private void settings(){
		System.out.println("settings");
	}
	private void showPoints(){
		thermoMap.setShowXY(true);
		thermoSPMap.setShowXY(true);
		lightMap.setShowXY(true);
		lightSPMap.setShowXY(true);
		showXY.setText("Hide XY");
	}
	private void hidePoints(){
		thermoMap.setShowXY(false);
		thermoSPMap.setShowXY(false);
		lightMap.setShowXY(false);
		lightSPMap.setShowXY(false);
		showXY.setText("Show XY");
		statusBar.setStatusText2("");
	}
	private void fastRefresh(){
		quickRefresh.setText("Normal Refresh");
		setRefresh(true);
	}
	private void normalRefresh(){
		quickRefresh.setText("Quick Refresh");
		setRefresh(false);
	}
	private void leanSwitching(){
		switchingMode.setText("Normal Switching Mode");
		if(switchesInitialized)
			for(SwitchController x : switchControllers){
				x.setSwitchMode(false);
				x.interrupt();
			}
		else
			System.out.println("Error: Switches not Initialized yet!");
	}
	private void normalSwitching(){
		switchingMode.setText("Lean Switching Mode");
		if(switchesInitialized)
			for(SwitchController x : switchControllers){
				x.setSwitchMode(true);
				x.interrupt();
			}
		else
			System.out.println("Error: Switches not Initialized yet!");
	}
	
	private void setRefresh(boolean rate){
		if(listenersInitialized)
			for(CalendarListener x : listeners){
				x.setRefresh(rate);
				x.interrupt();
			}
		else
			System.out.println("Error: Listeners not Initialized yet!");
		if(switchesInitialized)
			for(SwitchController x : switchControllers){
				x.setRefresh(rate);
				x.interrupt();
			}
		else
			System.out.println("Error: Switches not Initialized yet!");
		thermoMap.setRefresh(rate);
		thermoSPMap.setRefresh(rate);
		lightMap.setRefresh(rate);
		lightSPMap.setRefresh(rate);
		
	}	
	public void addWeather(WeatherWatcher w){
		weather = w;
		statusBar.addWeather(weather);
	}
}
