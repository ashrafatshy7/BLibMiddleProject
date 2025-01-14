package gui.bounderies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import application.ChatClient;
import application.ClientUI;
import enteties.Loan;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import message.Message;
import message.MessageType;

public class ExtendPopupController {

	private ChatClient chatClient;

	@FXML
	private Button btnRequestExtention;

	@FXML
	private Label lblRequest;

	@FXML
	private TableView<Loan> extendTable;

	@FXML
	private TableColumn<Loan, String> bookTitleColumn;

	@FXML
	private TableColumn<Loan, String> returnDateColumn;

	private String cardNumber;
	private SubscriberCardDetailsController subscriberCardDetailsController;

	public ExtendPopupController() {
		chatClient = ClientUI.chat.getClient();
	}

	public void setChatClient(ChatClient chatClient) {
		this.chatClient = chatClient;
		this.chatClient.ExtendPopupController(this);
	}

	@FXML
	private void initialize() {
		lblRequest.setVisible(false);
		// Initially disable the button
		btnRequestExtention.setDisable(true); // Disable the button

		// tableFillRequest();

		// Add a listener to the selection model of the TableView
		extendTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			// Enable the button when a row is selected
			if (newValue != null) {
				btnRequestExtention.setDisable(false);
			} else {
				btnRequestExtention.setDisable(true);
			}
		});

		// Bind columns to Book properties
		bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
		returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

	}

	public void setSubscriberCardDetailsController(SubscriberCardDetailsController subscriberCardDetailsController) {
		this.subscriberCardDetailsController = subscriberCardDetailsController;
	}

	// Setter method to set the card number
	public void setCardNumber(String cardNum) {
		cardNumber = cardNum;
	}

	public void tableFillRequest() {
		// Disable UI interactions and show loading message
		Platform.runLater(() -> {
			btnRequestExtention.setDisable(true); // Disable the button until data is updated
		});

		// Send request to the server asynchronously
		Message sendToServer = new Message(MessageType.bookExtentionTable, cardNumber);
		ClientUI.chat.accept(sendToServer);
	}

	public void showExtentionBooks(Map<String, String> booksCanExtend) {
		Platform.runLater(() -> {
			// Clear existing items in the table
			extendTable.getItems().clear();

			// Populate the table with new data
			ObservableList<Loan> loans = FXCollections.observableArrayList();
			for (Map.Entry<String, String> entry : booksCanExtend.entrySet()) {
				System.out.println("FOR : " + entry.getKey() + " " + entry.getValue());
				loans.add(new Loan(entry.getKey(), entry.getValue()));
			}
			extendTable.setItems(loans);

			// Enable the extension request button if a row is selected
			btnRequestExtention.setDisable(extendTable.getSelectionModel().getSelectedItem() == null);
		});
	}

	@FXML
	private void onCloseClicked() {
		// Close the popup
		subscriberCardDetailsController.btnSearchClicked(null);
		Stage stage = (Stage) extendTable.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void onRequestExtensionClicked(ActionEvent event) {
		// Get the selected row (Loan object)
		Loan selectedLoan = extendTable.getSelectionModel().getSelectedItem();
		Map<String, String> updateExtensionRequestsMap = new HashMap<>();

		if (selectedLoan != null) {
			String bookTitle = selectedLoan.getBookTitle();
			String returnDate = selectedLoan.getReturnDate();

			updateExtensionRequestsMap.put("cardNum", cardNumber);
			updateExtensionRequestsMap.put("bookTitle", bookTitle);
			updateExtensionRequestsMap.put("returnDate", returnDate);
			Message sendToServer = new Message(MessageType.bookExtensionSucceeded, updateExtensionRequestsMap);
			ClientUI.chat.accept(sendToServer);

		} else {
			lblRequest.setVisible(true);
			lblRequest.setText("No book selected.");
			lblRequest.setStyle("-fx-text-fill: red;");
		}
	}

	public void bookExtensionSucceess(boolean extensionSuccess) {
		Platform.runLater(() -> {
			if (extensionSuccess) {
				lblRequest.setVisible(true);
				lblRequest.setText("Extension succeeded!");
				lblRequest.setStyle("-fx-text-fill: green;");
				System.out.println("A message sent to the Librarian");
				tableFillRequest(); // Refresh the table with updated data
			} else {
				lblRequest.setVisible(true);
				lblRequest.setText("This book is currently on order\nand cannot be extended.");
				lblRequest.setStyle("-fx-text-fill: red;");
			}
		});
	}

}
