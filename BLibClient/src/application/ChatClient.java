package application;

import ocsf.client.*;
import gui.bounderies.*;

import common.ChatIF;
import gui.bounderies.ClientFrameController;
import javafx.application.Platform;
import message.Message;
import message.MessageType;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import enteties.Loan;
import enteties.Subscriber;

import gui.bounderies.*;

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
	private SubscriberCardDetailsController subscriberCardDetailsController;
	private ExtendPopupController extendPopupController;

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

	// added
	public void setSubscriberCardDetailsController(SubscriberCardDetailsController subscriberCardDetailsController) {
		this.subscriberCardDetailsController = subscriberCardDetailsController;
	}

	public void ExtendPopupController(ExtendPopupController extendPopupController) {
		this.extendPopupController = extendPopupController;
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
		case getTop5LoanedBooks:

			break;
		case getAllBooks:
			break;

		case cardNumber:
			HashMap<String, Object> response = (HashMap<String, Object>) ((Message) msg).getMessageData();

			// Pass the data to the controller
			if (subscriberCardDetailsController != null) {
				subscriberCardDetailsController.cardExist(response);
			} else {
				System.out.println("subscriberCardDetailsController is null.");
			}
			break;
		case updateEmailAndPhone:
			// Get the boolean value indicating if the update was successful
			boolean isUpdateSuccessful = (boolean) message.getMessageData();
			// Call the method btnUpdateDetailsClickedCheck with the result
			if (subscriberCardDetailsController != null) {
				subscriberCardDetailsController.btnUpdateDetailsClickedCheck(isUpdateSuccessful);
			} else {
				System.out.println("subscriberCardDetailsController is null.");
			}
			break;

		case updateReturnDate:
			boolean isDateUpdated = (boolean) message.getMessageData();
			// Pass the data to the controller
			if (subscriberCardDetailsController != null) {
				subscriberCardDetailsController.btnUpdateReturnDateCheck(isDateUpdated);
			} else {
				System.out.println("subscriberCardDetailsController is null.");
			}
			break;

		case bookExtentionTable:
			// ArrayList<Loan> booksCanExtend = (ArrayList<Loan>) message.getMessageData();
			Map<String, String> booksCanExtend = (Map<String, String>) message.getMessageData();
			if (extendPopupController != null) {
				extendPopupController.showExtentionBooks(booksCanExtend);
			} else {
				System.out.println("extendPopupController is null.");
			}
			break;
		case bookExtensionSucceeded:
			boolean extentionSuccess = (boolean) message.getMessageData();
			if (extendPopupController != null) {
				extendPopupController.bookExtensionSucceess(extentionSuccess);
			} else {
				System.out.println("extendPopupController is null.");
			}
			break;

		}

	}

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
