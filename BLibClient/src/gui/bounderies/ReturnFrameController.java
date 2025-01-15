package gui.bounderies;
import java.io.IOException;
import java.util.ArrayList;

import application.ClientUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
/*import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;*/
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import message.Message;
import message.MessageType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.control.TextField;
/*import javafx.stage.Stage;
import javafx.event.ActionEvent;*/

public class ReturnFrameController {

    @FXML
    private TextField bookBarcodeField;

    @FXML
    private TextField ReadercardField;

    @FXML
    private Button Returnbutton;

    @FXML
    private Label EmptyErrorLabel;
    
    @FXML
    private Label InvalidErrorLabel;
    
    @FXML
    private Label  BookbarcodeLabel;
    
    @FXML
    private Label  ReadercardLabel;
    
    @FXML
    private Label  ReturnLabel;
    
    @FXML
    private Label SuccessMessageLabel;
    
    @FXML 
    private Label returnSuccess;
    
    @FXML
    private Label returnUnsuccess;
    
    @FXML
    private void initialize() {
    	
    	bookBarcodeField.textProperty().addListener((observable, oldValue, newValue) -> {
    		bookBarcodeField.getStyleClass().remove("invalid-border");
    		EmptyErrorLabel.setVisible(false);
		    InvalidErrorLabel.setVisible(false);
            SuccessMessageLabel.setVisible(false);
        });
    	
    	ReadercardField.textProperty().addListener((observable, oldValue, newValue) -> {
    		ReadercardField.getStyleClass().remove("invalid-border"); 
    		EmptyErrorLabel.setVisible(false);
		    InvalidErrorLabel.setVisible(false);
            SuccessMessageLabel.setVisible(false);
    		
        });

    }
    
   
    public void handleReturnButtonAction(ActionEvent event) throws Exception {
    	
    	String bookBarcode = bookBarcodeField.getText();
        String readerCard = ReadercardField.getText(); 

        if (bookBarcode.isEmpty()) {
            EmptyErrorLabel.setVisible(true);
            InvalidErrorLabel.setVisible(false);
            SuccessMessageLabel.setVisible(false); 
            if (!bookBarcodeField.getStyleClass().contains("invalid-border")) {
                bookBarcodeField.getStyleClass().add("invalid-border");
            }
        }
        
         if (readerCard.isEmpty()) {
            EmptyErrorLabel.setVisible(true);
            InvalidErrorLabel.setVisible(false);
            SuccessMessageLabel.setVisible(false); 
            if (!ReadercardField.getStyleClass().contains("invalid-border")) {
                ReadercardField.getStyleClass().add("invalid-border");
            }
        }
        
        else if (!isValidBookBarcode(bookBarcode)&&!bookBarcode.isEmpty()) {
        	EmptyErrorLabel.setVisible(false);
        	InvalidErrorLabel.setVisible(true);
            SuccessMessageLabel.setVisible(false);  
            if (!bookBarcodeField.getStyleClass().contains("invalid-border")) {
                bookBarcodeField.getStyleClass().add("invalid-border");
            }
        }
        
      
        else  if (!isValidReaderCard(readerCard)&&!readerCard.isEmpty()) {
            InvalidErrorLabel.setVisible(true);
            EmptyErrorLabel.setVisible(false);
            SuccessMessageLabel.setVisible(false); 
            if (!ReadercardField.getStyleClass().contains("invalid-border")) {
                ReadercardField.getStyleClass().add("invalid-border");
            }
        }
        
       
         else {
            
            bookBarcodeField.getStyleClass().remove("invalid-border");
            ReadercardField.getStyleClass().remove("invalid-border");
            EmptyErrorLabel.setVisible(false);
            InvalidErrorLabel.setVisible(false);
            SuccessMessageLabel.setVisible(true); 
        }
         
         ArrayList<String> returnBook = new ArrayList<>();
         returnBook.add(bookBarcode);
         returnBook.add(readerCard);
         Message sendToServer = new Message(MessageType.Return, returnBook);
         ClientUI.chat.accept(sendToServer); 
    }
    
     
    
    private boolean isValidBookBarcode(String bookBarcode)
    { 
    	return bookBarcode.matches("^\\d{6}$");
    }
    
    private boolean isValidReaderCard(String readerCard)
    { 
    	return readerCard.matches("^\\d{9}$");
    }




  	    
       /* if (!isBookValid(bookBarcode)) 
        {
            showAlert(AlertType.ERROR, "Error", "Book not found. Please check the barcode.");
            return;//
        }

        if (!isReaderValid(readerCard)) 
        {
            showAlert(AlertType.ERROR, "Error", "Reader card not found. Please check the card number.");
            return;
        }
        processReturn(bookBarcode, readerCard);
        showAlert(AlertType.INFORMATION, "Success", "Book returned successfully!");
        }*/
    
   /* private boolean isBookValid(String bookBarcode) {
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
    }*/
    
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


	public void showMessage(boolean isReturnSuccessful) {
		// TODO Auto-generated method stub
		if (isReturnSuccessful) {
			returnSuccess.setVisible(true);
			returnUnsuccess.setVisible(false);
		} else {
			returnSuccess.setVisible(false);
			returnUnsuccess.setVisible(true);
		}
	}
}
