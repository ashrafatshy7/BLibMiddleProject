package gui.bounderies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application.ChatClient;
import application.ClientUI;
import enteties.Book;
import enteties.Librarian;
import enteties.Subscriber;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import message.Message;
import message.MessageType;

public class HomeFrameController {

	private ChatClient chatClient;

	@FXML
	private Button seeAll;

	@FXML
	private Button login;

	@FXML
	private Label status;

	@FXML
	private Label activeFrozen;

	@FXML
	private Button subscriberInfo;

	@FXML
	private Button librarianRegisterReader;

	@FXML
	private Button librarianreturnBook;

	@FXML
	private Button librarianLoanBook;

	@FXML
	private Button librarianReport;

	@FXML
	private FlowPane topBooksContainer; // Updated container reference

	@FXML
	private void initialize() {

		configureUserInterface();

	}

	public HomeFrameController() {
		chatClient = ClientUI.chat.getClient();
	}

	public void setChatClient(ChatClient chatClient) {
		this.chatClient = chatClient;
		this.chatClient.setHomeFrameController(this);
	}

	@FXML
	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/HomeFrame.fxml"));
			HomeFrameController controller = new HomeFrameController();
			loader.setController(controller);
			Parent root = loader.load();

			// Ensure that the ChatClient is set before any server messages are handled
			if (this.chatClient != null) {
				controller.setChatClient(this.chatClient);
			} else {
				// Handle the case where chatClient is null
				System.err.println("ChatClient is not initialized.");
			}

			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/gui/bounderies/HomeFrame.css").toExternalForm());
			primaryStage.setTitle("Home Page");
			primaryStage.setScene(scene);
			primaryStage.show();

			Message sendToServer = new Message(MessageType.getTop5LoanedBooks);
			ClientUI.chat.accept(sendToServer);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configures the visibility of UI components based on the user's role.
	 */
	private void configureUserInterface() {
		// Hide all role-specific buttons initially
		subscriberInfo.setVisible(false);
		librarianRegisterReader.setVisible(false);
		librarianreturnBook.setVisible(false);
		librarianLoanBook.setVisible(false);
		librarianReport.setVisible(false);
		status.setVisible(false);
		activeFrozen.setVisible(false);
		subscriberInfo.setVisible(true);

		// Show buttons based on user type
		if (ClientUI.user instanceof Subscriber) {
			// subscriberInfo.setVisible(true);
			status.setVisible(true);
			activeFrozen.setVisible(true);
		} else if (ClientUI.user instanceof Librarian) {
			librarianRegisterReader.setVisible(true);
			librarianreturnBook.setVisible(true);
			librarianLoanBook.setVisible(true);
			librarianReport.setVisible(true);
		}

		if (ClientUI.user != null) {
			login.setText("Logout");
		}
	}

	/**
	 * Opens the Subscriber Information view.
	 */
	public void registerReader(ActionEvent event) throws Exception {
		((Node) event.getSource()).getScene().getWindow().hide();
		Stage primaryStage = new Stage();
		RegisterSubscriberController registerSubscriber = new RegisterSubscriberController();
		registerSubscriber.start(primaryStage);
	}

	public void returnBook(ActionEvent event) throws Exception {
		((Node) event.getSource()).getScene().getWindow().hide();
		Stage primaryStage = new Stage();
		ReturnFrameController returnBook = new ReturnFrameController();
		returnBook.start(primaryStage);
	}

	public void loanBook(ActionEvent event) throws Exception {
		((Node) event.getSource()).getScene().getWindow().hide();
		Stage primaryStage = new Stage();
		LoanFrameController loanBook = new LoanFrameController();
		loanBook.start(primaryStage);
	}

	public void orderBook(ActionEvent event) throws Exception {
		System.out.println("Subscriber Information clicked.");
		// Implement navigation to Subscriber Information view
	}

	public void report(ActionEvent event) throws Exception {
		((Node) event.getSource()).getScene().getWindow().hide();
		Stage primaryStage = new Stage();
		TwoChartsController twoChartsController = new TwoChartsController();
		twoChartsController.start(primaryStage);
	}

	//
	public void btnShowSubscriberDetails(ActionEvent event) throws Exception {
		((Node) event.getSource()).getScene().getWindow().hide();
		Stage primaryStage = new Stage();
		SubscriberCardDetailsController subscriberCardDetailsController = new SubscriberCardDetailsController();
		subscriberCardDetailsController.start(primaryStage);
	}

	/**
	 * Opens the See All Books view.
	 */
	public void seeAllBooks(ActionEvent event) throws Exception {
		((Node) event.getSource()).getScene().getWindow().hide();
		Stage primaryStage = new Stage();
		SeeAllFrameController seeAllFrame = new SeeAllFrameController();
		seeAllFrame.start(primaryStage);
	}

	public void loginBtn(ActionEvent event) throws Exception {
		if (login.getText().equals("Login")) {
			((Node) event.getSource()).getScene().getWindow().hide();
			Stage primaryStage = new Stage();
			LoginFrameController loginFrameController = new LoginFrameController();
			loginFrameController.start(primaryStage);
		} else if (login.getText().equals("Logout")) {
			ClientUI.user = null;
			configureUserInterface();
			login.setText("Login");
		}
	}

	/**
	 * Sets the books and populates the top books UI. This method should be called
	 * after fetching books from the server.
	 *
	 * @param books List of top 5 loaned books.
	 */
	public void setBooks(ArrayList<Book> books) {
		// Populate the top books UI
		populateTopBooks(books);
	}

	/**
	 * Populates the topBooksContainer with book images and names. Ensures that UI
	 * updates are run on the JavaFX Application Thread.
	 *
	 * @param topBooks List of top loaned books.
	 */
	private void populateTopBooks(List<Book> topBooks) {
		Platform.runLater(() -> {
			topBooksContainer.getChildren().clear();

			for (Book book : topBooks) {
				VBox bookBox = new VBox();
				bookBox.setSpacing(5);
				ImageView imageView = new ImageView(book.getImage());
				imageView.setFitWidth(109.0);
				imageView.setFitHeight(182.0);
				imageView.setPreserveRatio(true);

				Label bookName = new Label(book.getTitle());
				bookName.setWrapText(true);
				bookName.setMaxWidth(109.0);
				bookName.setStyle("-fx-alignment: center;");

				bookBox.setOnMouseClicked(event -> {
					try {
						Stage primaryStage = new Stage();
						BookDetailsFrameController bookDetails = new BookDetailsFrameController();
						bookDetails.setBook(book);
						bookDetails.setSource("HomeFrameController");
						bookDetails.start(primaryStage);

						((Node) event.getSource()).getScene().getWindow().hide();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});

				bookBox.getChildren().addAll(imageView, bookName);
				bookBox.getStyleClass().add("book-box");
				topBooksContainer.getChildren().add(bookBox);
			}
		});
	}
}