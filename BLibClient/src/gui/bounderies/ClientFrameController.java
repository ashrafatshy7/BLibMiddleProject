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

public class ClientFrameController {

    @FXML
    private Button updateDetails;
    
    @FXML
    private Button backBtn;

    @FXML
    private TableView<Subscriber> tableView;

    @FXML
    private TableColumn<Subscriber, String> subscriberID;

    @FXML
    private TableColumn<Subscriber, String> subscriberName;

    @FXML
    private TableColumn<Subscriber, String> subscriberPhoneNumber;

    @FXML
    private TableColumn<Subscriber, String> subscriberEmail;

    @FXML
    private TableColumn<Subscriber, String> subscriptionHistory;

    private ObservableList<Subscriber> subscribersList = FXCollections.observableArrayList();
    private ChatClient chatClient;
    
    private Map<String, Map<String, String>> changedSubscribers = new HashMap<>();

    
    public ClientFrameController() {
    	chatClient = ClientUI.chat.getClient();
    }
    
    
    private void showErrorAlert(String errorText) {
		Alert alert = new Alert(AlertType.ERROR);
		 alert.setContentText("Error");
		 alert.setHeaderText(errorText);
		 alert.showAndWait();
	}
    

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

    public void setChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
           this.chatClient.setClientFrameController(this);
    }

    public void initializeData() {
        if (chatClient != null) {
        	requestSubscribers();
        } else {
            showErrorAlert("ChatClient is not set!");
        }
    }

    public void requestSubscribers() {
        ClientUI.chat.accept("getAllValues subscribers");
    }
    
    
    //update subscribers 215612351 phoneNumber 054652 email qqq@gmail.com
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
    
    
    

 
    public void getSubscribers(ArrayList<Subscriber> subscribers) {
        Platform.runLater(() -> {
            subscribersList.clear();
            subscribersList.setAll(subscribers);
            this.tableView.refresh();
        });
        this.tableView.setItems(subscribersList);
    }
    
    
    public void backToMainMenu(ActionEvent event) throws Exception {
    	Stage primaryStage = new Stage();
        ((Node)event.getSource()).getScene().getWindow().hide();
        IpFrameController ipFrameController = new IpFrameController();
        ipFrameController.start(primaryStage);
        chatClient.quit();
        
    }
    
    
    
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
    
    private boolean isValidEmail(String email) {
    	if (email == null || email.isEmpty()) {
            return false;
        }
    	String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    	return email.matches(regex);
    }
    
    
}
