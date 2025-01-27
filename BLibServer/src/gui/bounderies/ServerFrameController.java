package gui.bounderies;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.List;

import application.Client;
import application.EchoServer;
import application.ServerUI;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * The {@code ServerFrameController} class is responsible for controlling the server GUI. It 
 * handles the server startup and shutdown actions, displays a list of connected clients, and 
 * allows the user to specify a port number on which the server will run.
 */
public class ServerFrameController {
	
	 /** Reference to the EchoServer instance. */
    EchoServer server;

    /** Button to start or stop the server. */
    @FXML
    private Button startServerBtn;

    /** TextField for entering the port number. */
    @FXML
    private TextField portTxt;

    /** TableView that displays a list of connected clients. */
    @FXML
    private TableView<Client> clientsTable;

    /** TableColumn for displaying the IP address of a client. */
    @FXML
    private TableColumn<Client, String> ipColumn;

    /** TableColumn for displaying the host name of a client. */
    @FXML
    private TableColumn<Client, String> hostColumn;

    /** TableColumn for displaying the connection status of a client. */
    @FXML
    private TableColumn<Client, String> statusColumn;

    /** ObservableList containing the connected clients. */
    private ObservableList<Client> clientsData;
    
    private Timeline timeline;

    /**
     * Initializes the server frame controller.
     * <p>
     * This method is automatically called after the FXML has been loaded. It checks if the 
     * server is running and sets the start/stop button text accordingly. It also sets up 
     * the columns of the clients TableView.
     */
    @FXML
    public void initialize() {
    	server = ServerUI.getServer();
        if (server != null) {
            startServerBtn.setText("Stop Server");
        }
        
        
        
        
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));
        hostColumn.setCellValueFactory(new PropertyValueFactory<>("hostName"));
        
        statusColumn.setCellValueFactory(cellData -> {
            boolean isConnected = cellData.getValue().isConnected();
            String status = isConnected ? "Connected" : "Disconnected";
            return new SimpleStringProperty(status); 
        });
        

        
        
        
    }
    
    
    /**
     * Shows an error alert with a specified message. Uses a non-blocking alert dialog.
     *
     * @param errorText the text to be displayed as the header of the alert
     */
    private void showErrorAlert(String errorText) {
		Alert alert = new Alert(AlertType.ERROR);
		 alert.setContentText("Error");
		 alert.setHeaderText(errorText);
		 alert.show();
	}

    
    /**
     * Launches the server frame GUI in the specified primary stage.
     *
     * @param primaryStage the primary stage for this JavaFX application
     * @throws Exception if an error occurs during loading the FXML or setting up the scene
     */
    public void start(Stage primaryStage) throws Exception {    
    	Parent root = FXMLLoader.load(getClass().getResource("/gui/bounderies/ServerFrame.fxml"));

                
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/bounderies/ServerFrame.css").toExternalForm());
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.show();        
    }

    
    /**
     * Handles the action event for the start/stop server button.
     * <p>
     * If the server is currently running, it will stop the server and change the button 
     * text to "Start Server". If the server is not running, it attempts to start the server 
     * using the port specified in the {@code portTxt} field. If the port is invalid or empty, 
     * it displays an error alert.
     *
     * @param event the action event triggered by clicking the button
     * @throws Exception if an error occurs while starting the server
     */
    @FXML
    public void StartServer(ActionEvent event) throws Exception {
    	
    	if(startServerBtn.getText().equals("Stop Server")) {
    	    ServerUI.stopServer();
    	    startServerBtn.setText("Start Server");
    	    clientsData.clear();
    	    clientsTable.getItems().clear();
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
            startServerBtn.setText("Stop Server");
           
            timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> loadClientData()));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
    }
    
  
    /**
     * Loads the client data from the server and populates the {@code clientsTable}.
     * <p>
     * If the server is not running, displays an error alert.
     */
    private void loadClientData() {
        EchoServer server = ServerUI.getServer();
        if (server != null) {
            List<Client> clients = server.getClients();
            clientsData = FXCollections.observableArrayList(clients);
            clientsTable.setItems(clientsData);
        } else {
            showErrorAlert("Server is not running.");
            timeline.stop();
        }
    }

}
