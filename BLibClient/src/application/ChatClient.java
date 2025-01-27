package application;

import ocsf.client.*;

import common.ChatIF;
import gui.bounderies.BookDetailsFrameController;
import gui.bounderies.ClientFrameController;
import gui.bounderies.HomeFrameController;
import gui.bounderies.LoanFrameController;
import gui.bounderies.LoginFrameController;
import gui.bounderies.ReturnFrameController;
import gui.bounderies.SeeAllFrameController;
import gui.bounderies.SubscriberCardDetailsController;
import gui.bounderies.TwoChartsController;
import gui.bounderies.ExtendPopupController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
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
import enteties.User;

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

	/** Controller for the client frame. */
    private ClientFrameController clientFrameController;

    /** Controller for the home frame. */
    private HomeFrameController homeFrameController;

    /** Controller for viewing all books. */
    private SeeAllFrameController seeAllFrameController;

    /** Controller for book details. */
    private BookDetailsFrameController bookDetailsFrameController;

    /** Controller for subscriber card details. */
    private SubscriberCardDetailsController subscriberCardDetailsController;

    /** Controller for extending book loans. */
    private ExtendPopupController extendPopupController;

    /** Controller for the login frame. */
    private LoginFrameController loginFrameController;

    /** Controller for loaning books. */
    private LoanFrameController loanFrameController;

    /** Controller for viewing reports and charts. */
    private TwoChartsController twoChartsController;
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

	 /**
     * Sets the client frame controller.
     * @param clientFrameController The controller instance.
     */
    public void setClientFrameController(ClientFrameController clientFrameController) {
        this.clientFrameController = clientFrameController;
    }

    /**
     * Sets the See All frame controller.
     * @param seeAllFrameController The controller instance.
     */
    public void setSeeAllFrameController(SeeAllFrameController seeAllFrameController) {
        this.seeAllFrameController = seeAllFrameController;
    }

    /**
     * Sets the home frame controller.
     * @param homeFrameController The controller instance.
     */
    public void setHomeFrameController(HomeFrameController homeFrameController) {
        this.homeFrameController = homeFrameController;
    }

    /**
     * Sets the book details frame controller.
     * @param bookDetailsFrameController The controller instance.
     */
    public void setBookDetailsFrameController(BookDetailsFrameController bookDetailsFrameController) {
        this.bookDetailsFrameController = bookDetailsFrameController;
    }

    /**
     * Sets the subscriber card details controller.
     * @param subscriberCardDetailsController The controller instance.
     */
    public void setSubscriberCardDetailsController(SubscriberCardDetailsController subscriberCardDetailsController) {
        this.subscriberCardDetailsController = subscriberCardDetailsController;
    }

    /**
     * Sets the extend popup controller.
     * @param extendPopupController The controller instance.
     */
    public void setExtendPopupController(ExtendPopupController extendPopupController) {
        this.extendPopupController = extendPopupController;
    }

    /**
     * Sets the login frame controller.
     * @param loginFrameController The controller instance.
     */
    public void setLoginFrameController(LoginFrameController loginFrameController) {
        this.loginFrameController = loginFrameController;
    }

    /**
     * Sets the loan frame controller.
     * @param loanFrameController The controller instance.
     */
    public void setLoanFrameController(LoanFrameController loanFrameController) {
        this.loanFrameController = loanFrameController;
    }

    /**
     * Sets the two charts controller.
     * @param twoChartsController The controller instance.
     */
    public void setTwoChartsController(TwoChartsController twoChartsController) {
        this.twoChartsController = twoChartsController;
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

		String type = null;
		String status = null;
		String messageLog = null;

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
			break;
		case getEarliestReturnDate:
			try {
				String date = (String) message.getMessageData();
				if (bookDetailsFrameController != null) {
					bookDetailsFrameController.setEarliestReturnDate(date);
				} else {
					System.err.println("bookDetailsFrameController is not set in ChatClient.");
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			break;
		case orderBook:
			try {
				String ordered = (String) message.getMessageData();
				if (bookDetailsFrameController != null) {
					Platform.runLater(() -> {
						if (ordered.equals("success")) {
							bookDetailsFrameController.setAlreadyOrdered(true);
							showSuccessAlert("Order Successed");
						} else {
							bookDetailsFrameController.setAlreadyOrdered(false);
							showErrorAlert("Order not set! ask the libraian");
						}
					});
				} else {
					System.err.println("bookDetailsFrameController is not set in ChatClient.");
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			break;
		case checkOrderBook:
			try {
				String ordered = (String) message.getMessageData();
				if (bookDetailsFrameController != null) {
					Platform.runLater(() -> {
						if (ordered.equals("success"))
							bookDetailsFrameController.setCheckOrderBook(true, ordered);
						else
							bookDetailsFrameController.setCheckOrderBook(false, ordered);
					});
				} else {
					System.err.println("bookDetailsFrameController is not set in ChatClient.");
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			break;
		case registerSubscriber:
			Map<String, String> regiter = (Map<String, String>) message.getMessageData();

			type = regiter.get("type");
			messageLog = regiter.get("message");
			if (type.equals("success"))
				showSuccessAlert(messageLog);
			else
				showErrorAlert(messageLog);
			break;

		case cardNumber:
			HashMap<String, Object> response = (HashMap<String, Object>) ((Message) msg).getMessageData();

			// Pass the data to the controller
			if (subscriberCardDetailsController != null) {
				subscriberCardDetailsController.cardExist(response);
			} else {
				System.out.println("3-subscriberCardDetailsController is null.");
			}
			break;

		case updateEmailAndPhone:
			// Get the boolean value indicating if the update was successful
			boolean isUpdateSuccessful = (boolean) message.getMessageData();
			// Call the method btnUpdateDetailsClickedCheck with the result
			if (subscriberCardDetailsController != null) {
				subscriberCardDetailsController.btnUpdateDetailsClickedCheck(isUpdateSuccessful);
			} else {
				System.out.println("1- subscriberCardDetailsController is null.");
			}
			break;

		case updateReturnDate:
			boolean isDateUpdated = (boolean) message.getMessageData();
			// Pass the data to the controller
			if (subscriberCardDetailsController != null) {
				subscriberCardDetailsController.btnUpdateReturnDateCheck(isDateUpdated);
			} else {
				System.out.println("2-subscriberCardDetailsController is null.");
			}
			break;

		case bookExtentionTable:
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
		case login:
			try {
				User user = (User) message.getMessageData();
				System.out.println("user is: " + user);
				if (loginFrameController != null) {
					loginFrameController.setUser(user);
				} else {
					System.err.println("HomeFrameController is not set in ChatClient.");
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			break;

		case returnBook:
			Map<String, String> isReturnSuccessful = (Map<String, String>) message.getMessageData();
			type = isReturnSuccessful.get("type");
			messageLog = isReturnSuccessful.get("message");
			if (type.equals("success"))
				showSuccessAlert(messageLog);
			else if (type.equals("noLoan") || type.equals("frozen") || type.equals("error") || type.equals("Lost")
					|| type.equals("lateWithoutFreeze"))
				showErrorAlert(messageLog);
			break;
		case checkStatus:
			Map<String, String> checkStatus = (Map<String, String>) message.getMessageData();
			type = checkStatus.get("type");
			status = checkStatus.get("status");
			messageLog = checkStatus.get("message");
			if (type.equals("notFound") || (type.equals("found") && status.equals("Frozen")))
				showErrorAlert(messageLog);
			if (type.equals("found") && status.equals("Active")) {
				try {

					if (loanFrameController != null) {
						loanFrameController.setActive();
					} else {
						System.err.println("LoanFrameController is not set in ChatClient.");
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
			break;
		case loan:
			Map<String, String> loan = (Map<String, String>) message.getMessageData();
			type = loan.get("type");
			messageLog = loan.get("message");
			if (type.equals("notFound") || type.equals("unsuccessful") || type.equals("alreadyLoaned"))
				showErrorAlert(messageLog);
			if (type.equals("success"))
				showSuccessAlert(messageLog);
			break;

		case loanReport:
			Platform.runLater(() -> {
				try {
					Map<String, Map<String, String>> loanReportMap = (Map<String, Map<String, String>>) message
							.getMessageData();

					if (loanReportMap == null || loanReportMap.isEmpty()) {
						showAlert("No Data Found", "No loan report data is available for the selected month and year.");
						return;
					}

					if (twoChartsController != null) {
						twoChartsController.showLoanReportData(loanReportMap);
					} else {
						System.err.println("twoChartsController is not set in ChatClient.");
					}
				} catch (Exception e) {
					System.err.println("Error processing return case: " + e.getMessage());
				}
			});
			break;
		case StatusReport:
			Platform.runLater(() -> {
				try {
					Map<String, String> statusReportMap = (Map<String, String>) message.getMessageData();

					if (statusReportMap == null || statusReportMap.isEmpty()) {
						showAlert("No Data Found",
								"No status report data is available for the selected month and year.");
						return;
					}

					if (twoChartsController != null) {
						twoChartsController.showStatusReportData(statusReportMap);
					} else {
						System.err.println("twoChartsController is not set in ChatClient.");
					}
				} catch (Exception e) {
					System.err.println("Error processing return case: " + e.getMessage());
				}
			});
			break;

		}

	}

	/**
	 * This method handles all data coming from the UI
	 *
	 * @param message The message from the UI.
	 */

	public void handleMessageFromClientUI(Object obj) {
		try {
			openConnection();
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
     * Displays an informational alert.
     *
     * @param title The alert title.
     * @param content The alert content.
     */
	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
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

	/**
     * Displays a success alert with the specified message.
     *
     * @param message The success message to display.
     */
	private void showSuccessAlert(String message) {
		javafx.application.Platform.runLater(() -> {
			javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
					javafx.scene.control.Alert.AlertType.INFORMATION);
			alert.setHeaderText("Success");
			alert.setContentText(message);
			alert.showAndWait();
		});
	}

	/**
     * Displays an error alert with the specified message.
     *
     * @param message The error message to display.
     */
	private void showErrorAlert(String message) {
		javafx.application.Platform.runLater(() -> {
			javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
					javafx.scene.control.Alert.AlertType.ERROR);
			alert.setHeaderText("Error");
			alert.setContentText(message);
			alert.showAndWait();
		});
	}

}
