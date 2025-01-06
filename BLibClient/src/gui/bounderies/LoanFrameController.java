package gui.bounderies;

import java.io.IOException;
import java.time.LocalDate;

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

public class LoanFrameController {

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

	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/LoanFrame.fxml"));
			Parent root = loader.load();

			LoanFrameController controller = loader.getController();

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
		checkBarcode(barcodeTextField.getText());
		checkReaderCard(readerCardTextField.getText());
		checkReturnDate(returnDatePicker.getValue());
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

        // If the user must pick a date that is on or after "current date"
        if (returnDate.isBefore(currectDatePicker.getValue())) {
            returnDateError.setVisible(true);
            returnDatePicker.getStyleClass().add("invalid-border");
            return false;
        }

        // All good
        return true;
    }

}
