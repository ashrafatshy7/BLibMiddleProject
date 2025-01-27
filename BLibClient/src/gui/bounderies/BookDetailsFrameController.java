package gui.bounderies;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;

import application.ChatClient;
import application.ClientUI;
import enteties.Book;
import enteties.Order;
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
/**
 * Controller for the Book Details Frame.
 */
public class BookDetailsFrameController {
	/** Chat client instance. */
    private ChatClient chatClient;

    /** The book whose details are displayed. */
    private Book book;

    /** Source frame identifier. */
    private String source;

    /** Earliest return date of the book. */
    private String earliestReturnDate;

    /** Indicates whether the book is already ordered. */
    private boolean alreadyOrdered;

    /** Label for book title. */
    @FXML
    private Label title;

    /** Label for book author. */
    @FXML
    private Label author;

    /** Label for book barcode. */
    @FXML
    private Label barcode;

    /** Label for book category. */
    @FXML
    private Label category;

    /** Label indicating book availability. */
    @FXML
    private Label isAvailable;

    /** Label for shelf return date. */
    @FXML
    private Label shelfReturnDate;

    /** Label for book description. */
    @FXML
    private Label description;

    /** Label indicating book order status. */
    @FXML
    private Label bookOrdered;

    /** Image view for book cover. */
    @FXML
    private ImageView image;

    /** Button to order the book. */
    @FXML
    private Button order;

    
    /**
     * Initializes the UI elements and their visibility.
     */
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
	
	/**
     * Default constructor that initializes the chat client.
     */
	public BookDetailsFrameController() {
		chatClient = ClientUI.chat.getClient();
	}
	
	/**
     * Sets the chat client instance.
     * @param chatClient The chat client to set.
     */
	public void setChatClient(ChatClient chatClient) {
		this.chatClient = chatClient;
		this.chatClient.setBookDetailsFrameController(this);
	}
	
	/**
     * Sets the book details.
     * @param book The book to set.
     */
	public void setBook(Book book) {
		this.book = book;
		if (title != null) {
			if (book.getAvailableCopies() == 0) {
				order.setDisable(true);
				Message sendToServer = new Message(MessageType.getEarliestReturnDate, book.getBarcode());
				ClientUI.chat.accept(sendToServer);
				
			}
			populateBookDetails();
		}
	}
	
	/**
     * Sets the earliest return date for the book.
     * @param date The earliest return date.
     */
	public void setEarliestReturnDate(String date) {
		earliestReturnDate = date;
	}

	/**
     * Sets the order status of the book.
     * @param ordered Whether the book is already ordered.
     */
	public void setAlreadyOrdered(boolean ordered) {
		
		alreadyOrdered = ordered;
		if (alreadyOrdered) {
			order.setDisable(true);
			bookOrdered.setVisible(true);
		}
	}

	
	/**
     * Sets the order check result.
     * @param ordered Whether the book is already ordered.
     * @param message The order status message.
     */
	public void setCheckOrderBook(boolean ordered, String message) {
		alreadyOrdered = ordered;
		if (!alreadyOrdered) {
			order.setDisable(true);
			bookOrdered.setVisible(true);
			if(message.equals("UserHasActiveLoan"))
				bookOrdered.setText("You have active loan!");
			else if(message.equals("OrderExists"))
				bookOrdered.setText("You already have an order!");
			else if(message.equals("error"))
				bookOrdered.setText("error in ordering!");
		}
	}
	
	
	/**
     * Sets the source frame.
     * @param source The source to set.
     */
	public void setSource(String source) {
		this.source = source;
	}

	/**
     * Handles book availability logic.
     */
	private void bookAvailable() {
		int available = book.getAvailableCopies();

		if (available > 0) {
			isAvailable.setText(isAvailable.getText() + "Yes");
			System.out.println(book.getShelfs());
			shelfReturnDate.setText("Shelf: " + book.getShelfs().get(0));
		} else {
			isAvailable.setText(isAvailable.getText() + "No");
			shelfReturnDate.setText("Return date: " + earliestReturnDate);
			if (ClientUI.user instanceof Subscriber) {
				order.setVisible(true);
				boolean isActive = ((Subscriber)ClientUI.user).getIsActive();
				if(isActive) {
					bookOrdered.setText("You ordered this book!");
					order.setDisable(false);
				}else {
					bookOrdered.setText("Your Account is Frozen!");
					bookOrdered.setVisible(true);
					order.setDisable(true);
				}
			}
		}

	}

	
	
	/**
     * Populates the UI elements with book details.
     */
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

	
	
	/**
     * Starts the Book Details Frame.
     * @param primaryStage The primary stage to set.
     * @throws Exception If an error occurs while loading the frame.
     */
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
				Order orderBook = new Order(ClientUI.user.getID(), book.getBarcode());
				Message sendToServer = new Message(MessageType.checkOrderBook, orderBook);
				ClientUI.chat.accept(sendToServer);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	/**
     * Handles back button action.
     * @param event The action event.
     * @throws Exception If an error occurs.
     */
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

	
	 /**
     * Handles order button action.
     * @param event The action event.
     * @throws Exception If an error occurs.
     */
	@FXML
	public void orderBtn(ActionEvent event) throws Exception {
		Order orderBook = new Order(ClientUI.user.getID(), book.getBarcode());
		Message sendToServer = new Message(MessageType.orderBook, orderBook);
		ClientUI.chat.accept(sendToServer);
		order.setDisable(true);
		bookOrdered.setVisible(true);
	}

}
