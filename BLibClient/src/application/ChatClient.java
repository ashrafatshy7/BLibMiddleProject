package application;

import ocsf.client.*;

import common.ChatIF;
import enteties.User;
import gui.bounderies.ClientFrameController;
import gui.bounderies.HomeFrameController;
import gui.bounderies.LoginFrameController;
import gui.bounderies.ReturnFrameController;
import message.Message;
import message.MessageType;

import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient {
	// Instance variables **********************************************

	private ClientFrameController clientFrameController;
	private HomeFrameController homeFrameController;
	private LoginFrameController loginFrameController;
	private ReturnFrameController returnFrameController;

	/**
	 * The interface type variable. It allows the implementation of the display
	 * method in the client.
	 */
	ChatIF clientUI;
	public static boolean awaitResponse = false;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the chat client.
	 *
	 * @param host     The server to connect to.
	 * @param port     The port number to connect on.
	 * @param clientUI The interface type variable.
	 */

	public ChatClient(String host, int port, ChatIF clientUI) throws IOException {
		super(host, port); // Call the superclass constructor
		this.clientUI = clientUI;
	}

	// Instance methods ************************************************

	public void setClientFrameController(ClientFrameController clientFrameController) {
		this.clientFrameController = clientFrameController;
	}
	
	public void setHomeFrameController(HomeFrameController homeFrameController) {
        this.homeFrameController = homeFrameController;
    }
	
	public void setLoginFrameController(LoginFrameController loginFrameController) {
        this.loginFrameController = loginFrameController;
    }
	
	public void setReturnFrameController(ReturnFrameController returnFrameController) {
		
		this.returnFrameController = returnFrameController;
	}

	/**
	 * This method handles all data that comes in from the server.
	 *
	 * @param msg The message from the server.
	 */
	@SuppressWarnings("unchecked")
	public void handleMessageFromServer(Object msg) {
		awaitResponse = false;
		
		
		
		
		
		Message message = (Message) msg;
		MessageType messageType = message.getMessageType();

		switch (messageType) {
		case login:
			try {
				User user = (User) message.getMessageData();
				System.out.println("user is: "+user);
				if (loginFrameController != null) {
					loginFrameController.setUser(user);
                } else {
                    System.err.println("HomeFrameController is not set in ChatClient.");
                }
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			break;
			
		case Return:
			try {
				boolean isReturnSuccessful = (boolean) message.getMessageData();
				System.out.println("Book return status: " + (isReturnSuccessful ? "Successful" : "Failed"));
				
				 if (returnFrameController != null) {
					 returnFrameController.showMessage(isReturnSuccessful);
		            } else {
		                System.err.println("ReturnFrameController is not set in ChatClient.");
		            }
		        } catch (Exception e) {
		            System.err.println("Error processing return case: " + e.getMessage());
		        }
		        break;
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		HashMap<String, Object> response = (HashMap<String, Object>) msg;
//
//		String operation = (String) response.get("operation");
//
//		Object data = response.get("data");
//
//		if (operation.equals("getAllsubscribers")) {
//
//			ArrayList<Map<String, Object>> rawRows = (ArrayList<Map<String, Object>>) data;
//			// Convert each row (Map<String,Object>) to a Subscriber object
//			ArrayList<Subscriber> subscribers = new ArrayList<>();
//			for (Map<String, Object> row : rawRows) {
//
//				Subscriber sub = new Subscriber((String) row.get("subscriber_id"), (String) row.get("subscriber_name"),
//						(String) row.get("subscriber_phone_number"), (String) row.get("subscriber_email"),
//						(int) row.get("detailed_subscription_history"));
//				subscribers.add(sub);
//			}
//
//			clientFrameController.setSubscribers(subscribers);
//
//		} else if (operation.equals("getAllbooks")) {
//			ArrayList<Map<String, Object>> rawRows = (ArrayList<Map<String, Object>>) data;
//			ArrayList<Book> books = new ArrayList<>();
//			for (Map<String, Object> row : rawRows) {
//				
//				Book book = new Book((String) row.get("barcode"), (String) row.get("title"), (String) row.get("author"),
//						(String) row.get("category"), (String) row.get("description"), (String) row.get("shelf"),
//						(int) row.get("availableCopies"), (byte[]) row.get("image"));
//				books.add(book);
//			}
//			homeFrameController.setBooks(books);
//			
//			
//		}

	

	/**
	 * This method handles all data coming from the UI
	 *
	 * @param message The message from the UI.
	 */

	public void handleMessageFromClientUI(Object message) {
		try {
			openConnection();// in order to send more than one message
			awaitResponse = true;
			sendToServer(message);
			// wait for response
			while (awaitResponse) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			clientUI.display("Could not send message to server: Terminating client." + e);
			quit();
		}
	}

	/**
	 * This method terminates the client.
	 */
	public void quit() {
		try {
			ClientUI.chat.accept("QUIT");
			closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
//End of ChatClient class
