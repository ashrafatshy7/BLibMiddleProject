package gui.bounderies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import enteties.Book;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class SeeAllFrameController {
	private ArrayList<Book> books;
	private ToggleGroup radios;
	private ArrayList<String> categoryList;

	@FXML
	private TextField searchByNameTextField;

	@FXML
	private TextField searchByDescriptionTextField;

	@FXML
	private ComboBox<String> searchByCategory;

	@FXML
	private Button backButton;

	@FXML
	private FlowPane booksPanel;

	@FXML
	private ScrollPane booksScrollPane;

	@FXML
	private RadioButton nameRadio;

	@FXML
	private RadioButton categoryRadio;

	@FXML
	private RadioButton descriptionRadio;

	@FXML
	public void initialize() {
		// Add books to the list
		books.add(new Book("123", "Alone", "aaa", "Drama", "disc", "A5 23", true, 15));
		books.add(new Book("456", "Together", "bbb", "Romance", "An emotional journey of love", "B4 56", true, 20));
		books.add(new Book("789", "Survivor", "ccc", "Thriller", "A tale of resilience", "C3 89", false, 12));
		books.add(new Book("321", "Echoes", "ddd", "Mystery", "Secrets unravel in unexpected ways", "D2 34", true, 18));
		books.add(new Book("654", "Horizons", "eee", "Science Fiction", "Exploring new frontiers beyond the stars",
				"E5 67", true, 22));
		books.add(new Book("987", "Whispers", "fff", "Horror", "Chilling tales that haunt the night", "F6 78", false,
				10));
		books.add(new Book("159", "Legacy", "ggg", "Historical", "A journey through time and heritage", "G7 89", true,
				16));
		books.add(new Book("753", "Odyssey", "hhh", "Adventure", "An epic quest across uncharted lands", "H8 90", true,
				25));
		books.add(new Book("852", "Reflections", "iii", "Philosophy", "Deep thoughts on existence and purpose", "I9 12",
				false, 14));
		books.add(new Book("951", "Phoenix", "jjj", "Fantasy", "Rising from the ashes to conquer darkness", "J1 23",
				true, 19));
		books.add(new Book("357", "Mirage", "kkk", "Suspense", "Illusions that blur reality and deception", "K2 34",
				false, 11));
		books.add(new Book("258", "Zenith", "lll", "Non-Fiction", "Achieving the peak of personal growth", "L3 45",
				true, 17));
		books.add(new Book("147", "Cascade", "mmm", "Romantic Comedy", "Love and laughter in unexpected places",
				"M4 56", true, 20));
		books.add(new Book("369", "Adventure Awaits", "nnn", "Adventure", "A thrilling adventure story", "N5 67", true,
				13));
		books.add(
				new Book("741", "Alone Again", "ooo", "Drama", "Continuing the journey of solitude", "O6 78", true, 9));
		books.add(new Book("852", "Echoes of Silence", "ppp", "Mystery", "Whispers in the dark", "P7 89", true, 7));
		books.add(
				new Book("963", "Survivor's Tale", "qqq", "Thriller", "Surviving against all odds", "Q8 90", false, 5));
		books.add(new Book("174", "Whispers in the Wind", "rrr", "Horror", "Ghostly whispers under the moonlight",
				"R9 01", true, 8));
		books.add(new Book("285", "Horizons Beyond", "sss", "Science Fiction",
				"Exploring dimensions beyond imagination", "S1 23", true, 14));
		books.add(new Book("396", "Legacy of the Past", "ttt", "Historical", "Echoes from ancient times", "T2 34", true,
				12));
		books.add(new Book("507", "Cascade of Memories", "uuu", "Romantic Comedy", "Memories intertwined with love",
				"U3 45", true, 19));

		radios = new ToggleGroup();
		nameRadio.setToggleGroup(radios);
		categoryRadio.setToggleGroup(radios);
		descriptionRadio.setToggleGroup(radios);

		// categories
		Set<String> categorySet = new HashSet<>();
		for (Book book : books) {
			categorySet.add(book.getCategory());
		}
		categoryList = new ArrayList<>(categorySet);
		Collections.sort(categoryList);
		categoryList.add(0, "Select Category");
		searchByCategory.getItems().addAll(categoryList);
		searchByCategory.setValue("Select Category");

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

		

		searchByNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			nameRadio.setSelected(true);
			String selectedRadio = ((RadioButton) this.radios.getSelectedToggle()).getId();
			if (selectedRadio.equals("nameRadio")) {
				searchByDescriptionTextField.setText("");
				searchByCategory.setValue("Select Category");
				searchBook(newValue);
			}
		});

		searchByCategory.valueProperty().addListener((observable, oldValue, newValue) -> {
			categoryRadio.setSelected(true);
			String selectedRadio = ((RadioButton) this.radios.getSelectedToggle()).getId();
			if (selectedRadio.equals("categoryRadio")) {
				searchByNameTextField.setText("");
				searchByDescriptionTextField.setText("");
				searchBook(newValue);
			}
		});

		searchByDescriptionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			descriptionRadio.setSelected(true);
			String selectedRadio = ((RadioButton) this.radios.getSelectedToggle()).getId();
			if (selectedRadio.equals("descriptionRadio")) {
				searchByNameTextField.setText("");
				searchByCategory.setValue("Select Category");
				searchBook(newValue);
			}
		});
	}

	public SeeAllFrameController() {
		books = new ArrayList<>();
	}

	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/SeeAllFrame.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/gui/bounderies/SeeAllFrame.css").toExternalForm());
			primaryStage.setTitle("Search A Book");
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
			ImageView imageView = new ImageView(book.getImage());
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

	private void searchBook(String text) {
		ArrayList<Book> matchingBooks = new ArrayList<>();
		if (text == null || text.trim().isEmpty()) {
			booksResultPanel(books);
			return;
		}
		String lowerCaseText = text.toLowerCase().trim();
		String selectedRadio = ((RadioButton) radios.getSelectedToggle()).getId();
		if (selectedRadio.equals("nameRadio")) {
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
		}

		else if (selectedRadio.equals("categoryRadio")) {
			for (Book currentBook : books) {
				if (currentBook.getCategory() != null) {
					if (currentBook.getCategory().toLowerCase().equals(lowerCaseText)) {
						matchingBooks.add(currentBook);
					}
				}
			}
		} else if (selectedRadio.equals("descriptionRadio")) {
			String[] searchWords = lowerCaseText.split("\\s+"); // Split by spaces

			for (Book currentBook : books) {
				if (currentBook.getDescription() != null) {
					String[] descriptionWords = currentBook.getDescription().toLowerCase().split("\\s+"); // Split the
																											// description
																											// into
																											// words
					for (String searchWord : searchWords) {
						for (String descriptionWord : descriptionWords) {
							if (descriptionWord.contains(searchWord)) { // Check if the word matches partially or fully
								matchingBooks.add(currentBook);
								break; // No need to check other words in the description
							}
						}
						if (matchingBooks.contains(currentBook)) {
							break; // No need to check other search words for this book
						}
					}
				}
			}
		}

		booksResultPanel(matchingBooks);
	}

	@FXML
	public void searchBtn(ActionEvent event) throws Exception {
		String selectedRadio = ((RadioButton) radios.getSelectedToggle()).getId();
		if (selectedRadio.equals("nameRadio")) {
			searchBook(searchByNameTextField.getText());
		} else if (selectedRadio.equals("categoryRadio")) {

		} else if (selectedRadio.equals("descriptionRadio")) {

		}
	}
}
