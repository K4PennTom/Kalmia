

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.udi.insteon.client.InsteonConstants;
import com.universaldevices.client.NoDeviceException;
import com.universaldevices.device.model.UDGroup;
import com.universaldevices.device.model.UDNode;
import com.universaldevices.resources.errormessages.Errors;



/**
 * This class is simple ISY Insteon Client which enables you to do everything (except trigger)
 * that can be done from the applet through command prompt.
 * @author UD Architect
 *
 */
public class MyISYInsteonClientApp  {
	
	private KalmiaISYClient myISY = null;
	
	public MyISYInsteonClientApp()
	{
		myISY=new KalmiaISYClient();
		Errors.addErrorListener(new ISYErrorHandler());
		new MyCommandHandler().start();
		
	}
	
	
	public synchronized KalmiaISYClient getISY()
	{
		return myISY;
	}
	
	
	

	/**
	 * This class handles user inputs
	 * @author UD Architect
	 */
    private class MyCommandHandler extends Thread{
		public void run(){
			try {
		        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		        String str = "";
		        while (str != null) {
		            System.out.print("(? for Help) >");
		            str = in.readLine();
		            processCommand(str);
		        }
		    } catch (IOException e) {
		    	System.err.println("UMMM .... can't seem to read the input");
		    }

		}
	}
    
    /**
     * This method shows the user the list of commands supported 
     * by this example application
     */
	protected static void processHelp(){
		System.out.println("The Insteon Device address is of the form X.X.X.X where X is Hexadecimal Uppercase");
		System.out.println("The Scene address is of the form ddd where ddd is a decimal number");
		System.out.println("StartLinking       - Starts a Linking Session");
		System.out.println("StopLinking        - Stops a Linking Session");
		System.out.println("SetLinkingMode M/S - Subsequent Linking Sessions are started with PLM as Master or Slave");
		System.out.println("NewScene newName - Creates a new Scene with the given name");
		System.out.println("Rename X.X.X.X/ddd to newName- Renames an Insteon Device or a Scene");
		System.out.println("Delete X.X.X.X/ddd - Permanently deletes an Insteon Device or a Scene");
		System.out.println("Move X.X.X.X to ddd as M/S (X.X.X.X Insteon Device address, ddd Scene address)");
		System.out.println("Remove X.X.X.X from dddd - Removes an Insteon Device from a Scene");
		System.out.println("SetSceneOnLevel for X.X.X.X in ddd to n - Sets the Scene OnLevel for X.X.X.X to n%");
		System.out.println("SetSceneRampRate for X.X.X.X in ddd to n - Sets the Scene RampRate for X.X.X.X to n%");
		System.out.println("SetSceneControllerOnLevel for X.X.X.X by X.X.X.X to n - Sets the Scene OnLevel by Controller X.X.X.X for X.X.X.X to n%");
		System.out.println("SetSceneControllerRampRate for X.X.X.X by X.X.X.X to n - Sets the Scene RampRate by Controller X.X.X.X for X.X.X.X to n%");		
		System.out.println("ListNodes - Lists all the nodes configured in ISY");
		System.out.println("ListScenes - Lists all the scenes configured in ISY");
		System.out.println("GetStatus X.X.X.X - Retrieves the current status for the given Insteon Device");
		System.out.println(InsteonConstants.DEVICE_ON +" X.X.X.X/ddd - turns on an Insteon device/Scene with the given address");
		System.out.println(InsteonConstants.DEVICE_OFF+" X.X.X.X/ddd - turns off an Insteon device/Scene with the given address");
		System.out.println(InsteonConstants.DEVICE_FAST_ON+" X.X.X.X/ddd - turns fast on an Insteon device/Scene with the given address");
		System.out.println(InsteonConstants.DEVICE_FAST_OFF+" X.X.X.X/ddd - turns fast off an Insteon device/Scene with the given address");
		System.out.println(InsteonConstants.LIGHT_DIM+" X.X.X.X/ddd - dims an Insteon device/Scene with the given address");
		System.out.println(InsteonConstants.LIGHT_BRIGHT+" X.X.X.X/ddd - brightens an Insteon device/Scene with the given address");
		System.out.println("Exit - Bye Bye!");
		
	}
	
	/**
	 * Dispatches the commands to right methods
	 * @param command - the command line
	 */
	protected void processCommand(String command){
		if (command == null || command.length()<1)
			return;
		StringTokenizer tk = new StringTokenizer(command," ");
		try{
			String cmd = tk.nextToken();
			if (cmd.startsWith("?"))
				processHelp();
			else if (cmd.startsWith(InsteonConstants.DEVICE_ON) ||
					 cmd.startsWith(InsteonConstants.DEVICE_OFF)||
					 cmd.startsWith(InsteonConstants.DEVICE_FAST_ON)||
					 cmd.startsWith(InsteonConstants.DEVICE_FAST_OFF)||
					 cmd.startsWith(InsteonConstants.LIGHT_DIM)||
					 cmd.startsWith(InsteonConstants.LIGHT_BRIGHT))
			{
				processInsteonCommand(cmd,tk);
			}else if (cmd.equals("StartLinking")){
				getISY().startLinking();
			}else if (cmd.equals("StopLinking")){
				getISY().stopLinking();
			}else if (cmd.startsWith("SetLinkingMode")){
				setLinkingMode(tk);
			}else if (cmd.startsWith("Rename")){
				processRename(tk);
			}else if (cmd.startsWith("Delete")){
				processDelete(tk);
			}else if (cmd.startsWith("Remove")){
				processRemoveFromScene(tk);
			}else if (cmd.startsWith("Move")){
				processMove(tk);
			}else if (cmd.startsWith("NewScene")){
				processNewScene(tk);
			}else if (cmd.startsWith("SetSceneOnLevel")){
				processSceneOnLevel(tk);
			}else if (cmd.startsWith("SetSceneRampRate")){
				processSceneRampRate(tk);
			}else if (cmd.startsWith("SetSceneControllerOnLevel")){
				processSceneControllerOnLevel(tk);
			}else if (cmd.startsWith("SetSceneControllerRampRate")){
				processSceneControllerRampRate(tk);
			}else if (cmd.startsWith("ListNodes")){
				processListNodes();
			}else if (cmd.startsWith("ListScenes")){
				processListScenes();
			}else if (cmd.startsWith("GetStatus")){
				processStatus(tk);
			}
			else if (cmd.startsWith("Exit")){
				getISY().stop();
				System.exit(0);
			}
			else{
				syntaxError();
			}
		}catch(Exception e){
			e.printStackTrace();
			syntaxError();
		}
	}

	/**
	 * Notifies the user of a syntax error
	 */
	protected static void syntaxError(){
		System.err.println("Syntax error. Try again.");
	}
	
	/**
	 * Returns a <code>UDGroup</code> or a <code>UDNode</code> based on the 
	 * given address
	 * @param address - the address of the node/scene to be retrieved
	 * @return the UDNode if found, null otherwise
	 */
	protected UDNode getNode(String address){
		if (address == null){
			System.err.println("Missing Device/Scene address");
			return null;
		}
		if (address.indexOf(".")>0){
			//this is an insteon device
			address=address.replace(".", " ");//normalize it to our format
			try{
				UDNode node = getISY().getNodes().get(address);
				if (node == null)
				{
					System.err.println("Address points to a non existing Insteon Device");
					return null;
				}
				return node;
			}catch(NoDeviceException e){
				System.out.println(e);
				return null;
			}
		}
	
		//this is an insteon scene
		try{
			UDGroup group = getISY().getGroups().get(address);
			if (group == null){
				System.err.println("Address points to a non-existing scene");
				return null;
			}
			return group;
		}catch(Exception e){
			System.err.println(e);
			return null;
		}
	}
	
	protected static char INSTEON_MASTER_MODE =0xF0;
	protected static char INSTEON_SLAVE_MODE  =0x0F;
	
	/**
	 * Returns the mode based on the input
	 * @param mode
	 * @return - the mode (INSTEON_MASTER_MODE, INSTEON_SLAVE_MODE)
	 */
	protected static char getMode(String mode){
		if (mode.equals("M"))
			return INSTEON_MASTER_MODE;
		if (mode.equals("S"))
			return INSTEON_SLAVE_MODE;
		
		System.err.println("Please specify M (for Master mode) or S (for Slave mode)");
		return 0;

	}
	
	/**
	 * Processes an Insteon command
	 * @param cmd - the command to be processed
	 * @param tk - the StringTokenizer
	 */
	protected void processInsteonCommand(String cmd, StringTokenizer tk){
		String address=tk.nextToken();
		UDNode node = getNode(address);
		if (node == null)
			return;
		if (node instanceof UDGroup)//it's a group
			getISY().changeGroupState(cmd, null, node.address);
		else
			getISY().changeNodeState(cmd, null, node.address);
	}
	
	/**
	 * Sets the linking mode as either master or slave
	 * @param tk - the StringTokenizer
	 */
	protected void setLinkingMode(StringTokenizer tk){
		String mode = tk.nextToken();
		char cmode=getMode(mode);
		if (cmode==0)
			return;
		if (cmode == INSTEON_MASTER_MODE)
			getISY().setMasterLinkingMode();
		else if (cmode == INSTEON_SLAVE_MODE)
			getISY().setSlaveLinkingMode();		
	}
	
	/**
	 * Renames either a node or a scene
	 * @param tk - the StringTokenizer
	 */
	protected void processRename(StringTokenizer tk){
		String tmp = tk.nextToken();
		UDNode node = getNode(tmp);
		if (node == null)
			return;
		tmp= tk.nextToken();
		if (tmp == null){
			syntaxError();
			return;
		}
		tmp = tk.nextToken(); // this must be the name
		if (tmp == null){
			syntaxError();
			return;
		}
		while (tk.hasMoreTokens())
			tmp+=" "+tk.nextToken();
		
		if (node instanceof UDGroup)
			getISY().renameGroup(node.address, tmp);
		else
			getISY().renameNode(node.address, tmp);
	}
	
	/**
	 * Removes a node or a scene from ISY
	 * @param tk - the StringTokenizer
	 */
	protected void processDelete(StringTokenizer tk){
		String tmp = tk.nextToken();
		UDNode node = getNode(tmp);
		if (node == null)
			return;
		if (node instanceof UDGroup)
			getISY().removeGroup(node.address);
		else
			getISY().removeNode(node.address);
	}
	
	/**
	 * Removes a node (Insteon Device) from scene
	 * @param tk - the StringTokenzier
	 */
	protected void processRemoveFromScene(StringTokenizer tk){
		String n_address = tk.nextToken();
		UDNode node = getNode(n_address);
		if (node == null)
			return;
		if (tk.nextToken() == null){
			syntaxError();
			return;
		}
		String g_address = tk.nextToken();
		UDGroup scene = (UDGroup)getNode(g_address);
		if (scene == null)
			return;
		
		getISY().removeFromGroup(node.address, scene.address);
	}
	
	/**
	 * Creates a new scene
	 * @param tk - the StringTokenzier
	 */
	protected void processNewScene(StringTokenizer tk){
		String name= tk.nextToken();
		if (name == null){
			syntaxError();
			return;
		}
		while (tk.hasMoreTokens())
			name+=" "+tk.nextToken();
		getISY().addNewScene(name);
	}
	
	/**
	 * Moves a node (Insteon Device) to a scene 
	 * @param tk - the StringTokenizer
	 */
	protected void processMove(StringTokenizer tk){
		String n_address = tk.nextToken();
		UDNode node = getNode(n_address);
		if (node == null)
			return;
		if (tk.nextToken()==null){
			syntaxError();
			return;
		}
		String g_address = tk.nextToken();
		UDGroup scene = (UDGroup)getNode(g_address);
		String mode=null;
		if (scene == null)
			return;
		try{
			tk.nextToken();
			mode = tk.nextToken();
		}catch(NoSuchElementException ne){
			syntaxError();
			return;
		}
		
		char cmode=getMode(mode);
		if (cmode == 0)
			return;
		if (cmode == INSTEON_MASTER_MODE){
			getISY().moveNodeToSceneAsMaster(node, scene);
		}else if (cmode == INSTEON_SLAVE_MODE){
			getISY().moveNodeToSceneAsSlave(node, scene);
		}
	}
	/**
	 * Changes the scene on level for an Insteon device within a scene
	 * @param tk - the StringTokenzier
	 */
	protected void processSceneOnLevel(StringTokenizer tk){
		tk.nextToken(); //for
		String tmp = tk.nextToken(); //device address
		UDNode node = getNode(tmp);
		if (node == null)
			return;
		tk.nextToken(); //in
		tmp = tk.nextToken();
		UDGroup scene = (UDGroup)getNode(tmp);
		if (scene == null)
			return;
		tk.nextToken(); //to
		tmp = tk.nextToken(); //on level
		getISY().setDeviceOnLevelForAScene(scene.address, node.address, Integer.parseInt(tmp));
		
	}
	/**
	 * Changes the ramp rate for an Insteon Device within a scene
	 * @param tk - the StringTokenizer
	 */
	protected void processSceneRampRate(StringTokenizer tk){
		tk.nextToken(); //for
		String tmp = tk.nextToken(); //device address
		UDNode node = getNode(tmp);
		if (node == null)
			return;
		tk.nextToken(); //in
		tmp = tk.nextToken();
		UDGroup scene = (UDGroup)getNode(tmp);
		if (scene == null)
			return;
		tk.nextToken(); //to
		tmp = tk.nextToken(); //on level
		getISY().setDeviceRampRateForAScene(scene.address, node.address, Integer.parseInt(tmp));
		
	}	
	
	/**
	 * Changes the on level for an Insteon Device linked to an Insteon Controller within
	 * a scene
	 * @param tk - the StringTokenizer
	 */
	protected void processSceneControllerOnLevel(StringTokenizer tk){
		tk.nextToken(); //for
		String tmp = tk.nextToken(); //device address
		UDNode node = getNode(tmp);
		if (node == null)
			return;
		tk.nextToken(); //by
		tmp = tk.nextToken();
		UDNode controller = getNode(tmp);
		if (controller == null)
			return;
		tk.nextToken(); //to
		tmp = tk.nextToken(); //on level
		getISY().setDeviceOnLevelForASceneController(controller.address, node.address, Integer.parseInt(tmp));
	}
	/**
	 * Changes the ramp rate for an Insteon Device linked to an Insteon Controller within
	 * a scene
	 * @param tk - the StringTokenizer
	 */	
	protected void processSceneControllerRampRate(StringTokenizer tk){
		tk.nextToken(); //for
		String tmp = tk.nextToken(); //device address
		UDNode node = getNode(tmp);
		if (node == null)
			return;
		tk.nextToken(); //by
		tmp = tk.nextToken();
		UDNode controller = getNode(tmp);
		if (controller == null)
			return;
		tk.nextToken(); //to
		tmp = tk.nextToken(); //ramp rate
		getISY().setDeviceRampRateForASceneController(controller.address, node.address, Integer.parseInt(tmp));
	}	
	/**
	 * Lists out all the nodes as configured within ISY
	 */
	protected void processListNodes(){
		System.out.println("------------- NODES ------------");
		try{
			for (Enumeration<UDNode> e = getISY().getNodes().elements(); e.hasMoreElements(); ){
				UDNode node= e.nextElement();
				System.out.println(node.address +":"+node.name);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * Lists out all the scenes as configured within ISY
	 *
	 */
	protected void processListScenes(){
		System.out.println("------------- SCENES ------------");
		try{
			for (Enumeration<UDGroup> e = getISY().getGroups().elements(); e.hasMoreElements(); ){
				UDGroup scene= e.nextElement();
				if (scene.isRootNode())//don't show the root (ISY) node
					continue;
				System.out.println(scene.address +":"+scene.name);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the current value of an Insteon Device (its state)
	 * @param tk - the StringTokenzier
	 */
	protected void processStatus(StringTokenizer tk){
		String tmp = tk.nextToken(); //device address
		UDNode node = getNode(tmp);
		if (node == null)
			return;
		tmp = (String)getISY().getCurrValue(node, InsteonConstants.DEVICE_STATUS);
		System.out.println("The current status for "+node.address+"/"+node.name+ " is " +tmp);
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		final MyISYInsteonClientApp app = new MyISYInsteonClientApp();
		try{
			if (args.length == 0)
				app.getISY().start();
			else if (args.length == 2)
				app.getISY().start(args[0], args[1]); 
			else
			{
				System.err.println("usage: MyISYInsteonClientApp ([uuid][url])|(no args for UPnP)");
				System.exit(1);
			}
			
			while (true)
			{
				app.getISY().changeNodeState("DFON", null, "B 31 66 1");
				Thread.sleep(100);
				app.getISY().queryNode("B 31 66 1");
				Thread.sleep(100);
				app.getISY().changeNodeState("DFOF", null, "B 31 66 1");
				Thread.sleep(100);
			
				/*getISY().changeNodeState("X10", "1", "A2");
				Thread.sleep(5000);
				getISY().changeNodeState("X10", "5", "B");
				Thread.sleep(5000);*/
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		

	}

}
