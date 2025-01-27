package gui.bounderies;

import java.io.IOException;
import java.util.ArrayList;

import application.ClientUI;
import enteties.Issue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import message.Message;
import message.MessageType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

/**
 * Controller for handling book returns.
 */
public class ReturnFrameController {

	/** TextField for entering the book barcode. */
    @FXML
    private TextField bookBarcodeField;

    /** TextField for entering the reader's card number. */
    @FXML
    private TextField readercardField;

    /** Button to initiate the return process. */
    @FXML
    private Button Returnbutton;

    /** Label to display book barcode issues. */
    @FXML
    private Label barcodeIssue;

    /** Label to display reader card issues. */
    @FXML
    private Label readerCardIssue;

    /** ChoiceBox for selecting return issues (e.g., lost, no issue). */
    @FXML
    private ChoiceBox<String> issueChoice;

    
    /**
     * Initializes the UI components and sets default values.
     */
	@FXML
	private void initialize() {

		barcodeIssue.setVisible(false);
		readerCardIssue.setVisible(false);

		issueChoice.getItems().addAll("No Issue", "Lost");
		issueChoice.setValue("No Issue");

		bookBarcodeField.textProperty().addListener((observable, oldValue, newValue) -> {
			bookBarcodeField.getStyleClass().remove("invalid-border");
			barcodeIssue.setVisible(false);
		});

		readercardField.textProperty().addListener((observable, oldValue, newValue) -> {
			readercardField.getStyleClass().remove("invalid-border");
			readerCardIssue.setVisible(false);
		});

	}

	
	/**
     * Handles the return button action.
     * @param event The action event.
     * @throws Exception If an error occurs during the return process.
     */
	@FXML
	public void handleReturnButtonAction(ActionEvent event) throws Exception {

		String bookBarcode = bookBarcodeField.getText();
		String readerCard = readercardField.getText();
		boolean valid = true;

		if (!bookBarcode.matches("^\\d{6}$")) {
			bookBarcodeField.getStyleClass().add("invalid-border");
			barcodeIssue.setVisible(true);
			valid = false;
		}
		if (!readerCard.matches("^\\d{9}$")) {
			readercardField.getStyleClass().add("invalid-border");
			readerCardIssue.setVisible(true);
			valid = false;
		}

		if (!valid)
			return;

		ArrayList<Object> returnBook = new ArrayList<>();
		returnBook.add(bookBarcode);
		returnBook.add(readerCard);
		returnBook.add(new Issue(issueChoice.getValue()));
		Message sendToServer = new Message(MessageType.returnBook, returnBook);
		ClientUI.chat.accept(sendToServer);
	}

	
	/**
     * Starts the Return Frame.
     * @param primaryStage The primary stage.
     * @throws Exception If an error occurs while loading the frame.
     */
	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/ReturnFrame.fxml"));
			Parent root = loader.load();
			ReturnFrameController controller = loader.getController();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/gui/bounderies/ReturnFrame.css").toExternalForm());
			primaryStage.setTitle("Return Book");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	 /**
     * Handles the action to return to the home screen.
     * @param event The action event.
     */
	@FXML
	public void goBack(ActionEvent event) {
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

}