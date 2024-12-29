package gui.bounderies;

import application.Client;
import application.EchoServer;
import application.ServerUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.property.SimpleStringProperty; 

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class ClientsFrameController {

    @FXML
    private TableView<Client> clientsTable;

    @FXML
    private TableColumn<Client, String> ipColumn;

    @FXML
    private TableColumn<Client, String> hostColumn;

    @FXML
    private TableColumn<Client, String> statusColumn;
    
    @FXML
    private Button backBtn;

    private ObservableList<Client> clientsData;
    
    
    public void backToMainMenu(ActionEvent event) throws Exception {
    	Stage primaryStage = new Stage();
        ((Node)event.getSource()).getScene().getWindow().hide();
        ServerFrameController serverFrameController = new ServerFrameController();
        serverFrameController.start(primaryStage);
    }
    
    
    
    private void showErrorAlert(String errorText) {
		Alert alert = new Alert(AlertType.ERROR);
		 alert.setContentText("Error");
		 alert.setHeaderText(errorText);
		 alert.showAndWait();
	}
    
    
    public void start(Stage primaryStage) throws Exception {
    	 
       
    	/////
    	/////////
    	/////////
    }

    @FXML
    public void initialize() {
        // Initialize the columns
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));
        hostColumn.setCellValueFactory(new PropertyValueFactory<>("hostName"));
        
        // Corrected CellValueFactory for statusColumn
        statusColumn.setCellValueFactory(cellData -> {
            boolean isConnected = cellData.getValue().isConnected();
            String status = isConnected ? "Connected" : "Disconnected";
            return new SimpleStringProperty(status); // Use ReadOnlyStringWrapper if preferred
            // return new ReadOnlyStringWrapper(status);
        });
        
       

        // Load data
        loadClientData();

        // Optionally, add double-click to refresh
        clientsTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    loadClientData();
                }
            }
        });
        
        
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> loadClientData()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Loads client data from the server and populates the TableView.
     */
    private void loadClientData() {
        EchoServer server = ServerUI.getServer();
        if (server != null) {
            List<Client> clients = server.getClients();
            clientsData = FXCollections.observableArrayList(clients);
            clientsTable.setItems(clientsData);
        } else {
            showErrorAlert("Server is not running.");
        }
    }
}
