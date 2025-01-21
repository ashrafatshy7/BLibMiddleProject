package gui.bounderies;
import java.io.IOException;
import java.util.ArrayList;

import application.ClientUI;
import enteties.Issue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import message.Message;
import message.MessageType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class ReturnFrameController {

    @FXML
    private TextField bookBarcodeField;

    @FXML
    private TextField readercardField;

    @FXML
    private Button Returnbutton;
       
    
    @FXML 
    private Label barcodeIssue;
    
    @FXML
    private Label readerCardIssue;
    
    @FXML 
    private ChoiceBox<String> issueChoice;
    
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
    
    @FXML
    public void handleReturnButtonAction(ActionEvent event) throws Exception {
    	
    	String bookBarcode = bookBarcodeField.getText();
        String readerCard = readercardField.getText(); 
        boolean valid = true;
        
        if(!bookBarcode.matches("^\\d{6}$")) {
        	bookBarcodeField.getStyleClass().add("invalid-border");
        	barcodeIssue.setVisible(true);
        	valid = false;
        }
        if(!readerCard.matches("^\\d{9}$")) {
        	readercardField.getStyleClass().add("invalid-border");
        	readerCardIssue.setVisible(true);
        	valid = false;
        }
        
        if(!valid) return;
        
         
         ArrayList<Object> returnBook = new ArrayList<>();
         returnBook.add(bookBarcode);
         returnBook.add(readerCard);
         returnBook.add(new Issue(issueChoice.getValue()));
         Message sendToServer = new Message(MessageType.returnBook, returnBook);
         ClientUI.chat.accept(sendToServer); 
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
    
    
    
    
    @FXML
    public void goBack(ActionEvent event) throws Exception{
    	
    }
    
    
    
    
    
    
    
}