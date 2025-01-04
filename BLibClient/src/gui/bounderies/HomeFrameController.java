package gui.bounderies;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class HomeFrameController {
	
	
	
	@FXML
    private Button seeAll;
	
	@FXML
	private Button login;
	
	@FXML
	private Label status;
	
	@FXML
	private Button myInfo;
	
	@FXML
	private ImageView image1;
	
	@FXML
	private ImageView image2;
	
	@FXML
	private ImageView image3;
	
	@FXML
	private ImageView image4;
	
	@FXML
	private ImageView image5;
	
	@FXML 
	private Label name1;
	
	@FXML 
	private Label name2;
	
	@FXML 
	private Label name3;
	
	@FXML 
	private Label name4;
	
	@FXML 
	private Label name5;
	
	
	 public void start(Stage primaryStage) throws Exception {
	        try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/HomeFrame.fxml"));
	            Parent root = loader.load();
	            HomeFrameController controller = loader.getController();
	           
	            Scene scene = new Scene(root);
	            scene.getStylesheets().add(getClass().getResource("/gui/bounderies/HomeFrame.css").toExternalForm());
	            primaryStage.setTitle("Home Page");
	            primaryStage.setScene(scene);
	            primaryStage.show();
	            
	            
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
    }
	 
	 
	 public void information(ActionEvent event) throws Exception {
		 
	 }
	 
	public void seeAllBooks(ActionEvent event) throws Exception {
		((Node)event.getSource()).getScene().getWindow().hide();
		Stage primaryStage = new Stage();
		SeeAllFrameController seeAllFrame = new SeeAllFrameController();
		seeAllFrame.start(primaryStage);
	 }
	

}
