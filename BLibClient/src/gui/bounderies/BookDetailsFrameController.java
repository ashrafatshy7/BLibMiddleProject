package gui.bounderies;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;

import application.ChatClient;
import application.ClientUI;
import enteties.Book;
import enteties.Subscriber;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import message.Message;
import message.MessageType;

public class BookDetailsFrameController {

	private ChatClient chatClient;
	private Book book;
	private String source, earliestReturnDate;
	private boolean alreadyOrdered;

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
	private Label shelfReturnDate;

	@FXML
	private Label description;

	@FXML
	private Label bookOrdered;

	@FXML
	private ImageView image;

	@FXML
	private Button order;

	@FXML
	public void initialize() {

		if (book != null) {
			populateBookDetails();
		}
		
		bookOrdered.setVisible(false);
		if (ClientUI.user instanceof Subscriber) {
			order.setVisible(false);
			if (alreadyOrdered) {
				order.setDisable(false);
				bookOrdered.setVisible(false);
			}
		} else {
			order.setVisible(false);
		}

	}

	public BookDetailsFrameController() {
		chatClient = ClientUI.chat.getClient();
	}

	public void setChatClient(ChatClient chatClient) {
		this.chatClient = chatClient;
		this.chatClient.setBookDetailsFrameController(this);
	}

	public void setBook(Book book) {
		this.book = book;
		if (title != null) {
			if (book.getAvailableCopies() == 0) {
				Message sendToServer = new Message(MessageType.getEarliestReturnDate, book.getBarcode());
				ClientUI.chat.accept(sendToServer);
			}
			populateBookDetails();
		}
	}

	public void setEarliestReturnDate(String date) {
		earliestReturnDate = date;
	}

	public void setAlreadyOrdered(boolean ordered) {
		alreadyOrdered = ordered;
		if (alreadyOrdered) {
			order.setDisable(true);
			bookOrdered.setVisible(true);
		}
	}

	public void setSource(String source) {
		this.source = source;
	}

	private void bookAvailable() {
		int available = book.getAvailableCopies();

		if (available > 0) {
			isAvailable.setText(isAvailable.getText() + "Yes");
			System.out.println(book.getShelfs());
			shelfReturnDate.setText("Shelf: " + book.getShelfs().get(0));
		} else {
			isAvailable.setText(isAvailable.getText() + "No");
			shelfReturnDate.setText("Return date: " + earliestReturnDate);
			if(ClientUI.user instanceof Subscriber)
				order.setVisible(true);
		}

	}

	private void populateBookDetails() {
		image.setImage(book.getImage());
		image.setPreserveRatio(true);

		title.setText(book.getTitle());
		author.setText(author.getText() + book.getAuthor());
		barcode.setText(barcode.getText() + book.getBarcode());
		category.setText(category.getText() + book.getCategory());
		bookAvailable();

		description.setText(book.getDescription());
	}
	
	

	
	
	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/BookDetailsFrame.fxml"));
			Parent root = loader.load();

			// Retrieve the controller instance from the loader
			BookDetailsFrameController controller = loader.getController();

			if (this.chatClient != null) {
				controller.setChatClient(this.chatClient);
			} else {
				// Handle the case where chatClient is null
				System.err.println("ChatClient is not initialized.");
			}

			controller.setBook(book);
			controller.setSource(source);

			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/gui/bounderies/BookDetailsFrame.css").toExternalForm());
			primaryStage.setTitle("Book Details");
			primaryStage.setScene(scene);
			primaryStage.show();
			
		
			if (ClientUI.user instanceof Subscriber) {

				ArrayList<String> checkOrder = new ArrayList<>();
				checkOrder.add(book.getBarcode());
				checkOrder.add(ClientUI.user.getID());
				Message sendToServer = new Message(MessageType.checkOderBook, checkOrder);
				ClientUI.chat.accept(sendToServer);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void backBtn(ActionEvent event) throws Exception {
		Stage primaryStage = new Stage();
		((Node) event.getSource()).getScene().getWindow().hide();
		if (source.equals("SeeAllFrameController")) {
			SeeAllFrameController seeAllFrame = new SeeAllFrameController();
			seeAllFrame.start(primaryStage);
		} else if (source.equals("HomeFrameController")) {
			HomeFrameController homeFrame = new HomeFrameController();
			homeFrame.start(primaryStage);
		}
	}

	@FXML
	public void orderBtn(ActionEvent event) throws Exception {
		ArrayList<String> orderDetails = new ArrayList<>();
		orderDetails.add(book.getBarcode());
		orderDetails.add(ClientUI.user.getID());
		Message sendToServer = new Message(MessageType.orderBook, orderDetails);
		ClientUI.chat.accept(sendToServer);
		order.setDisable(true);
		bookOrdered.setVisible(true);
	}

}
