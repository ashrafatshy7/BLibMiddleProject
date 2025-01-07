package gui.bounderies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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

    // Root node after loading FXML
    private Parent root;

    @FXML
    public void initialize() {
        // Initialize UI components that don't depend on 'books'
        radios = new ToggleGroup();
        nameRadio.setToggleGroup(radios);
        categoryRadio.setToggleGroup(radios);
        descriptionRadio.setToggleGroup(radios);

        // Setup listeners for search fields
        setupSearchListeners();
    }

    public SeeAllFrameController() {
        books = new ArrayList<>();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/SeeAllFrame.fxml"));
            loader.setController(this); // Bind this instance as the controller
            root = loader.load(); // Load the FXML, initializing @FXML fields
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Start method remains unchanged
    public void start(Stage primaryStage) throws Exception {
        try {
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/bounderies/SeeAllFrame.css").toExternalForm());
            primaryStage.setTitle("Search A Book");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSearchListeners() {
        searchByNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            nameRadio.setSelected(true);
            String selectedRadio = ((RadioButton) this.radios.getSelectedToggle()).getId();
            if ("nameRadio".equals(selectedRadio)) {
                searchByDescriptionTextField.setText("");
                searchByCategory.setValue("Select Category");
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
                searchByCategory.setValue("Select Category");
                searchBook(newValue);
            }
        });
    }

    private void booksResultPanel(ArrayList<Book> books) {
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
                    Stage primaryStage = new Stage();
                    BookDetailsFrameController bookDetails = new BookDetailsFrameController();
                    bookDetails.setBook(book);
                    bookDetails.start(primaryStage); // Ensure BookDetailsFrameController is similarly set up
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // Add the button to the FlowPane
            booksPanel.getChildren().add(bookButton);
        }
    }

    // Back Button Action
    @FXML
    public void backBtn(ActionEvent event) throws Exception {
        Stage primaryStage = new Stage();
        ((Node) event.getSource()).getScene().getWindow().hide();
        HomeFrameController homeFrame = new HomeFrameController();
        homeFrame.start(primaryStage); // Ensure HomeFrameController is similarly set up
    }

    private void searchBook(String text) {
        ArrayList<Book> matchingBooks = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
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
                    String[] descriptionWords = currentBook.getDescription().toLowerCase().split("\\s+"); // Split the description into words
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
        if ("nameRadio".equals(selectedRadio)) {
            searchBook(searchByNameTextField.getText());
        } else if ("categoryRadio".equals(selectedRadio)) {
            // Implement if needed
        } else if ("descriptionRadio".equals(selectedRadio)) {
            // Implement if needed
        }
    }

    public void setBooks(ArrayList<Book> books) {
        System.out.println("Received books: " + books);
        this.books = books;

        // Now that books are set, proceed to sort and populate UI
        Collections.sort(books, Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));

        // Populate categories
        Set<String> categorySet = new HashSet<>();
        for (Book book : books) {
            categorySet.add(book.getCategory());
        }
        categoryList = new ArrayList<>(categorySet);
        Collections.sort(categoryList);
        categoryList.add(0, "Select Category");
        searchByCategory.getItems().addAll(categoryList);
        searchByCategory.setValue("Select Category");

        // Populate the books panel
        if (booksPanel != null) {
            booksResultPanel(books);
        } else {
            System.err.println("booksPanel is not initialized. Check the FXML file for the correct fx:id.");
        }
    }
}
