package gui.bounderies;



import application.ClientController;
import application.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

public class IpFrameController {

	
	@FXML
    private Button connectBtn;
	@FXML
	private TextField ipText;
	
	@FXML
	private TextField portText;
	
	
	
	private void showErrorAlert(String errorText) {
		Alert alert = new Alert(AlertType.ERROR);
		 alert.setContentText("Error");
		 alert.setHeaderText(errorText);
		 alert.showAndWait();
	}
	
	
	
	 public void start(Stage primaryStage) throws Exception {
		 
		 
		Parent root = FXMLLoader.load(getClass().getResource("/gui/bounderies/IpFrame.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/bounderies/IpFrame.css").toExternalForm());
		primaryStage.setTitle("Connect");
		primaryStage.setScene(scene);
		primaryStage.show();
	
	 }
	 
	 
	 
	 
	 
	 
	 public void connect(ActionEvent event) throws Exception {
		 String ipAddress = ipText.getText();
		 
		 if(!isValidPort(portText.getText())) {
			 showErrorAlert("port is invalid");
			 return;
		 }
		 int port = Integer.parseInt(portText.getText());
		 if(!isValidIPv4(ipAddress)) {
			 showErrorAlert("IP address is invalid");
			 ipText.clear();
			 return;
		 }
		 ClientUI.chat = new ClientController(ipAddress, port);
		 if(ClientUI.chat != null)
			 goToSubscriber(event);
		 else
			 showErrorAlert("client is null");
	}
	 
	 
	 
	 public void goToSubscriber(ActionEvent event) throws Exception {
		 	Stage primaryStage = new Stage();
	        ((Node)event.getSource()).getScene().getWindow().hide();
	        ClientFrameController subscriber = new ClientFrameController();
	        subscriber.start(primaryStage);
	    }
	 
	 
	 
	 private boolean isValidPort(String port) {
		    if (port == null || port.isEmpty()) {
		        return false;
		    }

		    try {
		        int portNumber = Integer.parseInt(port);
		        return portNumber >= 0 && portNumber <= 65535;
		    } catch (NumberFormatException e) {
		        // The port string is not a valid integer
		        return false;
		    }
		}

	 
	 private boolean isValidIPv4(String ip) {
		    if (ip == null || ip.isEmpty()) {
		        return false;
		    }
		    
		    // Split into 4 parts by '.'
		    String[] parts = ip.split("\\.");
		    if (parts.length != 4) {
		        return false;
		    }
		    
		    // Check each of the 4 segments
		    for (String part : parts) {
		        // Ensure each part is digits-only
		        if (!part.matches("\\d+")) {
		            return false;
		        }
		        
		        // Parse the segment as an integer and check the range
		        int value = Integer.parseInt(part);
		        if (value < 0 || value > 255) {
		            return false;
		        }
		    }
		    
		    return true;
		}
	 
	 
}
