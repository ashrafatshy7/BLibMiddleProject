package gui.bounderies;

import java.io.IOException;

import application.ChatClient;
import application.ClientUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SubscriberCardDetailsController {

	@FXML
	private Button btnBack;

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
	private TableColumn<?, ?> colBookTitle;

	@FXML
	private TableColumn<?, ?> colBorrowDate;

	@FXML
	private TableColumn<?, ?> colReturnDate;

	@FXML
	private TableView<?> tableLoanHistory;

	@FXML
	private TableColumn<?, ?> colIssueType;

	@FXML
	private TableColumn<?, ?> colIssueDate;

	@FXML
	private TableColumn<?, ?> colIssueStatus;

	@FXML
	private TableView<?> tableIssuesHistory;

	@FXML
	private Label lblInvalidCardNumber;

	@FXML
	private void btnSearchClicked(ActionEvent event) {
//        String cardNumber = tfInsertCardNumber.getText();
//
//        if (cardNumber == null || cardNumber.isEmpty() || !isValidCardNumber(cardNumber)) {
//            lblInvalidCardNumber.setVisible(true);
//        } else {
//            lblInvalidCardNumber.setVisible(false);
//            // Logic to fetch and populate subscriber details
//            populateSubscriberDetails(cardNumber);
//        }
	}

	@FXML
	private void btnUpdateDetailsClicked(ActionEvent event) {
//        String cardNumber = tfCardNumber.getText();
//        String userName = tfUserName.getText();
//        String phoneNumber = tfPhoneNumber.getText();
//        String email = tfEmail.getText();
//
//        // Logic to update the subscriber details
//        updateSubscriberDetails(cardNumber, userName, phoneNumber, email);
	}

	@FXML
	private void btnUpdateDateClicked(ActionEvent event) {
//        // Logic to update loan or issue details
//        updateLoanOrIssueDetails();
	}

	@FXML
	private void btnBackClicked(ActionEvent event) {
		// Logic to navigate back to the previous screen
		// navigateBack();
	}

	private boolean isValidCardNumber(String cardNumber) {
		// Logic to validate the card number
		// return cardNumber.matches("\\d+"); // Example: Only digits are allowed
		return false;
	}

	private void populateSubscriberDetails(String cardNumber) {
		// Logic to fetch subscriber details from the database and populate the fields
		// Example: tfUserName.setText(fetchedUserName);
	}

	private void updateSubscriberDetails(String cardNumber, String userName, String phoneNumber, String email) {
		// Logic to update subscriber details in the database
	}

	private void updateLoanOrIssueDetails() {
		// Logic to update loan or issue details in the database
	}

	private void navigateBack() {
		// Logic to handle navigation...........
	}

//	public void start(Stage primaryStage) throws Exception { 
//
//		Parent root = FXMLLoader.load(getClass().getResource("/gui/bounderies/SubscriberCardDetails.fxml"));
//		Scene scene = new Scene(root);
//		primaryStage.setTitle("Subscriber Card Details");
//		primaryStage.setScene(scene);
//		primaryStage.show();
//
//	}
}
