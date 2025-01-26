package gui.bounderies;

import java.io.IOException;
import java.io.Serializable;

import message.Message;
import message.MessageType;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import application.ChatClient;
import application.ClientUI;
import enteties.Book;
import enteties.IssueHistory;
import enteties.Librarian;
import enteties.Loan;
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

public class SubscriberCardDetailsController {

	private ChatClient chatClient;

	@FXML
	private Button btnBack;
	@FXML
	private Button btnUpdateDetails;

	@FXML
	private Button btnUpdateDates;

	@FXML
	private Button btnExtend;

	@FXML
	private Button btnSearch;

	@FXML
	private TextField tfInsertCardNumber;

	@FXML
	private TextField tfCardNumber;

	@FXML
	private TextField tfUserName;

	@FXML
	private TextField tfPhoneNumber;

	@FXML
	private TextField tfEmail;

	@FXML
	private Label lblUserName;

	@FXML
	private Label lblInsertCardNumber;

	@FXML
	private Label lblPhoneNumber;

	@FXML
	private Label lblEmail;

	@FXML
	private Label lblCardNumber;

	@FXML
	private Label lblLoanHistory;

	@FXML
	private Label lblIssueHistory;

	@FXML
	private TableView<Loan> tableLoanHistory;

	@FXML
	private TableColumn<Loan, String> colBookTitle;

	@FXML
	private TableColumn<Loan, String> colBorrowDate;

	@FXML
	private TableColumn<Loan, String> colReturnDate;

	@FXML
	private TableView<IssueHistory> tableIssuesHistory;

	@FXML
	private TableColumn<IssueHistory, String> colIssueType;

	@FXML
	private TableColumn<IssueHistory, String> colIssueDate;

	@FXML
	private TableColumn<IssueHistory, String> colIssueDescription;

	@FXML
	private Label lblInvalidCardNumber;

	@FXML
	private Label lblPhoneNumberMessage;

	@FXML
	private Label lblEmailmessage;

	@FXML
	private Label lblEmailPhonemessage;

	@FXML
	private Label lblUpdateDateMessage;

	private Map<Loan, ArrayList<Object>> updatedReturnDates = new HashMap<>();
	
	// public static String type = "subscriber";

	public SubscriberCardDetailsController() {
		chatClient = ClientUI.chat.getClient();
	}

	public void setChatClient(ChatClient chatClient) {
		this.chatClient = chatClient;
		this.chatClient.setSubscriberCardDetailsController(this);
	}

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

	// bulululu
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
							(String) issue.get("barcode")));
				}
			}
		});
	}

	// Utility method to format java.sql.Date or other date objects to String
	private String formatDate(Object dateObject) {
		if (dateObject instanceof java.sql.Date) {
			java.sql.Date sqlDate = (java.sql.Date) dateObject;
			return sqlDate.toString(); // Default formatting (YYYY-MM-DD)
		}
		// You can add more date object handling here if needed
		return dateObject != null ? dateObject.toString() : "";
	}

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

	// Helper method to validate email format
	private boolean isValidEmail(String email) {
		return email != null
				&& email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
	}

	// Helper method to validate phone number format
	private boolean isValidPhoneNumber(String phoneNumber) {
		return phoneNumber != null && phoneNumber.matches("\\d{10}");
	}

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

		btnUpdateDates.setVisible(false);

		lblEmailPhonemessage.setVisible(false);
		lblEmailmessage.setVisible(false);
		lblPhoneNumberMessage.setVisible(false);

		lblUpdateDateMessage.setVisible(false);
		btnExtend.setVisible(false);
	}

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