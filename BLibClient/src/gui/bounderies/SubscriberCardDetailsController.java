package gui.bounderies;

import java.io.IOException;
import javafx.util.converter.LocalDateStringConverter;
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

	public static String type = "subscriber";

	@FXML
	private void initialize() {
		// Hide all components
		hideComponents();

		colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
		colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
		colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

		// Enable editing for Return Date column
		tableLoanHistory.setEditable(true);
		colReturnDate.setEditable(true);

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

			if (type.equals("subscriber")) {
				suscriberComponents();
			} else {
				librarianComponents();
			}

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
			tfEmail.getStyleClass().add("text-field-invalid");
			lblEmailmessage.setStyle("-fx-text-fill: red;");
			tfInsertCardNumber.getStyleClass().add("text-field-invalid");
			return;
		} else {
			lblEmailmessage.setText(""); // Clear any previous error message
		}

		// Validate the phone number format (assuming it should only contain digits and
		// be 10 characters long)
		if (!isValidPhoneNumber(phoneNumber)) {
			lblPhoneNumberMessage.setVisible(true);
			lblPhoneNumberMessage.setText("Invalid phone number format.");
			tfPhoneNumber.getStyleClass().add("text-field-invalid");
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
				tfPhoneNumber.getStyleClass().remove("text-field-invalid");
				tfEmail.getStyleClass().remove("text-field-invalid");

				lblPhoneNumberMessage.setText("");
				lblEmailmessage.setText("");
			} else {
				// Show failure message in red
				lblEmailPhonemessage.setText("Failed to update details. Please try again.");
				lblEmailPhonemessage.setStyle("-fx-text-fill: red;");
			}
		});
	}

//	@FXML
//	private void btnUpdateDateClicked(ActionEvent event) {
//		// Get the inserted new return date
//		String newDate = colReturnDate.getText();
//		String cardNum = tfCardNumber.getText();
//		String borrowDate = BorrowDate.
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
//		String command = String.format("updateReturnDate %s %s %s", newDate);
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

	@FXML
	private void btnUpdateDateClicked(ActionEvent event) {
//	    // Get selected rows (from the table view)
//	    ObservableList<TableViewData> selectedRows = tableView.getSelectionModel().getSelectedItems();
//	    
//	    // Get the new return date from the GUI (for example, a text field)
//	    String newReturnDate = newReturnDateTextField.getText(); // assuming you have a TextField for input
//	    
//	    // Check if a new return date is entered
//	    if (newReturnDate.isEmpty()) {
//	        // Optionally, show an alert if no return date is entered
//	        showAlert("Error", "Please enter a new return date.");
//	        return;
//	    }
//	    
//	    // Iterate through each selected row and update the return dates
//	    for (TableViewData row : selectedRows) {
//	        String cardNumber = row.getCardNumber();  // Get the card number from the row
//	        String borrowDate = row.getBorrowDate();  // Get the borrow date from the row
//	        
//	        // Call the method to update the return date for this row
//	        updateReturnDate(cardNumber, borrowDate, newReturnDate);
//	    }
//	    
//	    // Optionally, show a success message
//	    System.out.println("Updated return dates for selected rows.");
//	}
//
//	// Method to update a single return date
//	private void updateReturnDate(String cardNumber, String borrowDate, String newReturnDate) {
//	    // Construct the update command or query
//	    String command = String.format("updateReturnDate %s %s %s", cardNumber, borrowDate, newReturnDate);
//	    
//	    // Output the final command (you can send it to the server or process it here)
//	    System.out.println(command);
//	    
//	    // For example, send the command to the server or process it
//	    // sendCommandToServer(command);
//	}
//
//	// Method to show an alert (optional)
//	private void showAlert(String title, String message) {
//	    Alert alert = new Alert(Alert.AlertType.ERROR);
//	    alert.setTitle(title);
//	    alert.setHeaderText(null);
//	    alert.setContentText(message);
//	    alert.showAndWait();
	}

	private boolean isValidDate(String date) {
		// Validate the date format (yyyy-MM-dd)
		if (date == null || date.isEmpty()) {
			return false;
		}

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		try {
			LocalDate.parse(date, dateFormatter);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}
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

//	@FXML
//	private void btnUpdateDateClicked(ActionEvent event) {
//	    // Get the selected loan history item
//	    LoanHistory selectedLoan = tableLoanHistory.getSelectionModel().getSelectedItem();
//
//	    if (selectedLoan == null) {
//	        lblUpdateDateMessage.setVisible(true);
//	        lblUpdateDateMessage.setText("Please select a loan record to update the return date.");
//	        lblUpdateDateMessage.setStyle("-fx-text-fill: red;");
//	        return;
//	    }
//
//	    // Get the new return date entered by the user
//	    String newDate = selectedLoan.getReturnDate();
//
//	    // Validate the new return date format
//	    if (!isValidDate(newDate)) {
//	        lblUpdateDateMessage.setVisible(true);
//	        lblUpdateDateMessage.setText("Invalid date format. Use yyyy-MM-dd.");
//	        lblUpdateDateMessage.setStyle("-fx-text-fill: red;");
//	        return;
//	    } else {
//	        lblUpdateDateMessage.setText(""); // Clear any previous error message
//	    }
//
//	    // Extract other required details
//	    String cardNum = tfCardNumber.getText();
//	    String borrowDate = selectedLoan.getBorrowDate();
//
//	    // Create the command to send to the server
//	    String command = String.format("updateReturnDate %s %s %s %s", cardNum, borrowDate, newDate, selectedLoan.getBookTitle());
//
//	    // Send the command via ClientUI.chat
//	    try {
//	        ClientUI.chat.accept(command);
//	    } catch (Exception e) {
//	        lblUpdateDateMessage.setText("Error connecting to the server.");
//	        lblUpdateDateMessage.setStyle("-fx-text-fill: red;");
//	        e.printStackTrace();
//	    }
//	}

//	@FXML
//	private void btnUpdateDateClicked(ActionEvent event) {
//	    // Step 1: Get the card number from the TextField (tfCardNum)
//	    String cardNumber = tfCardNum.getText(); // Replace with the actual TextField name
//	    
//	    // Step 2: Get selected rows from the TableView (representing borrow and return dates)
//	    ObservableList<TableViewData> selectedRows = tableView.getSelectionModel().getSelectedItems();
//	    
//	    // Step 3: Get the new return date from the GUI (assuming you have a TextField for this input)
//	    String newReturnDate = newReturnDateTextField.getText(); // Change this to your TextField name
//	    
//	    // Step 4: Validate the new return date input (check if it's not empty)
//	    if (newReturnDate.isEmpty()) {
//	        // If the new return date is empty, show an alert and exit
//	        Alert alert = new Alert(Alert.AlertType.ERROR);
//	        alert.setTitle("Error");
//	        alert.setHeaderText(null);
//	        alert.setContentText("Please enter a new return date.");
//	        alert.showAndWait();
//	        return;
//	    }
//	    
//	    // Step 5: Loop through the selected rows and update the return date
//	    for (TableViewData row : selectedRows) {
//	        // Get borrow date from each selected row (we're not using card number from here)
//	        String borrowDate = row.getBorrowDate(); // Assuming getBorrowDate() returns a String
//
//	        // Step 6: Update the return date for this row (update logic can vary, here we simply print the result)
//	        updateReturnDate(cardNumber, borrowDate, newReturnDate);
//	    }
//
//	    // Step 7: Optionally, show a success message or feedback to the user
//	    System.out.println("Return dates updated for the selected rows.");
//	}
//
//	// Method to update the return date (this can involve database interaction)
//	private void updateReturnDate(String cardNumber, String borrowDate, String newReturnDate) {
//	    // Construct the update command or query to update the return date for the card number and borrow date
//	    String command = String.format("updateReturnDate %s %s %s", cardNumber, borrowDate, newReturnDate);
//	    
//	    // Output the command (this could be sent to a server or database)
//	    System.out.println("Updating return date with command: " + command);
//
//	    // Add database interaction here if needed, for example:
//	    // sendCommandToDatabase(command); // Implement this function as needed
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

		lblUpdateDateMessage.setVisible(false);
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

	private void suscriberComponents() {
		btnUpdateDates.setVisible(false);
		btnUpdateDetails.setVisible(true);
	}

	private void librarianComponents() {
		btnUpdateDetails.setVisible(false);
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
