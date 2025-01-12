package gui.bounderies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import application.ChatClient;
import application.ClientUI;
import enteties.Loan;
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

	@FXML
	private void initialize() {
		lblRequest.setText("");
		// Initially disable the button
		btnRequestExtention.setDisable(true); // Disable the button

		tableFillRequest();

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
		bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
		returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

	}

	public ExtendPopupController() {
		chatClient = ClientUI.chat.getClient();
	}

	public void setChatClient(ChatClient chatClient) {
		this.chatClient = chatClient;
		this.chatClient.ExtendPopupController(this);
	}

	// Setter method to set the card number
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public void tableFillRequest() {
		Message sendToServer = new Message(MessageType.bookExtentionTable, cardNumber);
		ClientUI.chat.accept(sendToServer);
	}

	public void showExtentionBooks(ArrayList<Loan> booksCanExtend) {
		// Convert ArrayList<Loan> to ObservableList
		ObservableList<Loan> observableBooks = FXCollections.observableArrayList(booksCanExtend);

		// Set the ObservableList to the TableView (this will automatically fill the
		// rows)
		extendTable.setItems(observableBooks);

		// Set the cell value factories for each column to match the Loan properties
		bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
		returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
	}

	@FXML
	private void onCloseClicked() {
		// Close the popup
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

			// You can now use these values as needed, for example, display them or pass
			// them to another method.
			System.out.println("Selected Book: " + bookTitle + ", Return Date: " + returnDate);
			lblRequest.setText("Extension Succeeded");
			lblRequest.setStyle("-fx-text-fill: green;");

			String bookTitle2 = selectedLoan.getBookTitle();
			String returnDate2 = selectedLoan.getReturnDate();

			updateExtensionRequestsMap.put("cardNum", cardNumber);
			updateExtensionRequestsMap.put("bookTitle", bookTitle2);
			updateExtensionRequestsMap.put("returnDate", returnDate2);

			Message sendToServer = new Message(MessageType.bookExtensionSucceeded, updateExtensionRequestsMap);
			ClientUI.chat.accept(sendToServer);

		} else {
			lblRequest.setText("No book selected.");
			lblRequest.setStyle("-fx-text-fill: red;");
		}
	}

}
