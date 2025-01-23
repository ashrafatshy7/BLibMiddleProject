package application;

import java.io.*;
import java.net.InetAddress;
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
import enteties.Subscriber;
import enteties.User;
import message.Message;
import message.MessageType;
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
			System.out.println("Message arrived to server: " + messageType);

			switch (messageType) {
			case disconnectFromServer:
				clientDisconnected(client);
				client.sendToClient(new Message(MessageType.disconnectFromServer, "You have been disconnected."));
				return;
			case getAllBooks:
				ArrayList<Book> allBooks = mysqlConnection.getAllBooks();
				messageFromServer = new Message(MessageType.getAllBooks, allBooks);
				client.sendToClient(messageFromServer);
				break;
			case getTop5LoanedBooks:
				ArrayList<Book> topLoaned = mysqlConnection.getTop5LoanedBooks();
				messageFromServer = new Message(MessageType.getTop5LoanedBooks, topLoaned);
				client.sendToClient(messageFromServer);
				break;
			case getEarliestReturnDate:
				String date = mysqlConnection.getEarliestReturnDate(message.getMessageData());
				messageFromServer = new Message(MessageType.getEarliestReturnDate, date);
				client.sendToClient(messageFromServer);
				break;
			case orderBook:
				boolean order = mysqlConnection.orderBook(message.getMessageData());
				messageFromServer = new Message(MessageType.orderBook, order);
				client.sendToClient(messageFromServer);
				break;
			case checkOderBook:
				boolean ordered = mysqlConnection.checkOrderedBook(message.getMessageData());
				messageFromServer = new Message(MessageType.checkOderBook, ordered);
				client.sendToClient(messageFromServer);
				break;
			case registerSubscriber:
				boolean success = mysqlConnection.saveNewSubscriber(message.getMessageData());
				Message response = new Message(MessageType.registerSubscriber, success);
				client.sendToClient(response);
				break;

			case cardNumber:
				Map<String, Object> cardDetailsIfExists = mysqlConnection
						.getCardDetailsIfExists(message.getMessageData().toString());
				messageFromServer = new Message(MessageType.cardNumber, cardDetailsIfExists);

				client.sendToClient(messageFromServer);
				break;

			case updateEmailAndPhone:
				ArrayList<String> allData = new ArrayList<>(
						Arrays.asList(((String) message.getMessageData()).split(" ")));
				String email = allData.get(0);
				String phoneNumber = allData.get(1);
				String cardNum = allData.get(2);
				boolean isUpdated = mysqlConnection.updateSubscriberEmailAndPhoneNumber(email, phoneNumber, cardNum);
				Message isUpdatedMessage = new Message(MessageType.updateEmailAndPhone, isUpdated);
				try {
					client.sendToClient(isUpdatedMessage);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			case updateReturnDate:
				boolean isReturnDateUpdated = mysqlConnection.updateReturnDate(message.getMessageData());
				messageFromServer = new Message(MessageType.updateReturnDate, isReturnDateUpdated);

				client.sendToClient(messageFromServer);
				break;

			case bookExtentionTable:
				Map<String, String> booksCanExtend = mysqlConnection.getExtendedBooks(message.getMessageData());
				messageFromServer = new Message(MessageType.bookExtentionTable, booksCanExtend);
				client.sendToClient(messageFromServer);
				break;

			case bookExtensionSucceeded:
				boolean isExtentionUpdated = mysqlConnection.updateExtensionReturnDate(message.getMessageData());
				messageFromServer = new Message(MessageType.bookExtensionSucceeded, isExtentionUpdated);
				client.sendToClient(messageFromServer);
				break;
			case login:
				User user = mysqlConnection.login(message.getMessageData());
				messageFromServer = new Message(MessageType.login, user);
				client.sendToClient(messageFromServer);
				break;

			case returnBook:
				Map<String, String> returnBook = mysqlConnection.returnBook(message.getMessageData());
				messageFromServer = new Message(MessageType.returnBook, returnBook);
				client.sendToClient(messageFromServer);
				break;
			case checkStatus:
				Map<String, String> checkStatus = mysqlConnection.checkSubscriberStatus(message.getMessageData());
				messageFromServer = new Message(MessageType.checkStatus, checkStatus);
				client.sendToClient(messageFromServer);
				break;
			case loan:
				Map<String, String> loan = mysqlConnection.createLoan(message.getMessageData());
				messageFromServer = new Message(MessageType.loan, loan);
				client.sendToClient(messageFromServer);
				break;

			case loanReport:
				Map<String, Map<String, String>> loanChartData = mysqlConnection
						.fetchLoanDataForReport(message.getMessageData());
				messageFromServer = new Message(MessageType.loanReport, loanChartData);
				client.sendToClient(messageFromServer);
				break;

			case StatusReport:
				Map<String, String> statusChartData = mysqlConnection
						.fetchStatusDataForReport(message.getMessageData());
				messageFromServer = new Message(MessageType.StatusReport, statusChartData);
				client.sendToClient(messageFromServer);
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
