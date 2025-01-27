package gui.bounderies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import application.ChatClient;
import application.ClientUI;
import enteties.Book;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import message.Message;
import message.MessageType;


/**
 * Controller for handling the "See All Books" functionality.
 */
public class SeeAllFrameController {
	 /** List of books displayed. */
    private ArrayList<Book> books;

    /** ToggleGroup for search options. */
    private ToggleGroup radios;

    /** List of book categories. */
    private ArrayList<String> categoryList;

    /** Chat client instance. */
    private ChatClient chatClient;

    /** TextField for searching by book name. */
    @FXML
    private TextField searchByNameTextField;

    /** TextField for searching by book description. */
    @FXML
    private TextField searchByDescriptionTextField;

    /** ComboBox for selecting book categories. */
    @FXML
    private ComboBox<String> searchByCategory;

    /** Button to navigate back to the home screen. */
    @FXML
    private Button backButton;

    /** Panel to display books. */
    @FXML
    private FlowPane booksPanel;

    /** ScrollPane for book results. */
    @FXML
    private ScrollPane booksScrollPane;

    /** RadioButton to search by name. */
    @FXML
    private RadioButton nameRadio;

    /** RadioButton to search by category. */
    @FXML
    private RadioButton categoryRadio;

    /** RadioButton to search by description. */
    @FXML
    private RadioButton descriptionRadio;

    /** Root node after loading FXML. */
    private Parent root;

    
    /**
     * Initializes the UI components and sets up search listeners.
     */
	@FXML
	public void initialize() {

		// Setup listeners for search fields
		setupSearchListeners();
		
		radios = new ToggleGroup();
		categoryRadio.setToggleGroup(radios);
		descriptionRadio.setToggleGroup(radios);
		nameRadio.setToggleGroup(radios);

	}

	
	/**
     * Default constructor that initializes the chat client and loads FXML.
     */
	public SeeAllFrameController() {
		chatClient = ClientUI.chat.getClient();
		books = new ArrayList<>();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/SeeAllFrame.fxml"));
			loader.setController(this);
			root = loader.load();
			SeeAllFrameController controller = loader.getController();
			if (this.chatClient != null) {
				controller.setChatClient(this.chatClient);
			}
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
		this.chatClient.setSeeAllFrameController(this);
	}

	 /**
     * Starts the "See All Books" frame.
     * @param primaryStage The primary stage.
     * @throws Exception If an error occurs while loading the frame.
     */
	public void start(Stage primaryStage) throws Exception {
		try {
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/gui/bounderies/SeeAllFrame.css").toExternalForm());
			primaryStage.setTitle("Search a book");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			
			Message sendToServer = new Message(MessageType.getAllBooks);
	    	ClientUI.chat.accept(sendToServer);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
     * Configures search listeners for text fields and combo box.
     */
	private void setupSearchListeners() {
		searchByNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			nameRadio.setSelected(true);
			String selectedRadio = ((RadioButton) this.radios.getSelectedToggle()).getId();
			if ("nameRadio".equals(selectedRadio)) {
				searchByDescriptionTextField.setText("");
				searchByCategory.setValue("All Categories");
				searchBook(newValue);
			}
		});

		searchByCategory.valueProperty().addListener((observable, oldValue, newValue) -> {
			categoryRadio.setSelected(true);
			String selectedRadio = ((RadioButton) this.radios.getSelectedToggle()).getId();
			if ("categoryRadio".equals(selectedRadio)) {
				searchByNameTextField.setText("");
				searchByDescriptionTextField.setText("");
				searchBook(newValue);
			}
		});

		searchByDescriptionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			descriptionRadio.setSelected(true);
			String selectedRadio = ((RadioButton) this.radios.getSelectedToggle()).getId();
			if ("descriptionRadio".equals(selectedRadio)) {
				searchByNameTextField.setText("");
				searchByCategory.setValue("All Categories");
				searchBook(newValue);
			}
		});
	}

	/**
     * Populates the books panel with search results.
     * @param books The list of books to display.
     */
	private void booksResultPanel(ArrayList<Book> books) {
        booksPanel.getChildren().clear();

        for (Book book : books) {
            ImageView imageView = new ImageView(book.getImage());
            imageView.setFitWidth(115);
            imageView.setFitHeight(182);
            imageView.setPreserveRatio(true);

            Button bookButton = new Button(book.getTitle());
            bookButton.setMaxWidth(imageView.getFitWidth());
            bookButton.setGraphic(imageView);
            bookButton.setContentDisplay(ContentDisplay.TOP);
            bookButton.getStyleClass().add("clear-button");

            bookButton.setOnAction(event -> {
                try {
                    Stage primaryStage = new Stage();
                    BookDetailsFrameController bookDetails = new BookDetailsFrameController();
                    bookDetails.setBook(book);
                    bookDetails.setSource("SeeAllFrameController");
                    bookDetails.start(primaryStage);
                    ((Node) event.getSource()).getScene().getWindow().hide();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            booksPanel.getChildren().add(bookButton);
        }
    }

	/**
     * Handles back button action to return to the home screen.
     * @param event The action event.
     */
	@FXML
	public void backBtn(ActionEvent event) throws Exception {
		Stage primaryStage = new Stage();
		((Node) event.getSource()).getScene().getWindow().hide();
		HomeFrameController homeFrame = new HomeFrameController();
		homeFrame.start(primaryStage); // Ensure HomeFrameController is similarly set up
	}

	
	/**
     * Searches books based on the selected criteria.
     * @param text The search text.
     */
	private void searchBook(String text) {
		ArrayList<Book> matchingBooks = new ArrayList<>();
		if (text == null || text.trim().isEmpty() || text.equals("All Categories")) {
			booksResultPanel(books);
			return;
		}
		String lowerCaseText = text.toLowerCase().trim();
		String selectedRadio = ((RadioButton) radios.getSelectedToggle()).getId();
		if ("nameRadio".equals(selectedRadio)) {
			for (Book currentBook : books) {
				if (currentBook.getTitle() != null) {
					String[] words = currentBook.getTitle().toLowerCase().split(" ");
					for (String word : words) {
						if (word.startsWith(lowerCaseText)) {
							matchingBooks.add(currentBook);
							break; // Found a match, no need to check other words
						}
					}
				}
			}
		} else if ("categoryRadio".equals(selectedRadio)) {
			for (Book currentBook : books) {
				if (currentBook.getCategory() != null) {
					if (currentBook.getCategory().toLowerCase().equals(lowerCaseText)) {
						matchingBooks.add(currentBook);
					}
				}
			}
		} else if ("descriptionRadio".equals(selectedRadio)) {
			String[] searchWords = lowerCaseText.split("\\s+"); // Split by spaces

			for (Book currentBook : books) {
				if (currentBook.getDescription() != null) {
					String[] descriptionWords = currentBook.getDescription().toLowerCase().split("\\s+");
					for (String searchWord : searchWords) {
						for (String descriptionWord : descriptionWords) {
							if (descriptionWord.contains(searchWord)) { 
								matchingBooks.add(currentBook);
								break; 
							}
						}
						if (matchingBooks.contains(currentBook)) {
							break;
						}
					}
				}
			}
		}

		booksResultPanel(matchingBooks);
	}

	
	/**
     * Sets the books and updates the UI accordingly.
     * @param books The list of books.
     */
	public void setBooks(ArrayList<Book> books) {
		this.books = books;

		// Data Processing: Sorting and Categorizing
		Collections.sort(books, Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));

		// Extract unique categories
		Set<String> categorySet = new HashSet<>();
		for (Book book : books) {
			if (book.getCategory() != null) { 
				categorySet.add(book.getCategory());
			}
		}
		categoryList = new ArrayList<>(categorySet);
		Collections.sort(categoryList);
		categoryList.add(0, "All Categories"); 

		Platform.runLater(() -> {
			searchByCategory.getItems().clear(); 
			searchByCategory.getItems().addAll(categoryList);
			searchByCategory.setValue("All Categories");

			// Update Books Panel
			if (booksPanel != null) {
				booksResultPanel(books);
			} else {
				System.err.println("booksPanel is not initialized. Check the FXML file for the correct fx:id.");
			}
		});
	}
}
