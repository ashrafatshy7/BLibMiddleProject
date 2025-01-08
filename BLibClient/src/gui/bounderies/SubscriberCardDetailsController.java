package gui.bounderies;

import java.io.IOException;
import java.time.LocalDate;
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

	private List<LoanHistory> loanHistoryList = new ArrayList<>();
	private List<IssueHistory> issueHistoryList = new ArrayList<>();

	@FXML
	private void initialize() {
		// Hide all components
		hideComponents();

//		colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
//		colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
//		colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
//
//		colIssueType.setCellValueFactory(new PropertyValueFactory<>("issueType"));
//		colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
//		colIssueDescription.setCellValueFactory(new PropertyValueFactory<>("issueDescription"));

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
			} else {
				// If card details are missing, clear the fields to avoid showing invalid data
				tfCardNumber.clear();
				tfUserName.clear();
				tfPhoneNumber.clear();
				tfEmail.clear();
			}
		});
	}

	@FXML
	private void btnUpdateDetailsClicked(ActionEvent event) {

	}

	@FXML
	private void btnUpdateDateClicked(ActionEvent event) {

	}

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
