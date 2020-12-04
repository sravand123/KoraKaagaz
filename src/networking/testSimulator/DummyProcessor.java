package networking.testSimulator;


import java.util.Random;
import networking.ICommunicator;
import networking.utility.ClientInfo;
/**
 * This class represents dummy Processor
 * @author sravan
 */
public class DummyProcessor extends Thread{
	ClientInfo srcClient;
	ClientInfo destClient;
	ICommunicator communicator;
	Message input = null;
	Message output  = null;
	int numMessages;
	int msgLength;
	
	/**
	 * @param msgLength, Length of string to be generated
	 * @return String, randomly generated string of length of msgLength
	 */
	public static String randomString(int msgLength) {
		int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = msgLength;
	    Random random = new Random();
	    StringBuilder buffer = new StringBuilder(targetStringLength);
	    for (int i = 0; i < targetStringLength; i++) {
	        int randomLimitedInt = leftLimit + (int) 
	          (random.nextFloat() * (rightLimit - leftLimit + 1));
	        buffer.append((char) randomLimitedInt);
	    }
	    String generatedString = buffer.toString();
	    return(generatedString);
	}

	public DummyProcessor(
			ICommunicator communicator , 
			ClientInfo src,
			ClientInfo dest,
			Message input,
			Message output,
			int numMessages,
			int msgLength,
			Stopper stopper
		) {
		this.srcClient = src;
		this.destClient = dest;
		this.communicator = communicator;
		this.input = input;
		this.output = output;
		this.numMessages = numMessages;
		this.msgLength = msgLength;
		DummyProcessingHandler handler = new DummyProcessingHandler(this.output,this.numMessages,stopper);
		communicator.subscribeForNotifications("processing", handler);
	}
	
	/**
	 * Driver code of this thread
	 */
	@Override
	public void run() {
		
		for (int i = 0; i < this.numMessages; i++) {
			String message = randomString(this.msgLength);
			communicator.send(destClient.getIp()+":"+destClient.getPort(), message, "processing");
			input.processingMessage.add(message);
		}	
	}

	
	
}
