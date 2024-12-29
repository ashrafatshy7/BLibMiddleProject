package gui.bounderies;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;

import application.EchoServer;
import application.ServerUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerFrameController {
	
	EchoServer server;

    @FXML
    private Button startServerBtn;
    
    @FXML
    private Button showClientsBtn;

    @FXML
    private TextField portTxt;
    
    
    @FXML
    public void initialize() {
    	server = ServerUI.getServer();
        if (server != null) {
            showClientsBtn.setDisable(false);
            startServerBtn.setText("Stop Server");
        }
    }
    
    
    
    
    private void showErrorAlert(String errorText) {
		Alert alert = new Alert(AlertType.ERROR);
		 alert.setContentText("Error");
		 alert.setHeaderText(errorText);
		 alert.showAndWait();
	}

    public void start(Stage primaryStage) throws Exception {    
    	Parent root = FXMLLoader.load(getClass().getResource("/gui/bounderies/ServerFrame.fxml"));

                
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/bounderies/ServerFrame.css").toExternalForm());
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.show();        
    }

    @FXML
    public void StartServer(ActionEvent event) throws Exception {
    	
    	if(startServerBtn.getText().equals("Stop Server")) {
    		ServerUI.stopServer();
    		startServerBtn.setText("Start Server");
    		showClientsBtn.setDisable(true);
    		return;
    	}
        String p= portTxt.getText();
        //check if the portTxt is not empty and contains only numbers.
        if(p.trim().isEmpty() || !p.trim().matches("\\d+")) {
            showErrorAlert("You must enter a valid port number");

        }
        else
        {
            
            ServerUI.runServer(p);
            showClientsBtn.setDisable(false);
            startServerBtn.setText("Stop Server");
        }
    }
    
    @FXML
    public void showClients(ActionEvent event) {
        try {
            ((Node)event.getSource()).getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/ClientsFrame.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Connected Clients");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
