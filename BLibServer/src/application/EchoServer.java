package application;

import java.io.*;
import java.net.InetAddress;
import message.Message;
import message.MessageType;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enteties.Book;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract superclass in order
 * to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */

public class EchoServer extends AbstractServer {
	// Class variables *************************************************

	private List<Client> clients;

	/**
	 * The default port to listen on.
	 */
	// final public static int DEFAULT_PORT = 5555;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the echo server.
	 *
	 * @param port The port number to connect on.
	 * 
	 */

	public EchoServer(int port) {
		super(port);
		clients = Collections.synchronizedList(new ArrayList<>());
	}

	// Instance methods ************************************************

	public List<Client> getClients() {
		return clients;
	}

	@Override
	protected void clientConnected(ConnectionToClient client) {
		super.clientConnected(client);
		try {
			InetAddress inetAddress = client.getInetAddress();
			String ipAddress = inetAddress.getHostAddress();
			String hostName = inetAddress.getHostName();
			Client newClient = new Client(ipAddress, hostName, true);
			clients.add(newClient);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void clientDisconnected(ConnectionToClient client) {

		super.clientDisconnected(client);
		try {
			InetAddress inetAddress = client.getInetAddress();
			if (inetAddress != null) {
				String ipAddress = inetAddress.getHostAddress();
				String hostName = inetAddress.getHostName();
				clients.removeIf(c -> c.getIpAddress().equals(ipAddress) && c.getHostName().equals(hostName));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg    The message received from the client.
	 * @param client The connection from which the message originated.
	 * @param
	 */
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		try {
			Message message = (Message) msg;
			MessageType messageType = message.getMessageType();
			Message messageFromServer = null;

			switch (messageType) {
			case disconnectFromServer:
				clientDisconnected(client);
				client.sendToClient(new Message(MessageType.disconnectFromServer, "You have been disconnected."));
				return;
			case cardNumber:
				Map<String, Object> cardDetailsIfExists = mysqlConnection
						.getCardDetailsIfExists(message.getMessageData().toString());
				messageFromServer = new Message(MessageType.cardNumber, cardDetailsIfExists);

				client.sendToClient(messageFromServer);
				break;

			case updateEmailAndPhone:
				// Extract the required data
				ArrayList<String> allData = new ArrayList<>(
						Arrays.asList(((String) message.getMessageData()).split(" ")));
				String email = allData.get(0);
				String phoneNumber = allData.get(1);
				String cardNum = allData.get(2); // Card number from the client data

				// Update the subscriber's email and phone number in the database
				boolean isUpdated = mysqlConnection.updateSubscriberEmailAndPhoneNumber(email, phoneNumber, cardNum);

				// Populate the response with operation result

				Message isUpdatedMessage = new Message(MessageType.updateEmailAndPhone, isUpdated);

				// Send the response back to the client
				try {
					client.sendToClient(isUpdatedMessage);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case getAllBooks:
				break;
			case getTop5LoanedBooks:
				break;
			default:
				break;

			}
		} catch (IOException e) {
			System.err.println("IOException while handling message: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Unexpected exception while handling message: " + e.getMessage());
			e.printStackTrace();
		}

//		String message = (String) msg;
//
//		if ("QUIT".equalsIgnoreCase(message.trim())) {
//			clientDisconnected(client);
//			try {
//				client.sendToClient("");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			return;
//		}
//
//		ArrayList<String> allData = new ArrayList<>(Arrays.asList(message.split(" ")));
//
//
//		else if (allData.get(0).equals("cardDetails")) {
//			String cardNum = allData.get(1);
//
//			// Check if the card exists in the database and retrieve the details if it does
//			Map<String, Object> cardDetailsIfExists = mysqlConnection.getCardDetailsIfExists(cardNum);
//
//			// Prepare a response to send back to the client
//			HashMap<String, Object> response = new HashMap<>();
//
//			// Check if the card exists based on the "exists" key in the map
//			boolean exists = (boolean) cardDetailsIfExists.get("exists");
//
//			// Add the "exists" value at the beginning of the response
//			response.put("operation", "cardExists");
//			response.put("exists", exists);
//
//			if (exists) {
//				response.put("cardDetails", cardDetailsIfExists);
//			} else {
//				response.put("cardDetails", null); // No details to return if the card doesn't exist
//			}
//
//			System.out.println("EchoServer: Response = " + response.toString());
//
//			// Send the response back to the client
//			try {
//				client.sendToClient(response);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//		else if ("emailAndPhone".equals(allData.get(0))) {
//			// Extract the required data
//			String email = allData.get(1);
//			String phoneNumber = allData.get(2);
//			String cardNum = allData.get(3); // Card number from the client data
//
//			// Prepare a response to send back to the client
//			Map<String, Object> response = new HashMap<>();
//
//			// Update the subscriber's email and phone number in the database
//			boolean isUpdated = mysqlConnection.updateSubscriberEmailAndPhoneNumber(email, phoneNumber, cardNum);
//
//			// Populate the response with operation result
//			response.put("operation", "updateEmailAndPhone");
//			response.put("success", isUpdated);
//
//			// Send the response back to the client
//			try {
//				client.sendToClient(response);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}

//		else if ("updateReturnDate".equals(allData.get(0))) {
//			System.out.println("echo server updateReturnDate in");
//			String newDate = allData.get(1);
//			String cardNum = allData.get(2);
//
//			// Prepare a response to send back to the client
//			Map<String, Object> response = new HashMap<>();
//
//			// Update the subscriber's email and phone number in the database
//			boolean isUpdated = mysqlConnection.updateReturnDate(newDate, cardNum);
//
//			// Populate the response with operation result
//			response.put("operation", "updateReturnDate");
//			response.put("success", isUpdated);
//
//			// Send the response back to the client
//			try {
//				client.sendToClient(response);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		}

//		else if ("updateReturnDates".equals(allData.get(0))) {
//			System.out.println("echo server updateReturnDates in");
//
//			// Retrieve and cast the updates map
//			Object updatesObject = allData.get(1);
//
//			// Validate and cast the object
//			if (updatesObject instanceof Map) {
//				@SuppressWarnings("unchecked")
//				Map<String, Map<String, String>> updates = (Map<String, Map<String, String>>) updatesObject;
//
//				// Prepare a response to send back to the client
//				Map<String, Object> response = new HashMap<>();
//
//				boolean allUpdatesSuccessful = true;
//
//				// Iterate through the map to update return dates in the database
//				for (Map.Entry<String, Map<String, String>> entry : updates.entrySet()) {
//					String cardNum = entry.getKey();
//					Map<String, String> borrowAndReturnDates = entry.getValue();
//
//					for (Map.Entry<String, String> dateEntry : borrowAndReturnDates.entrySet()) {
//						String borrowDate = dateEntry.getKey();
//						String newReturnDate = dateEntry.getValue();
//
//						// Update each record in the database
//						boolean isUpdated = mysqlConnection.updateReturnDate(newReturnDate, cardNum, borrowDate);
//
//						if (!isUpdated) {
//							allUpdatesSuccessful = false; // If any update fails, set this to false
//							System.out.println("Failed to update return date for cardNum: " + cardNum + ", borrowDate: "
//									+ borrowDate);
//						}
//					}
//				}
//
//				// Populate the response with operation result
//				response.put("operation", "updateReturnDates");
//				response.put("success", allUpdatesSuccessful);
//
//				// Send the response back to the client
//				try {
//					client.sendToClient(response);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			} else {
//				System.out.println("Invalid data received for updateReturnDates.");
//			}
//		}

	}

	/**
	 * This method retrieves the actual IP address of the machine. It skips loopback
	 * addresses (e.g., 127.0.0.1) and focuses on site-local IPv4 addresses (e.g.,
	 * 192.168.x.x or 10.x.x.x).
	 * 
	 * @return The actual IP address of the machine as a String, or "Unable to
	 *         determine IP address" if no valid IP address is found.
	 */

	/**
	 * This method overrides the one in the superclass. Called when the server
	 * starts listening for connections.
	 */
	protected void serverStarted() {
		System.out.println("Server listening for connections on port: " + getPort());

	}

	/**
	 * This method overrides the one in the superclass. Called when the server stops
	 * listening for connections.
	 */
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}
}
//End of EchoServer class
