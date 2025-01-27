package gui.bounderies;

import java.io.IOException;

import message.Message;
import message.MessageType;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.ChatClient;
import application.ClientUI;
import enteties.Book;
import enteties.IssueHistory;
import enteties.Librarian;
import enteties.Loan;
import enteties.Order;
import enteties.Subscriber;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
/**
 * Controller for managing subscriber card details.
 */
public class SubscriberCardDetailsController {

	/** The chat client for communication with the server. */
    private ChatClient chatClient;

    /** Button to navigate back to the previous screen. */
    @FXML
    private Button btnBack;

    /** Button to update subscriber details. */
    @FXML
    private Button btnUpdateDetails;

    /** Button to update loan return dates. */
    @FXML
    private Button btnUpdateDates;

    /** Button to request an extension for book loans. */
    @FXML
    private Button btnExtend;

    /** Button to search for subscriber details. */
    @FXML
    private Button btnSearch;

    /** TextField to enter a card number for searching. */
    @FXML
    private TextField tfInsertCardNumber;

    /** TextField displaying the subscriber's card number. */
    @FXML
    private TextField tfCardNumber;

    /** TextField displaying the subscriber's name. */
    @FXML
    private TextField tfUserName;

    /** TextField displaying the subscriber's phone number. */
    @FXML
    private TextField tfPhoneNumber;

    /** TextField displaying the subscriber's email. */
    @FXML
    private TextField tfEmail;

    /** Label for displaying the subscriber's name. */
    @FXML
    private Label lblUserName;

    /** Label for the order history section. */
    @FXML
    private Label lblOrderHistory;

    /** Label for prompting card number input. */
    @FXML
    private Label lblInsertCardNumber;

    /** Label for the phone number field. */
    @FXML
    private Label lblPhoneNumber;

    /** Label for the email field. */
    @FXML
    private Label lblEmail;

    /** Label for the card number field. */
    @FXML
    private Label lblCardNumber;

    /** Label for the loan history section. */
    @FXML
    private Label lblLoanHistory;

    /** Label for the issue history section. */
    @FXML
    private Label lblIssueHistory;

    /** Table to display the subscriber's loan history. */
    @FXML
    private TableView<Loan> tableLoanHistory;

    /** Column for displaying book titles in loan history. */
    @FXML
    private TableColumn<Loan, String> colBookTitle;

    /** Column for displaying borrow dates in loan history. */
    @FXML
    private TableColumn<Loan, String> colBorrowDate;

    /** Column for displaying return dates in loan history. */
    @FXML
    private TableColumn<Loan, String> colReturnDate;

    /** Table to display the subscriber's issue history. */
    @FXML
    private TableView<IssueHistory> tableIssuesHistory;

    /** Column for displaying issue types in issue history. */
    @FXML
    private TableColumn<IssueHistory, String> colIssueType;

    /** Column for displaying issue dates in issue history. */
    @FXML
    private TableColumn<IssueHistory, String> colIssueDate;

    /** Column for displaying issue descriptions in issue history. */
    @FXML
    private TableColumn<IssueHistory, String> colIssueDescription;

    /** Table to display the subscriber's order history. */
    @FXML
    private TableView<Order> tableOrderHistory;

    /** Column for displaying order dates in order history. */
    @FXML
    private TableColumn<Order, String> colOrderDate;

    /** Column for displaying ordered book titles. */
    @FXML
    private TableColumn<Order, String> colOrderBookTitle;

    /** Label to display invalid card number messages. */
    @FXML
    private Label lblInvalidCardNumber;

    /** Label to display phone number validation messages. */
    @FXML
    private Label lblPhoneNumberMessage;

    /** Label to display email validation messages. */
    @FXML
    private Label lblEmailmessage;

    /** Label to display general email and phone number update messages. */
    @FXML
    private Label lblEmailPhonemessage;

    /** Label to display return date update messages. */
    @FXML
    private Label lblUpdateDateMessage;

    /** Map to store updated return dates for processing. */
    private Map<Loan, ArrayList<Object>> updatedReturnDates = new HashMap<>();


    /**
     * Constructor for initializing the controller.
     */
	public SubscriberCardDetailsController() {
		chatClient = ClientUI.chat.getClient();
	}

	
	/**
     * Sets the chat client for communication.
     *
     * @param chatClient The ChatClient instance to use.
     */
	public void setChatClient(ChatClient chatClient) {
		this.chatClient = chatClient;
		this.chatClient.setSubscriberCardDetailsController(this);
	}

	
	 /**
     * Initializes the controller by setting up UI components and table configurations.
     */
	@FXML
	private void initialize() {
		// Hide all components
		hideComponents();

		// Configure table columns
		colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
		colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
		colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

		colReturnDate.setCellFactory(TextFieldTableCell.forTableColumn());

		colIssueType.setCellValueFactory(new PropertyValueFactory<>("issueType"));
		colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
		colIssueDescription.setCellValueFactory(new PropertyValueFactory<>("issueDescription"));

		colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
		colOrderBookTitle.setCellValueFactory(new PropertyValueFactory<>("title"));

		// Handle editing of the Return Date column
		colReturnDate.setOnEditCommit(event -> {
			Loan loanRow = event.getRowValue();
			loanRow.setReturnDate(event.getNewValue());

			// subscriber[0] -> ID, librarian[1] -> ID, book[2] -> name
			ArrayList<Object> details = new ArrayList<>();
			details.add(new Subscriber(tfInsertCardNumber.getText()));
			details.add(new Librarian(ClientUI.user.getID()));
			details.add(new Book(loanRow.getBookTitle()));
			updatedReturnDates.put(loanRow, details);

		});

		if (ClientUI.user instanceof Subscriber) {
			showComponents();
			suscriberComponents();
		}
	}

	
	
	/**
     * Handles the search button click event.
     *
     * @param event The action event.
     */
	@FXML
	public void btnSearchClicked(ActionEvent event) {
		String cardNumber;
		if (ClientUI.user instanceof Subscriber) {
			suscriberComponents();
			// Get the inserted card number from the TextField
			cardNumber = ClientUI.user.getID();
		} else {
			// Get the inserted card number from the TextField
			cardNumber = tfInsertCardNumber.getText();
		}

		// Check if the card number is empty
		if (cardNumber.isEmpty()) {
			hideComponents();
			lblInvalidCardNumber.setText("Please enter a card number.");
			lblInvalidCardNumber.setVisible(true);
			tfCardNumber.clear();
			return;
		}

		Message sendToServer = new Message(MessageType.cardNumber, cardNumber);
		ClientUI.chat.accept(sendToServer);
	}

	void noBtnForSubscriber() {
		// Get the inserted card number from the TextField
		String cardNumber = ClientUI.user.getID();

		Message sendToServer = new Message(MessageType.cardNumber, cardNumber);
		ClientUI.chat.accept(sendToServer);
	}

	/**
     * Handles when subscriber card details exist and updates the UI accordingly.
     *
     * @param cardDetails A map containing card details.
     */
	public void cardExist(Map<String, Object> cardDetails) {
		boolean cardExists = (boolean) cardDetails.get("exists");

		// Use Platform.runLater to update the UI on the JavaFX Application Thread
		Platform.runLater(() -> {
			if (!cardExists) {
				// If the card does not exist, show the "Invalid Card" label
				hideComponents();
				tfInsertCardNumber.getStyleClass().add("text-field-invalid");
				lblInvalidCardNumber.setText("Invalid card number.");
				lblInvalidCardNumber.setVisible(true);
				tfCardNumber.clear();
				tfUserName.clear();
				tfPhoneNumber.clear();
				tfEmail.clear();
				tableLoanHistory.getItems().clear(); // Clear the loan history table
				tableIssuesHistory.getItems().clear(); // Clear the issues history table
				return;
			}

			// If the card exists, hide the "Invalid Card" label
			lblInvalidCardNumber.setVisible(false);

			tfInsertCardNumber.getStyleClass().remove("text-field-invalid");

			if (ClientUI.user instanceof Librarian) {
				librarianComponents();
				// Make the labels and buttons visible
				showComponents();
			}

			tfCardNumber.setText((String) cardDetails.get("cardNum"));
			tfUserName.setText((String) cardDetails.get("username"));
			tfPhoneNumber.setText((String) cardDetails.get("phoneNumber"));
			tfEmail.setText((String) cardDetails.get("email"));

			// Populate the loan history table
			List<Map<String, Object>> loanHistory = (List<Map<String, Object>>) cardDetails.get("loanHistory");
			if (loanHistory != null) {
				tableLoanHistory.getItems().clear();
				for (Map<String, Object> loan : loanHistory) {
					// Convert borrowDate and returnDate to String if they are java.sql.Date
					String borrowDateStr = formatDate(loan.get("borrowDate"));
					String returnDateStr = formatDate(loan.get("returnDate"));

					tableLoanHistory.getItems().add(new Loan((String) loan.get("title"), borrowDateStr, returnDateStr));
				}
			}

			// Populate the issues history table
			List<Map<String, Object>> issuesHistory = (List<Map<String, Object>>) cardDetails.get("issuesHistory");
			if (issuesHistory != null) {
				tableIssuesHistory.getItems().clear();
				for (Map<String, Object> issue : issuesHistory) {
					// Convert issueDate to String if it is java.sql.Date
					String issueDateStr = formatDate(issue.get("issueDate"));

					tableIssuesHistory.getItems().add(new IssueHistory((String) issue.get("issueType"), issueDateStr,
							(String) issue.get("title")));
				}
			}

			// Order history table
			List<Map<String, Object>> orderHistory = (List<Map<String, Object>>) cardDetails.get("orderHistory");
			System.out.println("tableOrderHistory =======" + tableOrderHistory.toString());
			if (orderHistory != null) {
				tableOrderHistory.getItems().clear();
				for (Map<String, Object> order : orderHistory) {
					String orderDateStr = formatDate(order.get("orderDate"));
					tableOrderHistory.getItems().add(new Order(orderDateStr, (String) order.get("bookTitle"), true));
				}
			}
		});
	}


	 /**
     * Formats a given date object into a string.
     *
     * @param dateObject The date object to format.
     * @return A formatted date string.
     */
	private String formatDate(Object dateObject) {
		if (dateObject instanceof java.sql.Date) {
			java.sql.Date sqlDate = (java.sql.Date) dateObject;
			return sqlDate.toString(); // Default formatting (YYYY-MM-DD)
		}
		// You can add more date object handling here if needed
		return dateObject != null ? dateObject.toString() : "";
	}

	
	/**
     * Handles the update details button click event.
     *
     * @param event The action event.
     */
	@FXML
	private void btnUpdateDetailsClicked(ActionEvent event) {
		// Get the inserted details from the text fields
		String email = tfEmail.getText();
		String phoneNumber = tfPhoneNumber.getText();
		String cardNumber = tfCardNumber.getText();

		if (!isValidEmail(email) || !isValidPhoneNumber(phoneNumber)) {
			// Validate the email format
			if (!isValidEmail(email)) {
				lblEmailmessage.setVisible(true);
				lblEmailmessage.setText("Invalid email format.");
				tfEmail.getStyleClass().add("text-field-invalid");
				lblEmailmessage.setStyle("-fx-text-fill: red;");
			} else {
				lblEmailmessage.setVisible(false);
				lblEmailmessage.setText(""); // Clear any previous error message
				tfEmail.getStyleClass().remove("text-field-invalid");
			}

			// Validate the phone number format (assuming it should only contain digits and
			// be 10 characters long)
			if (!isValidPhoneNumber(phoneNumber)) {
				lblPhoneNumberMessage.setVisible(true);
				lblPhoneNumberMessage.setText("Invalid phone number format.");
				tfPhoneNumber.getStyleClass().add("text-field-invalid");
				lblPhoneNumberMessage.setStyle("-fx-text-fill: red;");
			} else {
				lblPhoneNumberMessage.setVisible(false);
				lblPhoneNumberMessage.setText(""); // Clear any previous error message
				tfPhoneNumber.getStyleClass().remove("text-field-invalid");
			}
			return;
		}

		tfPhoneNumber.getStyleClass().remove("text-field-invalid");
		tfEmail.getStyleClass().remove("text-field-invalid");
		lblPhoneNumberMessage.setVisible(false);
		lblEmailmessage.setVisible(false);

		// Create the command to send to the server
		String command = String.format("%s %s %s", email, phoneNumber, cardNumber);

		// Send the command via ClientUI.chat
		Message sendToServer = new Message(MessageType.updateEmailAndPhone, command);
		ClientUI.chat.accept(sendToServer);
	}

	/**
	 * Validates the format of the provided email address.
	 *
	 * @param email The email address to validate.
	 * @return True if the email format is valid, false otherwise.
	 */
	private boolean isValidEmail(String email) {
		return email != null
				&& email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
	}

	/**
	 * Validates the format of the provided phone number.
	 *
	 * @param phoneNumber The phone number to validate.
	 * @return True if the phone number format is valid (10-digit numeric), false otherwise.
	 */
	private boolean isValidPhoneNumber(String phoneNumber) {
		return phoneNumber != null && phoneNumber.matches("\\d{10}");
	}

	
	/**
     * Checks the response after updating subscriber details.
     *
     * @param isUpdated Boolean indicating success or failure of the update.
     */
	public void btnUpdateDetailsClickedCheck(boolean isUpdated) {
		Platform.runLater(() -> {
			if (isUpdated) {
				// Show success message in green
				lblEmailPhonemessage.setVisible(true);
				lblEmailPhonemessage.setText("Details updated successfully!");
				lblEmailPhonemessage.setStyle("-fx-text-fill: green;");
				tfPhoneNumber.getStyleClass().remove("text-field-invalid");
				tfEmail.getStyleClass().remove("text-field-invalid");
				lblPhoneNumberMessage.setVisible(false);
				lblEmailmessage.setVisible(false);
			} else {
				// Show failure message in red
				lblEmailPhonemessage.setText("Failed to update details. Please try again.");
				lblEmailPhonemessage.setStyle("-fx-text-fill: red;");
			}
		});
	}

	
	/**
     * Handles the update date button click event.
     *
     * @param event The action event.
     */
	@FXML
	private void btnUpdateDateClicked(ActionEvent event) {

		LocalDate currentDate = LocalDate.now();

		for (Loan loan : updatedReturnDates.keySet()) {
			LocalDate returnDate = LocalDate.parse(loan.getReturnDate());

			try {
				if (returnDate.isBefore(currentDate)) {
					lblUpdateDateMessage.setVisible(true);
					lblUpdateDateMessage
							.setText("Invalid return date for " + loan.getBookTitle() + ": cannot be before today.");
					lblUpdateDateMessage.setStyle("-fx-text-fill: red;");
					return; // Stop the process if validation fails
				}

			} catch (DateTimeParseException e) {
				// Handle invalid date format
				lblUpdateDateMessage.setVisible(true);
				lblUpdateDateMessage
						.setText("Invalid date format for " + loan.getBookTitle() + ". Please use YYYY-MM-DD.");
				lblUpdateDateMessage.setStyle("-fx-text-fill: red;");
				return; // Stop the process if validation fails
			}
		}

		// Send the data to the server
		Message sendToServer = new Message(MessageType.updateReturnDate, updatedReturnDates);
		ClientUI.chat.accept(sendToServer);

		// Optionally clear the message label after the process is done
		lblUpdateDateMessage.setVisible(false);
	}

	
	
	/**
     * Checks the response after updating return dates.
     *
     * @param isUpdated Boolean indicating success or failure of the update.
     */
	public void btnUpdateReturnDateCheck(boolean isUpdated) {
		Platform.runLater(() -> {
			if (isUpdated) {
				// Show success message in green
				lblUpdateDateMessage.setVisible(true);
				lblUpdateDateMessage.setText("Details updated successfully!");
				lblUpdateDateMessage.setStyle("-fx-text-fill: green;");

			} else {
				// Show failure message in red
				lblUpdateDateMessage.setText("Failed to update details. Please try again.");
				lblUpdateDateMessage.setStyle("-fx-text-fill: red;");
			}
		});

	}

	
	/**
     * Handles the back button click event.
     *
     * @param event The action event.
     */
	@FXML
	private void btnBackClicked(ActionEvent event) {
		Stage primaryStage = new Stage();
		((Node) event.getSource()).getScene().getWindow().hide();
		HomeFrameController homeFrameController = new HomeFrameController();
		try {
			homeFrameController.start(primaryStage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	/**
     * Handles the extend loan button click event.
     *
     * @param event The action event.
     */
	@FXML
	private void btnExtendClicked(ActionEvent event) {

		try {

			lblPhoneNumberMessage.setText("");
			lblEmailmessage.setText("");
			lblEmailPhonemessage.setText("");

			tfPhoneNumber.getStyleClass().remove("text-field-invalid");
			tfEmail.getStyleClass().remove("text-field-invalid");

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/Extend.fxml"));
			Parent root = loader.load();

			// Get the controller of the popup
			ExtendPopupController extendController = loader.getController();
			extendController.setChatClient(chatClient);
			extendController.setSubscriberCardDetailsController(this);
			// Pass the card number to the popup controller
			extendController.setCardNumber(tfCardNumber.getText());

			// Explicitly call the method to populate the table after setting the card
			// number
			extendController.tableFillRequest();

			Stage popupStage = new Stage();
			popupStage.setTitle("Request Extension");
			popupStage.initModality(Modality.APPLICATION_MODAL);
			popupStage.setScene(new Scene(root));
			popupStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
     * Hides the UI components related to subscriber details.
     */
	private void hideComponents() {

		lblCardNumber.setVisible(false);
		tfCardNumber.setVisible(false);

		lblUserName.setVisible(false);
		tfUserName.setVisible(false);

		lblPhoneNumber.setVisible(false);
		tfPhoneNumber.setVisible(false);

		lblEmail.setVisible(false);
		tfEmail.setVisible(false);

		btnUpdateDetails.setVisible(false);

		lblLoanHistory.setVisible(false);
		tableLoanHistory.setVisible(false);

		lblIssueHistory.setVisible(false);
		tableIssuesHistory.setVisible(false);

		lblOrderHistory.setVisible(false);
		tableOrderHistory.setVisible(false);

		btnUpdateDates.setVisible(false);

		lblEmailPhonemessage.setVisible(false);
		lblEmailmessage.setVisible(false);
		lblPhoneNumberMessage.setVisible(false);

		lblUpdateDateMessage.setVisible(false);
		btnExtend.setVisible(false);
	}

	
	/**
     * Shows the UI components related to subscriber details.
     */
	private void showComponents() {
		lblCardNumber.setVisible(true);
		tfCardNumber.setVisible(true);

		lblUserName.setVisible(true);
		tfUserName.setVisible(true);

		lblPhoneNumber.setVisible(true);
		tfPhoneNumber.setVisible(true);

		lblEmail.setVisible(true);
		tfEmail.setVisible(true);

		lblLoanHistory.setVisible(true);
		tableLoanHistory.setVisible(true);

		lblIssueHistory.setVisible(true);
		tableIssuesHistory.setVisible(true);

		lblOrderHistory.setVisible(true);
		tableOrderHistory.setVisible(true);

		colReturnDate.setVisible(true);
	}

	// bulululu
	private void suscriberComponents() {
		lblInsertCardNumber.setVisible(false);
		lblInvalidCardNumber.setVisible(false);
		btnSearch.setVisible(false);
		lblInvalidCardNumber.setVisible(false);
		tfInsertCardNumber.setVisible(false);
		colReturnDate.setEditable(false);
		btnUpdateDates.setVisible(false);
		btnUpdateDetails.setVisible(true);
		btnExtend.setVisible(true);
		tfCardNumber.setEditable(false);
		tfUserName.setEditable(false);

	}

	// bulululu
	private void librarianComponents() {
		colReturnDate.setEditable(true);
		btnUpdateDetails.setVisible(false);
		btnUpdateDates.setVisible(true);
		tfCardNumber.setEditable(false);
		tfUserName.setEditable(false);
		tfPhoneNumber.setEditable(false);
		tfEmail.setEditable(false);

	}

	
	 /**
     * Starts the subscriber details window.
     *
     * @param primaryStage The primary stage to display the UI.
     * @throws Exception if the UI fails to load.
     */
	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/SubscriberCardDetails.fxml"));
			Parent root = loader.load();
			SubscriberCardDetailsController controller = loader.getController();
			if (this.chatClient != null) {
				controller.setChatClient(this.chatClient);
			}

			Scene scene = new Scene(root);
			primaryStage.setTitle("subscriber details");
			primaryStage.setScene(scene);
			primaryStage.show();

			if (ClientUI.user instanceof Subscriber) {

				noBtnForSubscriber();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}