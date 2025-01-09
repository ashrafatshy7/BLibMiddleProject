package gui.bounderies;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.ChatClient;
import application.ClientUI;
import enteties.CardDetails;
import enteties.IssueHistory;
import enteties.LoanHistory;
import enteties.Subscriber;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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
	private TableView<LoanHistory> tableLoanHistory;

	@FXML
	private TableColumn<LoanHistory, String> colBookTitle;

	@FXML
	private TableColumn<LoanHistory, LocalDate> colBorrowDate;

	@FXML
	private TableColumn<LoanHistory, LocalDate> colReturnDate;

	@FXML
	private TableView<IssueHistory> tableIssuesHistory;

	@FXML
	private TableColumn<IssueHistory, String> colIssueType;

	@FXML
	private TableColumn<IssueHistory, LocalDate> colIssueDate;

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

	private List<LoanHistory> loanHistoryList = new ArrayList<>();
	private List<IssueHistory> issueHistoryList = new ArrayList<>();

	@FXML
	private void initialize() {
		// Hide all components
		hideComponents();

		// Set the TableView to editable
		tableLoanHistory.setEditable(true);

		colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
		colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
		colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

		colIssueType.setCellValueFactory(new PropertyValueFactory<>("issueType"));
		colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
		colIssueDescription.setCellValueFactory(new PropertyValueFactory<>("issueDescription"));

	}

	public SubscriberCardDetailsController() {
		chatClient = ClientUI.chat.getClient();
	}

	public void setChatClient(ChatClient chatClient) {
		System.out.println("setChatClient check ");
		this.chatClient = chatClient;
		this.chatClient.setSubscriberCardDetailsController(this);
	}

	@FXML
	private void btnSearchClicked(ActionEvent event) {
		// Get the inserted card number from the TextField
		String cardNumber = tfInsertCardNumber.getText();

		// Check if the card number is empty
		if (cardNumber.isEmpty()) {
			hideComponents();
			lblInvalidCardNumber.setText("Please enter a card number.");
			lblInvalidCardNumber.setVisible(true);
			tfCardNumber.clear();
			return;
		}

		String command = String.format("cardDetails %s", cardNumber);
		ClientUI.chat.accept(command);
	}

	public void cardExist(boolean cardExists, Map<String, Object> cardDetails) {
		// Use Platform.runLater to update the UI on the JavaFX Application Thread
		Platform.runLater(() -> {
			if (!cardExists) {
				// If the card does not exist, show the "Invalid Card" label
				hideComponents();
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

			// Make the labels and buttons visible
			showComponents();

			// Set the card details in the respective TextFields
			if (cardDetails != null) {
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

						tableLoanHistory.getItems()
								.add(new LoanHistory((String) loan.get("bookTitle"), borrowDateStr, returnDateStr));
					}
				}

				// Populate the issues history table
				List<Map<String, Object>> issuesHistory = (List<Map<String, Object>>) cardDetails.get("issuesHistory");
				if (issuesHistory != null) {
					tableIssuesHistory.getItems().clear();
					for (Map<String, Object> issue : issuesHistory) {
						// Convert issueDate to String if it is java.sql.Date
						String issueDateStr = formatDate(issue.get("issueDate"));

						tableIssuesHistory.getItems().add(new IssueHistory((String) issue.get("issueType"),
								issueDateStr, (String) issue.get("issueDescription")));
					}
				}
			} else {
				// If card details are missing, clear all fields and tables to avoid showing
				// invalid data
				tfCardNumber.clear();
				tfUserName.clear();
				tfPhoneNumber.clear();
				tfEmail.clear();
				tableLoanHistory.getItems().clear();
				tableIssuesHistory.getItems().clear();
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

		// Validate the email format
		if (!isValidEmail(email)) {
			lblEmailmessage.setVisible(true);
			lblEmailmessage.setText("Invalid email format.");
			lblEmailmessage.setStyle("-fx-text-fill: red;");
			return;
		} else {
			lblEmailmessage.setText(""); // Clear any previous error message
		}

		// Validate the phone number format (assuming it should only contain digits and
		// be 10 characters long)
		if (!isValidPhoneNumber(phoneNumber)) {
			lblPhoneNumberMessage.setVisible(true);
			lblPhoneNumberMessage.setText("Invalid phone number format.");
			lblPhoneNumberMessage.setStyle("-fx-text-fill: red;");
			return;
		} else {
			lblPhoneNumberMessage.setText(""); // Clear any previous error message
		}

		// Create the command to send to the server
		String command = String.format("emailAndPhone %s %s %s", email, phoneNumber, cardNumber);

		// Send the command via ClientUI.chat
		try {
			ClientUI.chat.accept(command);
		} catch (Exception e) {
			lblPhoneNumberMessage.setText("Error connecting to the server.");
			lblPhoneNumberMessage.setStyle("-fx-text-fill: red;");
			e.printStackTrace();
		}
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

				lblPhoneNumberMessage.setText("");
				lblEmailmessage.setText("");
			} else {
				// Show failure message in red
				lblEmailPhonemessage.setText("Failed to update details. Please try again.");
				lblEmailPhonemessage.setStyle("-fx-text-fill: red;");
			}
		});
	}

	@FXML
	private void btnUpdateDateClicked(ActionEvent event) {
	}

//	@FXML
//	private void btnUpdateDateClicked(ActionEvent event) {
//		// Get the inserted new return date
//		String newDate = colReturnDate.getText();
//
//		// Validate the date format (assuming it should follow the format "yyyy-MM-dd")
//		if (!isValidDate(newDate)) {
//			lblUpdateDateMessage.setVisible(true);
//			lblUpdateDateMessage.setText("Invalid date format. Use yyyy-MM-dd.");
//			lblUpdateDateMessage.setStyle("-fx-text-fill: red;");
//			return;
//		} else {
//			lblUpdateDateMessage.setText(""); // Clear any previous error message
//		}
//
//		// Create the command to send to the server
//		String command = String.format("updateReturnDate %s", newDate);
//
//		// Send the command via ClientUI.chat
//		try {
//			ClientUI.chat.accept(command);
//		} catch (Exception e) {
//			lblUpdateDateMessage.setText("Error connecting to the server.");
//			lblUpdateDateMessage.setStyle("-fx-text-fill: red;");
//			e.printStackTrace();
//		}
//	}
//
//	@FXML
//	private void btnUpdateDateClicked(ActionEvent event) {
//		// Map to store the updates
//		Map<String, Map<String, String>> updates = new HashMap<>();
//
//		// Iterate through each row in the TableView
//		for (LoanHistory record : tableLoanHistory.getItems()) {
//			// Get the values from the TableView columns
//			String cardNum = record.getCardNum(); // Assume this is available from the LoanHistory object
//			String borrowDate = colBorrowDate.getCellData(record).toString(); // Convert LocalDate to String
//			String newReturnDate = colReturnDate.getCellData(record).toString(); // Convert LocalDate to String
//
//			// Check if the return date is null or empty (if required)
//			if (newReturnDate == null || newReturnDate.isEmpty()) {
//				continue; // Skip rows with no return date
//			}
//
//			// Add the data to the map
//			updates.computeIfAbsent(cardNum, k -> new HashMap<>()).put(borrowDate, newReturnDate);
//		}
//
//		// At this point, 'updates' contains all the updates you need
//		System.out.println("Updates to send to the database: " + updates);
//
//		// Send the updates to the server or database
//		try {
//			// Serialize the map to a JSON string (or any format you prefer)
//			String command = "updateReturnDates " + updates.toString();
//
//			// Send the command via ClientUI.chat
//			ClientUI.chat.accept(command);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private boolean isValidDate(String date) {
//		// Validate the date format (yyyy-MM-dd)
//		if (date == null || date.isEmpty()) {
//			return false;
//		}
//
//		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		try {
//			LocalDate.parse(date, dateFormatter);
//			return true;
//		} catch (DateTimeParseException e) {
//			return false;
//		}
//	}
//
//	public void btnUpdateReturnDateCheck(boolean isUpdated) {
//		Platform.runLater(() -> {
//			if (isUpdated) {
//				// Show success message in green
//				lblUpdateDateMessage.setVisible(true);
//				lblUpdateDateMessage.setText("Details updated successfully!");
//				lblUpdateDateMessage.setStyle("-fx-text-fill: green;");
//
//			} else {
//				// Show failure message in red
//				lblUpdateDateMessage.setText("Failed to update details. Please try again.");
//				lblUpdateDateMessage.setStyle("-fx-text-fill: red;");
//			}
//		});
//
//	}

	@FXML
	private void btnBackClicked(ActionEvent event) {

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

		btnUpdateDetails.setVisible(true);

		lblLoanHistory.setVisible(true);
		tableLoanHistory.setVisible(true);

		lblIssueHistory.setVisible(true);
		tableIssuesHistory.setVisible(true);

		btnUpdateDates.setVisible(true);
	}

	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/SubscriberCardDetails.fxml"));
			Parent root = loader.load();
			SubscriberCardDetailsController controller = loader.getController();
			if (this.chatClient != null) {
				controller.setChatClient(this.chatClient);
			}

			System.out.println("im in SubscriberCardDetailsController start  ");

			Scene scene = new Scene(root);
			primaryStage.setTitle("subscriber details");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
