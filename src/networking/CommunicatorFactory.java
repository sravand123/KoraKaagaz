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

	public static int getPort(){
		return 0;
	}

	/**
	* method @freeCommunicator() is used for getting rid of communicator
	*/
	public static void freeCommunicator(){
		communicatorInstance=null;
	}
}