package gui.bounderies;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import application.ChatClient;
import application.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

public class LoanFrameController {
	
	private ChatClient chatClient;


	@FXML
	private TextField barcodeTextField;

	@FXML
	private TextField readerCardTextField;

	@FXML
	private DatePicker currectDatePicker;

	@FXML
	private DatePicker returnDatePicker;

	@FXML
	private Button setLoanBtn;

	@FXML
	private Label barcodeError;

	@FXML
	private Label readerCardError;

	@FXML
	private Label returnDateError;

	@FXML
	private void initialize() {
		
		barcodeTextField.setDisable(true);
		currectDatePicker.setDisable(true);
		returnDatePicker.setDisable(true);
		setLoanBtn.setDisable(true);
		
		
		
		currectDatePicker.setValue(LocalDate.now());
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
	
	@FXML
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
		if(!checkBarcode(barcodeTextField.getText())) valid = false;
		if(!checkReaderCard(readerCardTextField.getText())) valid = false;
		if(!checkReturnDate(returnDatePicker.getValue())) valid = false;
		
		
		if(!valid) return;
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate currentSelectedDate = currectDatePicker.getValue();
		String currentformattedDate = currentSelectedDate.format(formatter);
		LocalDate returnSelectedDate = returnDatePicker.getValue();
		String returntformattedDate = returnSelectedDate.format(formatter);
		new Message(MessageType.loan, new Loan(readerCardTextField.getText(), barcodeTextField.getText(), currentformattedDate, returntformattedDate));
	
	}
	
	
	@FXML
	public void checkStatus(ActionEvent event) throws Exception {
		String readerCard = readerCardTextField.getText();
		
		boolean valid = true;
        if(!readerCard.matches("^\\d{9}$")) {
        	readerCardTextField.getStyleClass().add("invalid-border");
        	readerCardError.setVisible(true);
        	valid = false;
        }
        
        if(!valid) return;
        
        Message sendToServer = new Message(MessageType.checkStatus, readerCard);
        ClientUI.chat.accept(sendToServer);
	
	}
	
	
	public void setActive() {
		barcodeTextField.setDisable(false);
		currectDatePicker.setDisable(false);
		returnDatePicker.setDisable(false);
		setLoanBtn.setDisable(false);
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

        if (returnDate.isBefore(currectDatePicker.getValue())) {
            returnDateError.setVisible(true);
            returnDatePicker.getStyleClass().add("invalid-border");
            return false;
        }

        return true;
    }

}
