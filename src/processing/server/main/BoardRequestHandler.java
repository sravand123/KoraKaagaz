package processing.server.main;

import networking.INotificationHandler;
import processing.ClientBoardState;
import processing.PersistanceSupport;
import processing.utility.*;
import java.io.*;

import infrastructure.validation.logger.LogLevel;
import infrastructure.validation.logger.ModuleID;
import networking.CommunicatorFactory;

/**
 * This handler implements INotificationHandler and handles
 * existing board requests whenever any client asks for a board
 * which was existed before.
 * 
 * @author Himanshu Jain
 * @reviewer Ahmed Zaheer Dadarkar
 *
 */

public class BoardRequestHandler implements INotificationHandler{

	/**
	 * startBoardServer function will start a separate process as the Board Server
	 * with the given board ID and this server will listen on the given port.
	 * 
	 * It will also pass the persistence file existed earlier for this board.
	 * 
	 * @param port port number on which Board Server should listen
	 * @param boardId Board ID of the board going to start
	 * @param persistence persistent data
	 */
	public static void startBoardServer(Port port, BoardId boardId, String persistence) {
		
		ClientBoardState.logger.log(
				ModuleID.PROCESSING, 
				LogLevel.INFO, 
				"Received a request to start a board server"
		);
		
		/**
		 * creating an object of ProcessBuilder which is used to run the operating 
		 * system commands. It will be used to run the Board Server's jar file of the 
		 * project.
		 */
		ProcessBuilder processBuilder = new ProcessBuilder();
		
		/**
		 * command to run, BoardServer.jar is the file name and three command line arguments are
		 * given while starting this process.
		 * First is the port number to start communication on that Board Server
		 * Second is the BoardID of that board server
		 * Third is the persistent data
		 */
		processBuilder.command(
				"java", 
				"-jar", 
				"BoardServer.jar", 
				port.toString(),
				boardId.toString(),
				persistence
		);
		
		/**
		 * start the process by calling start function of processBuilder and it throws
		 * the IOException so we also need to handle the exception.
		 */
		try {
			
			processBuilder.start();
			
		} catch(IOException e) {
			
			ClientBoardState.logger.log(
					ModuleID.PROCESSING, 
					LogLevel.ERROR, 
					"IO EXception occured while starting a new board server"
			);
			
		}
	}
	
	/**
	 * onMessageReceived is the function defined in INotficationHandler which the 
	 * networking module will call if any message is received regarding existing
	 * board request.
	 */
	public void onMessageReceived(String message) {
		
		ClientBoardState.logger.log(
				ModuleID.PROCESSING, 
				LogLevel.INFO, 
				"New Board Request received on the Main Server"
		);
		
		/**
		 * First part of the message contains the boardID, as it is requesting for an
		 * existing board they will need to pass the board ID of that previously existing
		 * board.
		 * Second part of the message is the client's full address
		 */
		String[] arguments = message.split(":", 2);
		BoardId boardId = new BoardId(arguments[0]);
		
		String clientAddress = arguments[1];
		
		/**
		 * boardServerPort will store the port number of the Board's server requested,
		 * if it's already running then we will find on which port it is running, 
		 * else will ask for a free new port from the networking module.
		 */
		Port boardServerPort;
		
		// if the requested board is already running
		if(ServerState.boardToPort.containsKey(boardId)) {
			boardServerPort = ServerState.boardToPort.get(boardId);
		} else {
			// ask for a new free port from the networking module
			boardServerPort = new Port(
					CommunicatorFactory.getClientInfo().getPort()
			);
			
			/**
			 * If it is an existing board, then it may have the persistent data stored
			 * so we need to load them and pass as the argument while starting the Board
			 * server
			 */
			String persistence = null;
			
			try {
				persistence = PersistanceSupport.loadStateString(boardId);
			} catch (ClassNotFoundException e) {
				
				ClientBoardState.logger.log(
						ModuleID.PROCESSING, 
						LogLevel.ERROR, 
						"IOException occured while loading the persistence for board"
				);
				
			} catch(UnsupportedEncodingException e) {
				
				ClientBoardState.logger.log(
						ModuleID.PROCESSING, 
						LogLevel.ERROR, 
						"UnsupportedEncodingException occured while loading the persistence state"
				);
				
			} catch (IOException e) {
				
				ClientBoardState.logger.log(
						ModuleID.PROCESSING, 
						LogLevel.ERROR, 
						"IO Exception occured while loading the persistence state"
				);
				
			}
			
			// starting the Board Server
			startBoardServer(boardServerPort, boardId, persistence);
			
			/**
			 * We need to store the port number on which this board server is running 
			 * for future reference, thus storing it in the map boardToPort
			 */
			ServerState.boardToPort.put(boardId, boardServerPort);
		}
		
		/**
		 * We need to send back the Board Server port to client who requested tbis
		 * board's server so they can communicate further.
		 */
		ServerState.send(
				clientAddress, 
				boardServerPort.toString(), 
				"ProcessingServerPort"
		);
		
		ClientBoardState.logger.log(
				ModuleID.PROCESSING, 
				LogLevel.SUCCESS, 
				"Successfully sent the port number of the server to the client"
		);
	}
	
}
