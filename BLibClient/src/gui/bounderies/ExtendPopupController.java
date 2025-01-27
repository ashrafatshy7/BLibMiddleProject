package gui.bounderies;

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
/**
 * Controller for handling the extend book popup functionality.
 */
public class ExtendPopupController {

	 /** Chat client instance. */
    private ChatClient chatClient;

    /** Button to request book extension. */
    @FXML
    private Button btnRequestExtention;

    /** Label to display request status messages. */
    @FXML
    private Label lblRequest;

    /** TableView to display loans eligible for extension. */
    @FXML
    private TableView<Loan> extendTable;

    /** TableColumn for book title. */
    @FXML
    private TableColumn<Loan, String> bookTitleColumn;

    /** TableColumn for book return date. */
    @FXML
    private TableColumn<Loan, String> returnDateColumn;

    /** Subscriber card number. */
    private String cardNumber;

    /** Reference to the subscriber card details controller. */
    private SubscriberCardDetailsController subscriberCardDetailsController;

    
    /**
     * Default constructor that initializes the chat client.
     */
	public ExtendPopupController() {
		chatClient = ClientUI.chat.getClient();
	}

	
	/**
     * Sets the chat client instance.
     * @param chatClient The chat client to set.
     */
	public void setChatClient(ChatClient chatClient) {
		this.chatClient = chatClient;
		this.chatClient.setExtendPopupController(this);
	}

	/**
     * Initializes UI components and binds table columns.
     */
	@FXML
	private void initialize() {
		lblRequest.setVisible(false);
		// Initially disable the button
		btnRequestExtention.setDisable(true); // Disable the button

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
	
	/**
     * Sets the subscriber card details controller.
     * @param subscriberCardDetailsController The controller to set.
     */
	public void setSubscriberCardDetailsController(SubscriberCardDetailsController subscriberCardDetailsController) {
		this.subscriberCardDetailsController = subscriberCardDetailsController;
	}


	 /**
     * Sets the subscriber card number.
     * @param cardNum The card number to set.
     */
	public void setCardNumber(String cardNum) {
		cardNumber = cardNum;
	}

	/**
     * Sends a request to fill the table with loan extension requests.
     */
	public void tableFillRequest() {
		// Disable UI interactions and show loading message
		Platform.runLater(() -> {
			btnRequestExtention.setDisable(true); // Disable the button until data is updated
		});

		// Send request to the server asynchronously
		Message sendToServer = new Message(MessageType.bookExtentionTable, cardNumber);
		ClientUI.chat.accept(sendToServer);
	}

	/**
     * Displays books available for extension.
     * @param booksCanExtend A map of book titles and return dates.
     */
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

	/**
     * Handles the close button click event.
     */
	@FXML
	private void onCloseClicked() {
		// Close the popup
		subscriberCardDetailsController.btnSearchClicked(null);
		Stage stage = (Stage) extendTable.getScene().getWindow();
		stage.close();
	}

	
	/**
     * Handles the book extension request button click event.
     * @param event The action event.
     */
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

	
	/**
     * Handles the result of the book extension request.
     * @param extensionSuccess Whether the extension was successful.
     */
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