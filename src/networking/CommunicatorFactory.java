package networking;

//import networking.LanCommunicator;
import networking.ICommunicator;
import networking.LanCommunicator;

/**
*
* @author Pulagam Prudhvi Vardhan Reddy
*
*/


public class CommunicatorFactory{

	private static ICommunicator communicatorInstance;

	private CommunicatorFactory(int port){
		communicatorInstance =new LanCommunicator(port);
	}
	
	public static ICommunicator getCommunicator(int port){
		if(communicatorInstance==null){
			new CommunicatorFactory(port);
		}
		return communicatorInstance;
	}

	private static int getPort(){
		return 0;
	}

	private static String getLocalIp(){
		return "";
	}
	/**
	* @getClientInfo() helps in obtaning the private address of client's pc and
	* port of client's pc where our application listening to.
	*/
	public static String getClientInfo(){
		return (getLocalIp()+getPort());
	}
	/**
	* method @freeCommunicator() is used for getting rid of communicator
	*/
	public static void freeCommunicator(){
		communicatorInstance=null;
	}
}