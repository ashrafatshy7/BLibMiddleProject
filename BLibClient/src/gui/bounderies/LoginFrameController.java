package gui.bounderies;

import java.io.IOException;
import java.util.ArrayList;

import application.ChatClient;
import application.ClientUI;
import enteties.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
/*import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;*/
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import message.Message;
import message.MessageType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for handling user login.
 */
public class LoginFrameController {
	 /** Chat client instance. */
    private ChatClient chatClient;
    
    /** Label to display login title. */
    @FXML
    private Label LoginLabel;
    
    /** TextField to input email. */
    @FXML
    private TextField emailField;

    /** PasswordField to input password. */
    @FXML
    private PasswordField passwordField;

    /** Button to initiate login. */
    @FXML
    private Button loginButton;

    /** Label to display error messages. */
    @FXML
    private Label errorLabel;

    /** Label to display email address error messages. */
    @FXML
    private Label EmailAddresserrorLabel;
    
    /** Label to display email field label. */
    @FXML
    private Label EmailLabel;
    
    /** Label to display password field label. */
    @FXML
    private Label PasswordLabel;
    /**
     * Initializes UI components and event listeners.
     */
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
	    
	    /**
	     * Sets the user after successful login.
	     * @param user The logged-in user.
	     */
	    public void setUser(User user) {
	    	Platform.runLater(() -> {
	    	if(user != null) {
	    		ClientUI.user = user;
	    		
	                Stage currentStage = (Stage) loginButton.getScene().getWindow();
	                currentStage.close();

	                // Open the HomeFrame
	                Stage primaryStage = new Stage();
	                HomeFrameController homeFrameController = new HomeFrameController();
	                try {
	                    homeFrameController.start(primaryStage);
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            
	    	}
	    	else {
	    		showAlert("User Not Found", "No User data is available.");
	    		System.out.println("User Not Found");
	    	}
	    	});
	    }
	    
	    /**
	     * Default constructor that initializes the chat client.
	     */
	    public LoginFrameController() {
			chatClient = ClientUI.chat.getClient();
		}
	    
	    /**
	     * Sets the chat client instance.
	     * @param chatClient The chat client to set.
	     */
	    public void setChatClient(ChatClient chatClient) {
			this.chatClient = chatClient;
			this.chatClient.setLoginFrameController(this);
		}
	    
	   
	    /**
	     * Handles login button action.
	     * @param event The action event.
	     * @throws Exception If an error occurs.
	     */
	    public void handleLoginButtonAction(ActionEvent event) throws Exception {
	    	
	     String email = emailField.getText();
 		 String password = passwordField.getText();

        if (email.isEmpty())
        {
        	errorLabel.setVisible(true);
        	
            if (!emailField.getStyleClass().contains("invalid-border")) 
            {
            	emailField.getStyleClass().add("invalid-border");
            	
            }
            return;

        }
        else if (!isValidEmail(email)) 
        {
        	EmailAddresserrorLabel.setVisible(true);
            if (!emailField.getStyleClass().contains("invalid-border"))
            {
                emailField.getStyleClass().add("invalid-border");
            }
            return;
        }
        else if (password.isEmpty()) {
        	errorLabel.setVisible(true);
        	 if (!passwordField.getStyleClass().contains("invalid-border")) 
             {
                 passwordField.getStyleClass().add("invalid-border");
             }
        	 return;
        	
        }
        
        ArrayList<String> login = new ArrayList<>();
        login.add(email);
        login.add(password);
        Message sendToServer = new Message(MessageType.login, login);
        ClientUI.chat.accept(sendToServer);
        

    }
	    /**
	     * Validates the email address format.
	     * @param email The email to validate.
	     * @return True if valid, false otherwise.
	     */
	    private boolean isValidEmail(String email)
	    {
	        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
	        return email.matches(emailRegex);
	    }
	    
	    
	    /**
	     * Displays an alert message.
	     * @param alertType The type of alert.
	     * @param title The title of the alert.
	     * @param message The alert message.
	     */
	    private void showAlert(String title, String content) {
	        Alert alert = new Alert(Alert.AlertType.INFORMATION);
	        alert.setTitle(title);
	        alert.setHeaderText(null);
	        alert.setContentText(content);
	        alert.showAndWait();
	    }
	    
	    
	    /**
	     * Starts the Login Frame.
	     * @param primaryStage The primary stage.
	     * @throws Exception If an error occurs while loading the frame.
	     */
	    public void start(Stage primaryStage) throws Exception {
	        try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/LoginFrame.fxml"));
	            Parent root = loader.load();
	            LoginFrameController controller = loader.getController();
	            
	            if (this.chatClient != null) {
					controller.setChatClient(this.chatClient);
				} else {
					// Handle the case where chatClient is null
					System.err.println("ChatClient is not initialized.");
				}
	            
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