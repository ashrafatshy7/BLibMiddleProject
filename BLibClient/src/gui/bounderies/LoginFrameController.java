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
	    private Label EmailAddresserrorLabel;
	    
	    @FXML
	    private Label EmailLabel;
	    
	    @FXML
	    private Label PasswordLabel;
	    
	    @FXML
	    private void initialize() {
	    	
	    	passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
	    		passwordField.getStyleClass().remove("invalid-border");
	            errorLabel.setVisible(false);
	        });
	    	
	    	emailField.textProperty().addListener((observable, oldValue, newValue) -> {
	            emailField.getStyleClass().remove("invalid-border"); 
	            EmailAddresserrorLabel.setVisible(false);  
	        });
	    	
	    	emailField.textProperty().addListener((observable, oldValue, newValue) -> {
	            emailField.getStyleClass().remove("invalid-border"); 
	            errorLabel.setVisible(false);  
	        });
	    }
	    
	   
	    public void handleLoginButtonAction(ActionEvent event) throws Exception {
	    	
	     String Email = emailField.getText();
 		 String password = passwordField.getText();

        if (Email.isEmpty())
        {
        	errorLabel.setVisible(true);
            if (!emailField.getStyleClass().contains("invalid-border")) 
            {
            	emailField.getStyleClass().add("invalid-border");
            }

        }
        else if (!isValidEmail(Email)) 
        {
        	EmailAddresserrorLabel.setVisible(true);
            if (!emailField.getStyleClass().contains("invalid-border"))
            {
                emailField.getStyleClass().add("invalid-border");
            }
        }
        else if (password.isEmpty()) {
        	errorLabel.setVisible(true);
        	 if (!passwordField.getStyleClass().contains("invalid-border")) 
             {
                 passwordField.getStyleClass().add("invalid-border");
             }
        	
        }
        

        // Check credentials (replace this with actual database validation)\\\ צריך חיבור לדאטא 
        /*if ("admin".equals(Email) && "password".equals(password)) {
            showAlert(AlertType.INFORMATION, "Login Successful", "Welcome to BLib!");
        } else {
            showAlert(AlertType.ERROR, "Login Failed", "Invalid username or password. Try again.");
            if (!passwordField.getStyleClass().contains("invalid-border")) {
                passwordField.getStyleClass().add("invalid-border");
            }
        }
        return;*/
    }

	    private boolean isValidEmail(String email)
	    {
	        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
	        return email.matches(emailRegex);
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
