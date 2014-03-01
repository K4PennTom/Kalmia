package Kalmia.Server;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import Kalmia.Server.CalendarListener;
import Kalmia.Server.SwitchController;
import Kalmia.Server.WeatherWatcher;


public class ServerConsole extends Stage implements ActionListener{

	private LinkedList<SwitchController> switchControllers;
	private LinkedList<CalendarListener> listeners;
	// Instance attributes used in this example
	//private	JTable		table;
	//private	JScrollPane scrollPane;
	private TabPane tabbedPane;
	private MenuBar menuBar;
	private Menu file, tools;
	private MenuItem exit, settings,showXY,quickRefresh,switchingMode;
	private MapTab mapView;
	private String homePath;
	private StatusBar statusBar;
	private boolean listenersInitialized = false,switchesInitialized = false;
	private WeatherWatcher weather;

	public ServerConsole(Stage primaryStage, String path){
		
		primaryStage.setTitle("Kalmia Building Automation");        
        VBox root = new VBox();
        Scene scene = new Scene(root, 1862, 896);
        scene.setFill(Color.OLDLACE);
        
		homePath = path;
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setPreferredSize(new Dimension(1862,896));
		setResizable(false);
		
		mapView = new MapTab(path);
		exit=new Menu("Exit");
		//exit.setMnemonic('x');
		//exit.addActionListener(this);
		settings = new MenuItem("Settings");
		//settings.setMnemonic('S');
		//settings.addActionListener(this);
		showXY = new MenuItem("Show XY");
		//showXY.setMnemonic('Y');
		//showXY.addActionListener(this);
		quickRefresh = new MenuItem("Quick Refresh");
		//quickRefresh.setMnemonic('R');
		//quickRefresh.addActionListener(this);
		switchingMode = new MenuItem("Lean Switching Mode");
		//switchingMode.setMnemonic('L');
		//switchingMode.addActionListener(this);
		file = new Menu("File");
		//file.setMnemonic('F');
		file.getItems().add(exit);
		tools = new Menu("Tools");
		//tools.setMnemonic('T');
		tools.getItems().addAll(settings,showXY,quickRefresh,switchingMode);
		menuBar = new MenuBar();
		menuBar.getMenus().addAll(file,tools);
		tabbedPane = new TabPane();
		tabbedPane.getTabs().add(mapView);
		statusBar = new StatusBar();

		root.getChildren().addAll(menuBar,tabbedPane);
		//((VBox) scene.getRoot()).getChildren().addAll(menuBar,tabbedPane);
		primaryStage.setScene(scene);
		primaryStage.show();
		//1845x805
	}
	public void addSwitches(LinkedList<SwitchController> s){
		switchControllers=s;

		switchesInitialized = true;
	}
	public void addCalendars(LinkedList<CalendarListener> c){
		listeners=c;
		
		listenersInitialized = true;
	}
	public void setStatus(String s){
		statusBar.setStatusText(s);
	}
	//method to be called after Controller Core is initialized
	public void coreInitialized(){

	}
	
	public void actionPerformed(ActionEvent e) {
		//System.out.println("action performed");
		//if(e.getActionCommand().equals("Exit")) exit();
		//else if(e.getActionCommand().equals("Settings")) settings();
		//else if(e.getActionCommand().equals("Show XY")) showPoints();
		//else if(e.getActionCommand().equals("Hide XY")) hidePoints();
		//else if(e.getActionCommand().equals("Quick Refresh")) fastRefresh();
		//else if(e.getActionCommand().equals("Normal Refresh")) normalRefresh();
		//else if(e.getActionCommand().equals("Lean Switching Mode")) leanSwitching();
		//else if(e.getActionCommand().equals("Normal Switching Mode")) normalSwitching();
		
	}
	private void exit(){
		//System.out.println("exiting");
		//System.exit(NORMAL);
	}
	private void settings(){
		System.out.println("settings");
	}
	private void showPoints(){
		
		showXY.setText("Hide XY");
	}
	private void hidePoints(){
		
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

		
	}	
	public void addWeather(WeatherWatcher w){
		weather = w;
		statusBar.addWeather(weather);
	}
	@Override
	public void actionPerformed(java.awt.event.ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
