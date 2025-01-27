package enteties;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

import javafx.scene.image.Image;

/**
 * Represents a book entity in the library system. Implements Serializable for
 * object serialization.
 */
public class Book implements Serializable {
	private static final long serialVersionUID = 1L;

	/** The barcode of the book. */
	private String barcode;

	/** The title of the book. */
	private String title;

	/** The author of the book. */
	private String author;

	/** The category of the book. */
	private String category;

	/** A brief description of the book. */
	private String description;

	/** The number of available copies of the book. */
	private int availableCopies;

	/** The byte array representation of the book's image. */
	private byte[] imageBytes;

	/** The transient JavaFX image for UI display. */
	private transient Image image;

	/** A list of shelf locations where the book is available. */
	private List<String> shelfs;

	/** A list of loans associated with the book. */
	private List<Loan> loan;

	/**
	 * Constructs a Book instance with provided details.
	 *
	 * @param barcode         The book barcode.
	 * @param title           The title of the book.
	 * @param author          The author of the book.
	 * @param category        The category of the book.
	 * @param description     The description of the book.
	 * @param availableCopies The number of available copies.
	 * @param imageBytes      The image bytes representing the book.
	 */
	public Book(String barcode, String title, String author, String category, String description, int availableCopies,
			byte[] imageBytes) {
		this.barcode = barcode;
		this.title = title;
		this.author = author;
		this.category = category;
		this.description = description;
		this.availableCopies = availableCopies;
		this.imageBytes = imageBytes;
		this.shelfs = new ArrayList<>();
		this.loan = new ArrayList<>();
		
		if (imageBytes != null) {
			try (InputStream is = new ByteArrayInputStream(imageBytes)) {
				this.image = new Image(is);
			} catch (Exception e) {
				loadDefaultImage();
			}
		} else {
			loadDefaultImage();
		}
	}

	/**
	 * Constructs a Book instance with a title only.
	 *
	 * @param title The title of the book.
	 */
	public Book(String title) {
		this.title = title;
	}

	/**
	 * Loads a default image in case the provided image cannot be loaded.
	 */
	private void loadDefaultImage() {
		try {
			this.image = new Image(getClass().getResourceAsStream("../1003w-QHBKwQnsgzs.png"));
		} catch (Exception e) {
			System.err.println("Failed to load default book image: " + e.getMessage());
		}
	}

	/**
	 * Retrieves the image of the book.
	 * 
	 * @return The book image.
	 */
	public Image getImage() {
		if (image == null && imageBytes != null) {
			try (InputStream is = new ByteArrayInputStream(imageBytes)) {
				this.image = new Image(is);
			} catch (Exception e) {
				loadDefaultImage();
			}
		}
		return image;
	}

	/**
	 * Retrieves the byte array representation of the book's image.
	 * 
	 * @return The image bytes.
	 */
	public byte[] getImageBytes() {
		return imageBytes;
	}

	/**
	 * Sets the byte array representation of the book's image.
	 * 
	 * @param imageBytes The image bytes to set.
	 */
	public void setImageBytes(byte[] imageBytes) {
		this.imageBytes = imageBytes;
		if (imageBytes != null) {
			try (InputStream is = new ByteArrayInputStream(imageBytes)) {
				this.image = new Image(is);
			} catch (Exception e) {
				loadDefaultImage();
			}
		} else {
			loadDefaultImage();
		}
	}

	// Getters and Setters
	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getAvailableCopies() {
		return availableCopies;
	}

	public void setAvailableCopies(int availableCopies) {
		this.availableCopies = availableCopies;
	}

	public List<String> getShelfs() {
		return shelfs;
	}

	public List<Loan> getLoan() {
		return loan;
	}

	public void setLoan(List<Loan> loan) {
		this.loan = loan;
	}

	public void addLoan(Loan loan) {
		this.loan.add(loan);
	}

	/**
	 * Returns a string representation of the book.
	 * 
	 * @return A formatted string containing book details.
	 */
	@Override
	public String toString() {
		return "Book{" + "barcode='" + barcode + '\'' + ", title='" + title + '\'' + ", author='" + author + '\''
				+ ", category='" + category + '\'' + ", description='" + description + '\'' + ", availableCopies="
				+ availableCopies + ", loan=" + loan + '}';
	}
}
