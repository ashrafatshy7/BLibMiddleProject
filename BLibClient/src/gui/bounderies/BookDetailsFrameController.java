package gui.bounderies;


import javafx.event.ActionEvent;
import java.io.IOException;

import enteties.Book;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class BookDetailsFrameController {
	private Book book;
	
	@FXML
	private Label title;
	
	@FXML
	private Label author;
	
	@FXML
	private Label barcode;
	
	@FXML
	private Label category;
	
	@FXML
	private Label isAvailable;
	
	@FXML
	private Label shelf;
	
	@FXML
	private Label description;
	
	@FXML
	private ImageView image;
	
	@FXML
    private void initialize() {
        if (book != null) {
            populateBookDetails();
        }
    }
	
	public void setBook(Book book) {
        this.book = book;
        // If FXML is already loaded, populate fields now
        if (title != null) {
            populateBookDetails();
        }
    }
	
	
	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/BookDetailsFrame.fxml"));
			Parent root = loader.load();
					
            BookDetailsFrameController controller = loader.getController();
            controller.setBook(this.book);
					
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/gui/bounderies/BookDetailsFrame.css").toExternalForm());
			primaryStage.setTitle("Details");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void populateBookDetails() {
		image.setImage(book.getImage());
		image.setPreserveRatio(true);
		
        title.setText(book.getTitle());      
        author.setText(author.getText()+book.getAuthor());       
        barcode.setText(barcode.getText()+book.getBarcode());
        category.setText(category.getText()+book.getCategory());
        isAvailable.setText(isAvailable.getText()+(book.isAvailable() ? "Yes" : "No"));
        shelf.setText(shelf.getText()+book.getShelf());
        description.setText(book.getDescription());
    }
	
	
	@FXML
	public void backBtn(ActionEvent event) throws Exception {
		Stage primaryStage = new Stage();
		((Node) event.getSource()).getScene().getWindow().hide();
		SeeAllFrameController bookDetails = new SeeAllFrameController();
		bookDetails.start(primaryStage);
	}
	
}
