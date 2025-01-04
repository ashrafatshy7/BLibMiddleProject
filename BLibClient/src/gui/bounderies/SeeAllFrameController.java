package gui.bounderies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import enteties.Book;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SeeAllFrameController {
	private ArrayList<Book> books;
	private boolean isAllBooksVisible;

	@FXML
	private TextField searchTextField;

	@FXML
	private Button backButton;

	@FXML
	private FlowPane booksPanel;

	@FXML
	private ScrollPane booksScrollPane;

	@FXML
	public void initialize() {
		addBookSorted(new Book("123", "Alone", "aaa", "Drama", "disc", "A5 23", true, 15));
		addBookSorted(new Book("456", "Together", "bbb", "Romance", "An emotional journey of love", "B4 56", true, 20));
		addBookSorted(new Book("789", "Survivor", "ccc", "Thriller", "A tale of resilience", "C3 89", false, 12));
		addBookSorted(
				new Book("321", "Echoes", "ddd", "Mystery", "Secrets unravel in unexpected ways", "D2 34", true, 18));
		addBookSorted(new Book("654", "Horizons", "eee", "Science Fiction", "Exploring new frontiers beyond the stars",
				"E5 67", true, 22));
		addBookSorted(new Book("987", "Whispers", "fff", "Horror", "Chilling tales that haunt the night", "F6 78",
				false, 10));
		addBookSorted(new Book("159", "Legacy", "ggg", "Historical", "A journey through time and heritage", "G7 89",
				true, 16));
		addBookSorted(new Book("753", "Odyssey", "hhh", "Adventure", "An epic quest across uncharted lands", "H8 90",
				true, 25));
		addBookSorted(new Book("852", "Reflections", "iii", "Philosophy", "Deep thoughts on existence and purpose",
				"I9 12", false, 14));
		addBookSorted(new Book("951", "Phoenix", "jjj", "Fantasy", "Rising from the ashes to conquer darkness", "J1 23",
				true, 19));
		addBookSorted(new Book("357", "Mirage", "kkk", "Suspense", "Illusions that blur reality and deception", "K2 34",
				false, 11));
		addBookSorted(new Book("258", "Zenith", "lll", "Non-Fiction", "Achieving the peak of personal growth", "L3 45",
				true, 17));
		addBookSorted(new Book("147", "Cascade", "mmm", "Romantic Comedy", "Love and laughter in unexpected places",
				"M4 56", true, 20));
		addBookSorted(new Book("369", "Adventure Awaits", "nnn", "Adventure", "A thrilling adventure story", "N5 67",
				true, 13));
		addBookSorted(
				new Book("741", "Alone Again", "ooo", "Drama", "Continuing the journey of solitude", "O6 78", true, 9));
		addBookSorted(new Book("852", "Echoes of Silence", "ppp", "Mystery", "Whispers in the dark", "P7 89", true, 7));
		addBookSorted(
				new Book("963", "Survivor's Tale", "qqq", "Thriller", "Surviving against all odds", "Q8 90", false, 5));
		addBookSorted(new Book("174", "Whispers in the Wind", "rrr", "Horror", "Ghostly whispers under the moonlight",
				"R9 01", true, 8));
		addBookSorted(new Book("285", "Horizons Beyond", "sss", "Science Fiction",
				"Exploring dimensions beyond imagination", "S1 23", true, 14));
		addBookSorted(new Book("396", "Legacy of the Past", "ttt", "Historical", "Echoes from ancient times", "T2 34",
				true, 12));
		addBookSorted(new Book("507", "Cascade of Memories", "uuu", "Romantic Comedy", "Memories intertwined with love",
				"U3 45", true, 19));
		try {
			if (booksPanel != null) {
				booksResultPanel(books);
			} else {
				throw new IllegalStateException(
						"booksPanel is not initialized. Check the FXML file for the correct fx:id.");
			}
		} catch (IllegalStateException e) {
			System.err.println(e.getMessage());
		}

		searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			searchBook(newValue);
		});
	}

	public SeeAllFrameController() {
		books = new ArrayList<>();
		isAllBooksVisible = true;
	}

	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/SeeAllFrame.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/gui/bounderies/SeeAllFrame.css").toExternalForm());
			primaryStage.setTitle("Seach A Book");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void booksResultPanel(ArrayList<Book> books) {
		Stage primaryStage = new Stage();
		booksPanel.getChildren().clear();

		for (Book book : books) {
			// Create the ImageView for the book cover
			ImageView imageView = new ImageView(book.getCachedImage());
			imageView.setFitWidth(115);
			imageView.setFitHeight(182);
			imageView.setPreserveRatio(true);

			// Create a Button that combines the image and title
			Button bookButton = new Button(book.getTitle());
			bookButton.setMaxWidth(imageView.getFitWidth());
			bookButton.setGraphic(imageView);
			bookButton.setContentDisplay(ContentDisplay.TOP); // Image on top, text below
			bookButton.getStyleClass().add("clear-button");

			bookButton.setOnAction(event -> {
				try {
					((Node) event.getSource()).getScene().getWindow().hide();
					BookDetailsFrameController bookDetails = new BookDetailsFrameController();
					bookDetails.setBook(book);
					bookDetails.start(primaryStage);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

			// Add the button to the FlowPane
			booksPanel.getChildren().add(bookButton);
		}
	}

	// Back Button Action
	public void backBtn(ActionEvent event) throws Exception {
		Stage primaryStage = new Stage();
		((Node) event.getSource()).getScene().getWindow().hide();
		HomeFrameController homeFrame = new HomeFrameController();
		homeFrame.start(primaryStage);
	}

	private void addBookSorted(Book newBook) {
		if (newBook == null || newBook.getTitle() == null) {
			throw new IllegalArgumentException("Book and Book title cannot be null");
		}

		// Find the insertion point using binary search
		int insertionPoint = Collections.binarySearch(books, newBook, BOOK_TITLE_COMPARATOR);

		if (insertionPoint < 0) {
			// If not found, binarySearch returns (-(insertion point) - 1)
			insertionPoint = -insertionPoint - 1;
		} else {
			// If a book with the same title exists, decide where to insert
			// For this example, we'll insert after existing entries with the same title
			while (insertionPoint < books.size()
					&& books.get(insertionPoint).getTitle().equalsIgnoreCase(newBook.getTitle())) {
				insertionPoint++;
			}
		}

		// Insert the new book at the correct position
		books.add(insertionPoint, newBook);
	}

	private void searchBook(String text) {
		ArrayList<Book> matchingBooks = new ArrayList<>();
		if (text == null || text.trim().isEmpty()) {
			booksResultPanel(books);
			return;
		}

		String lowerCaseText = text.toLowerCase();

		// Create a dummy book with the search text as title for binary search
		Book dummyBook = new Book("", text, "", "", "", "", true, 0);

		// Find the insertion point
		int index = Collections.binarySearch(books, dummyBook, BOOK_TITLE_COMPARATOR);
		if (index < 0) {
			index = -index - 1;
		}

		// Iterate from the insertion point and collect all matching books
		while (index < books.size()) {
			Book currentBook = books.get(index);
			if (currentBook.getTitle() != null && currentBook.getTitle().toLowerCase().startsWith(lowerCaseText)) {
				matchingBooks.add(currentBook);
				index++;
			} else {
				break;
			}
		}

		booksResultPanel(matchingBooks);
	}

	private Comparator<Book> BOOK_TITLE_COMPARATOR = new Comparator<Book>() {
		@Override
		public int compare(Book b1, Book b2) {
			if (b1.getTitle() == null && b2.getTitle() == null) {
				return 0;
			}
			if (b1.getTitle() == null) {
				return -1;
			}
			if (b2.getTitle() == null) {
				return 1;
			}
			return b1.getTitle().compareToIgnoreCase(b2.getTitle());
		}
	};
}
