package gui.bounderies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import application.ChatClient;
import application.ClientUI;
import enteties.Subscriber;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

/**
 * Controller for the Client Frame.
 */
public class ClientFrameController {

	 /** Button to update subscriber details. */
    @FXML
    private Button updateDetails;
    
    /** Button to go back to the previous screen. */
    @FXML
    private Button backBtn;

    /** TableView to display subscriber details. */
    @FXML
    private TableView<Subscriber> tableView;

    /** TableColumn for subscriber ID. */
    @FXML
    private TableColumn<Subscriber, String> subscriberID;

    /** TableColumn for subscriber name. */
    @FXML
    private TableColumn<Subscriber, String> subscriberName;

    /** TableColumn for subscriber phone number. */
    @FXML
    private TableColumn<Subscriber, String> subscriberPhoneNumber;

    /** TableColumn for subscriber email. */
    @FXML
    private TableColumn<Subscriber, String> subscriberEmail;

    /** TableColumn for subscriber subscription history. */
    @FXML
    private TableColumn<Subscriber, String> subscriptionHistory;

    /** List to hold subscriber data. */
    private ObservableList<Subscriber> subscribersList = FXCollections.observableArrayList();

    /** Chat client instance. */
    private ChatClient chatClient;
    
    /** Map to store changed subscriber details. */
    private Map<String, Map<String, String>> changedSubscribers = new HashMap<>();

    
    /**
     * Default constructor that initializes the chat client.
     */
    public ClientFrameController() {
    	chatClient = ClientUI.chat.getClient();
    }
    
    
    /**
     * Displays an error alert with the provided text.
     * @param errorText The error message to display.
     */
    private void showErrorAlert(String errorText) {
		Alert alert = new Alert(AlertType.ERROR);
		 alert.setContentText("Error");
		 alert.setHeaderText(errorText);
		 alert.showAndWait();
	}
    
    /**
     * Initializes the TableView and its columns.
     */
    @FXML
    public void initialize() {
        subscriberID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        subscriberName.setCellValueFactory(new PropertyValueFactory<>("name"));
        subscriberPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        subscriberEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        subscriptionHistory.setCellValueFactory(new PropertyValueFactory<>("detailedSubscriptionHistory"));
        
        tableView.setEditable(true);

        subscriberPhoneNumber.setCellFactory(TextFieldTableCell.forTableColumn());
        subscriberEmail.setCellFactory(TextFieldTableCell.forTableColumn());

        subscriberPhoneNumber.setOnEditCommit(event -> {
            Subscriber sub = event.getRowValue();
            sub.setPhoneNumber(event.getNewValue());
            
            if(changedSubscribers.get(sub.getID()) == null) {
            	changedSubscribers.put(sub.getID(), new HashMap<>());
            	
            }
            Map<String, String> temp = changedSubscribers.get(sub.getID()) ;
        	temp.put("phoneNumber", sub.getPhoneNumber());


        });

        subscriberEmail.setOnEditCommit(event -> {
            Subscriber sub = event.getRowValue();
            sub.setEmail(event.getNewValue());

            if(changedSubscribers.get(sub.getID()) == null) {
            	changedSubscribers.put(sub.getID(), new HashMap<>());
            	
            }
            Map<String, String> temp = changedSubscribers.get(sub.getID()) ;
        	temp.put("email", sub.getEmail());

        });

    }

    /**
     * Starts the Client Frame.
     * @param primaryStage The primary stage.
     * @throws Exception If an error occurs while loading the frame.
     */
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/ClientFrame.fxml"));
            Parent root = loader.load();
            ClientFrameController controller = loader.getController();
            if (this.chatClient != null) {
                controller.setChatClient(this.chatClient);
            }
            controller.initializeData();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/bounderies/ClientFrame.css").toExternalForm());
            primaryStage.setTitle("Client");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            
            primaryStage.setOnCloseRequest(event -> {
                chatClient.quit();
                Platform.exit();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    /**
     * Sets the chat client instance.
     * @param chatClient The chat client to set.
     */
    public void setChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
        this.chatClient.setClientFrameController(this);
    }

    
    /**
     * Initializes data by requesting subscribers from the server.
     */
    public void initializeData() {
        if (chatClient != null) {
        	requestSubscribers();
        } else {
            showErrorAlert("ChatClient is not set!");
        }
    }

    /**
     * Sends a request to fetch all subscribers from the server.
     */
    public void requestSubscribers() {
        ClientUI.chat.accept("getAllValues subscribers");
    }
    
    
    /**
     * Requests updated subscriber details and sends them to the server.
     * @param event The action event triggering the update.
     * @throws Exception If an error occurs.
     */
    public void requestUpdatedSubscribers(ActionEvent event) throws Exception {
    	for(String subscriberID: changedSubscribers.keySet()) {
    		//String str = "update subscribers "+subscriberID + " ";
    		for(String changed: changedSubscribers.get(subscriberID).keySet()) {
    			if((changed.equals("phoneNumber") && !isValidPhoneNumber(changedSubscribers.get(subscriberID).get(changed))) || (changed.equals("email") && !isValidEmail(changedSubscribers.get(subscriberID).get(changed)))) {
    				showErrorAlert("check updated ddetails");
    				return;
    			}
    		}
    	}
    	
    	
    	for(String subscriberID: changedSubscribers.keySet()) {
    		String str = "update subscribers "+subscriberID + " ";
    		for(String changed: changedSubscribers.get(subscriberID).keySet()) {
    			str+= changed +" "+ changedSubscribers.get(subscriberID).get(changed)+" ";
    		}
    		ClientUI.chat.accept(str);
    		str = "";
    	}
    } 
    
    
    

    /**
     * Sets the list of subscribers in the TableView.
     * @param subscribers The list of subscribers.
     */
    public void setSubscribers(ArrayList<Subscriber> subscribers) {
        Platform.runLater(() -> {
            subscribersList.clear();
            subscribersList.setAll(subscribers);
            this.tableView.refresh();
        });
        this.tableView.setItems(subscribersList);
    }
    
    
    
    /**
     * Handles action to navigate back to the main menu.
     * @param event The action event.
     * @throws Exception If an error occurs.
     */
    public void backToMainMenu(ActionEvent event) throws Exception {
    	Stage primaryStage = new Stage();
        ((Node)event.getSource()).getScene().getWindow().hide();
        IpFrameController ipFrameController = new IpFrameController();
        ipFrameController.start(primaryStage);
        chatClient.quit();
        
    }
    
    
    /**
     * Validates if the provided phone number is in the correct format.
     * @param pNum The phone number to validate.
     * @return True if valid, false otherwise.
     */
    private boolean isValidPhoneNumber(String pNum) {
    	if(pNum == null || pNum.isEmpty() || pNum.length() != 10 || !pNum.startsWith("05"))
    		return false;
    	for (int i = 0; i < pNum.length(); i++) {
            if (!Character.isDigit(pNum.charAt(i))) {
                return false;
            }
        }
    	return true;
    }
    
    
    
    /**
     * Validates if the provided email address is in the correct format.
     * @param email The email to validate.
     * @return True if valid, false otherwise.
     */
    private boolean isValidEmail(String email) {
    	if (email == null || email.isEmpty()) {
            return false;
        }
    	String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    	return email.matches(regex);
    }
    
    
}
