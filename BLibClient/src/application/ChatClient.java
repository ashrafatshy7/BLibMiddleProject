package application;

import ocsf.client.*;

import common.ChatIF;
import gui.bounderies.ClientFrameController;
import gui.bounderies.HomeFrameController;
import gui.bounderies.SeeAllFrameController;
import javafx.scene.image.Image;
import message.Message;
import message.MessageType;

import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import enteties.Book;
import enteties.Subscriber;

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
	private SeeAllFrameController seeAllFrameController;

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

	public void setSeeAllFrameController(SeeAllFrameController seeAllFrameController) {
		this.seeAllFrameController = seeAllFrameController;
	}

	public void setHomeFrameController(HomeFrameController homeFrameController) {
		this.homeFrameController = homeFrameController;
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
			try {
				ArrayList<Book> books = (ArrayList<Book>) message.getMessageData();
				if (homeFrameController != null) {
                    homeFrameController.setBooks(books);
                } else {
                    System.err.println("HomeFrameController is not set in ChatClient.");
                }
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			break;
		case getAllBooks:
			try {
				ArrayList<Book> books = (ArrayList<Book>) message.getMessageData();
				if (seeAllFrameController != null) {
					seeAllFrameController.setBooks(books);
                } else {
                    System.err.println("HomeFrameController is not set in ChatClient.");
                }
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * This method handles all data coming from the UI
	 *
	 * @param message The message from the UI.
	 */

	public void handleMessageFromClientUI(Object obj) {
		try {
			openConnection();// in order to send more than one message
			awaitResponse = true;
			sendToServer(obj);
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
			Message sendToServer = new Message(MessageType.disconnectFromServer);
			ClientUI.chat.accept(sendToServer);
			closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
//End of ChatClient class
