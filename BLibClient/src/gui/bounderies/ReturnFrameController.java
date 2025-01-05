package gui.bounderies;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ReturnFrameController {

    @FXML
    private TextField bookBarcodeField;

    @FXML
    private TextField ReadercardField;

    @FXML
    private Button Returnbutton;

    @FXML
    private Label errorLabel;
    
    @FXML
    private Label  BookbarcodeLabel;
    
    @FXML
    private Label  ReadercardLabel;
    
    @FXML
    private Label  ReturnLabel;
    
    @FXML
    public void handleReturnButtonAction(ActionEvent event) throws Exception 
    {
    
        String bookBarcode = bookBarcodeField.getText();
        String readerCard = ReadercardField.getText(); 
        
        if (bookBarcode.isEmpty() || readerCard.isEmpty()) 	
        {
            showAlert(AlertType.WARNING, "Missing Information", "Please enter both book barcode and reader card.");
            return;
	    }
        if (!isBookValid(bookBarcode)) 
        {
            showAlert(AlertType.ERROR, "Error", "Book not found. Please check the barcode.");
            return;
        }

        if (!isReaderValid(readerCard)) 
        {
            showAlert(AlertType.ERROR, "Error", "Reader card not found. Please check the card number.");
            return;
        }
        processReturn(bookBarcode, readerCard);
        showAlert(AlertType.INFORMATION, "Success", "Book returned successfully!");
        }
    
    private boolean isBookValid(String bookBarcode) {
        // צריל להוסיף  קוד לחיפוש ברקוד ספר בדרטא 
        return "123456".equals(bookBarcode); // צריך להחליף לברקוד במסד הנתונים 
    }
    
    private boolean isReaderValid(String readerCard) {
        // צריך קוד לחיפוש כרטיס קורא במערכת
        return "987654".equals(readerCard); //סתם בדיקה
    }
    
    private void processReturn(String bookBarcode, String readerCard) 
    {
        // צריך לעדכן את המידע במסד הנתונים או לשמור את ההחזרה
        System.out.println("Book with barcode " + bookBarcode + " has been returned by reader with card " + readerCard);
    }
    
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
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
}

