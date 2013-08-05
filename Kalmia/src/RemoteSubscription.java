

import com.universaldevices.common.Constants;
import com.universaldevices.resources.errormessages.Errors;
import com.universaldevices.security.upnp.UPnPSecurity;
import com.universaldevices.soap.UDHTTPResponse;
import com.universaldevices.upnp.UDProxyDevice;



/**
 * This class is simple ISY Insteon Client which enables you to do everything (except trigger)
 * that can be done from the applet through command prompt.
 * @author UD Architect
 *
 */
public class RemoteSubscription  {
	protected static KalmiaISYClient myISY = null;

	public static boolean subscribe(String url)
	{
		if (myISY == null)
			return false;
		UDProxyDevice dev= myISY.getDevice();
		if (dev == null || !dev.isOnline)
			return false;
		StringBuffer body=new StringBuffer();
		body.append("<reportURL>");
		body.append(url);
		body.append("</reportURL><duration>");
		body.append(Constants.UD_SUBSCRIPTION_DURATION);
		body.append("</duration>");
		
		UDHTTPResponse res = dev.submitSOAPRequest("Subscribe",body,UPnPSecurity.SIGN_WITH_HMAC_KEY,false,false);
		if (!res.opStat || res.body == null)
		{
			Errors.showError(res.status,null,myISY.getDevice());
			return false;
		}
		System.out.println(res.body);
		return true;
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		myISY=new KalmiaISYClient();
		Errors.addErrorListener(new ISYErrorHandler());
		try{
			/**
			 * if UPnP use:
			 */
		//	myISY.start();
			//myISY.start("uuid:00:03:f4:02:af:74","https://udi.isy-mobile.com/desc");
			myISY.start("uuid:00:03:f4:03:97:64","http://192.168.0.198");
			while(myISY.getDevice()==null || !myISY.getDevice().isOnline)
				Thread.sleep(5000);
			
			System.out.println("Subscribing to http://192.168.0.135:8026/ORServer/events/uuid:00:03:f4:03:97:64");
			if (!subscribe("http://192.168.0.135:8026/ORServer/events/uuid:00:03:f4:03:97:64"))
			{
				System.out.println("Could not subscribe");
			}
			
			//myISY.start("uuid:00:03:f4:02:20:28","http://192.168.0.221/desc");
			while (true)
			{
				Thread.sleep(10000);
			
				/*myISY.changeNodeState("X10", "1", "A2");
				Thread.sleep(5000);
				myISY.changeNodeState("X10", "5", "B");
				Thread.sleep(5000);*/
				
			}
			/**
			 * else use:
			 */
			//myISY.start("uuid:00:03:f4:02:af:74","http://udi.isy-mobile.com");
			//myISY.start("uuid:00:03:f4:02:af:80","http://isy.mbdatasystems.com");
		}catch(Exception e){
			e.printStackTrace();
		}
		

	}

}
