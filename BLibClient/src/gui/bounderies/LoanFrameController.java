package gui.bounderies;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import application.ChatClient;
import application.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import message.Message;
import message.MessageType;
import enteties.Loan;
import enteties.Subscriber;

public class LoanFrameController {

	private ChatClient chatClient;

	@FXML
	private TextField barcodeTextField;

	@FXML
	private TextField readerCardTextField;

	@FXML
	private DatePicker currentDatePicker;

	@FXML
	private DatePicker returnDatePicker;

	@FXML
	private Button setLoanBtn;

	@FXML
	private Button checkStatusBtn;
	
	@FXML
	private Button btnBackClicked;

	@FXML
	private Label barcodeError;

	@FXML
	private Label readerCardError;

	@FXML
	private Label returnDateError;

	@FXML
	private void initialize() {

		barcodeTextField.setDisable(true);
		currentDatePicker.setDisable(true);
		returnDatePicker.setDisable(true);

		setLoanBtn.setDisable(true);

		currentDatePicker.setValue(LocalDate.now());
		currentDatePicker.setEditable(false);
		returnDatePicker.setValue(LocalDate.now().plusDays(14));

		barcodeError.setVisible(false);
		readerCardError.setVisible(false);
		returnDateError.setVisible(false);

		barcodeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			barcodeTextField.getStyleClass().remove("invalid-border");
			barcodeError.setVisible(false);
		});

		readerCardTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			readerCardTextField.getStyleClass().remove("invalid-border");
			readerCardError.setVisible(false);
		});

		returnDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			returnDatePicker.getStyleClass().remove("invalid-border");
			returnDateError.setVisible(false);
		});

	}

	public LoanFrameController() {
		chatClient = ClientUI.chat.getClient();
	}

	public void setChatClient(ChatClient chatClient) {
		this.chatClient = chatClient;
		this.chatClient.setLoanFrameController(this);
	}

	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/LoanFrame.fxml"));
			Parent root = loader.load();

			LoanFrameController controller = loader.getController();

			if (this.chatClient != null) {
				controller.setChatClient(this.chatClient);
			} else {
				// Handle the case where chatClient is null
				System.err.println("ChatClient is not initialized.");
			}

			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/gui/bounderies/LoanFrame.css").toExternalForm());
			primaryStage.setTitle("Details");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void submitLoan(ActionEvent event) throws Exception {
		boolean valid = true;
		if (!checkBarcode(barcodeTextField.getText()))
			valid = false;
		if (!checkReaderCard(readerCardTextField.getText()))
			valid = false;
		if (!checkReturnDate(returnDatePicker.getValue()))
			valid = false;

		if (!valid)
			return;

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate currentSelectedDate = currentDatePicker.getValue();
		String currentformattedDate = currentSelectedDate.format(formatter);
		LocalDate returnSelectedDate = returnDatePicker.getValue();
		String returntformattedDate = returnSelectedDate.format(formatter);

		ArrayList<Object> loanDetails = new ArrayList<>();
		loanDetails.add(new Loan(barcodeTextField.getText(), currentformattedDate, returntformattedDate, true));
		loanDetails.add(new Subscriber(readerCardTextField.getText()));
		ClientUI.chat.accept(new Message(MessageType.loan, loanDetails));

	}

	@FXML
	public void checkStatus(ActionEvent event) throws Exception {
		if (checkStatusBtn.getText().equals("check status")) {
			String readerCard = readerCardTextField.getText();
			boolean valid = true;
			if (!readerCard.matches("^\\d{9}$")) {
				readerCardTextField.getStyleClass().add("invalid-border");
				readerCardError.setVisible(true);
				valid = false;
			}

			if (!valid)
				return;

			Message sendToServer = new Message(MessageType.checkStatus, new Subscriber(readerCard));
			ClientUI.chat.accept(sendToServer);
		} else if (checkStatusBtn.getText().equals("change subscriber")) {
			barcodeTextField.setDisable(true);
			returnDatePicker.setDisable(true);
			currentDatePicker.setDisable(true);
			setLoanBtn.setDisable(true);
			checkStatusBtn.setText("check status");
			readerCardTextField.setDisable(false);
		}

	}

	public void setActive() {
		javafx.application.Platform.runLater(() -> {
			barcodeTextField.setDisable(false);
			returnDatePicker.setDisable(false);
			setLoanBtn.setDisable(false);
			currentDatePicker.setDisable(false);
			checkStatusBtn.setText("change subscriber");
		});
	}

	private boolean checkBarcode(String barcode) {
		if (barcode != null && barcode.length() == 6) {

			return true;
		}
		barcodeError.setVisible(true);
		barcodeTextField.getStyleClass().add("invalid-border");

		return false;
	}

	private boolean checkReaderCard(String readerCard) {
		if (readerCard != null && readerCard.length() == 9) {

			return true;
		}
		readerCardError.setVisible(true);
		readerCardTextField.getStyleClass().add("invalid-border");

		return false;
	}

	private boolean checkReturnDate(LocalDate returnDate) {
		if (returnDate == null || returnDatePicker.getEditor().getText().trim().isEmpty()) {
			returnDateError.setVisible(true);
			returnDatePicker.getStyleClass().add("invalid-border");
			return false;
		}

		if (returnDate.isBefore(currentDatePicker.getValue())) {
			returnDateError.setVisible(true);
			returnDatePicker.getStyleClass().add("invalid-border");
			return false;
		}

		return true;
	}

	@FXML
	public void btnBackClicked(ActionEvent event) {
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
