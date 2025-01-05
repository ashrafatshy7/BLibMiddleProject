package gui.bounderies;

import java.io.IOException;

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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
/*import javafx.stage.Stage;
import javafx.event.ActionEvent;*/
public class LoginFrameController {
	
	    @FXML
	    private Label LoginLabel;
	    
	    @FXML
	    private TextField emailField;

	    @FXML
	    private PasswordField passwordField;

	    @FXML
	    private Button loginButton;

	    @FXML
	    private Button forgotPasswordButton;

	    @FXML
	    private Label errorLabel;
	    
	    @FXML
	    private Label EmailLabel;
	    
	    @FXML
	    private Label PasswordLabel;
	    
	    public void handleLoginButtonAction(ActionEvent event) throws Exception {
	    	
	     String Email = emailField.getText();
 		 String password = passwordField.getText();

        if (Email.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.WARNING, "Login Failed", "Please enter both username and password.");
            return;
        }

        // Check credentials (replace this with actual database validation)\\\ צריך חיבור לדאטא 
        if ("admin".equals(Email) && "password".equals(password)) {
            showAlert(AlertType.INFORMATION, "Login Successful", "Welcome to BLib!");
        } else {
            showAlert(AlertType.ERROR, "Login Failed", "Invalid username or password. Try again.");
        }
    }
	    
	    private void showAlert(AlertType alertType, String title, String message) {
	        Alert alert = new Alert(alertType);
	        alert.setTitle(title);
	        alert.setHeaderText(null);
	        alert.setContentText(message);
	        alert.showAndWait();
	    }
	    
	    @FXML
	    private void handleForgotPasswordButtonAction() {
	        String email = emailField.getText();
	        
	        if (email.isEmpty()) {
	            showAlert(AlertType.WARNING, "Error", "Please enter your email address.");
	            return;
	        }

	        /*boolean success = DatabaseConnection.sendPasswordResetEmail(email);*/
	        boolean success=true;
	        if (success) {
	            showAlert(AlertType.INFORMATION, "Password Reset", 
	                      "A password reset link has been sent to your email.");
	        } else {
	            showAlert(AlertType.ERROR, "Error", "Email not found. Please check and try again.");
	        }
	    }
	    public void start(Stage primaryStage) throws Exception {
	        try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/LoginFrame.fxml"));
	            Parent root = loader.load();
	            LoginFrameController controller = loader.getController();
	            Scene scene = new Scene(root);
	            scene.getStylesheets().add(getClass().getResource("/gui/bounderies/LoginFrame.css").toExternalForm());
	            primaryStage.setTitle("Client");
	            primaryStage.setScene(scene);
	            primaryStage.show();
	           
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
}
