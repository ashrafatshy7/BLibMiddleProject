package enteties;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;

public class Book {
    
    private String barcode;
    private String title;
    private String author;
    private String category;
    private String description;
    private String shelf;
    private int availableCopies;
    private Image image;
    private List<Loan> loan;
    private List<Reservation> reservation;

    // Constructor
    public Book(String barcode, String title, String author, String category, String description,
            String shelf, int availableCopies, byte[] imageBytes) {
    this.barcode = barcode;
    this.title = title;
    this.author = author;
    this.category = category;
    this.description = description;
    this.shelf = shelf;
    this.availableCopies = availableCopies;
    this.loan = new ArrayList<>();
    this.reservation = new ArrayList<>();
    if (imageBytes != null) {
        try (InputStream is = new ByteArrayInputStream(imageBytes)) {
            this.image = new Image(is); // JavaFX image
        } catch (Exception e) {
        	loadDefaultImage();
        }
    } else {
        loadDefaultImage();
    }
}
    
    private void loadDefaultImage() {
        try {
            this.image = new Image(getClass().getResourceAsStream("../1003w-QHBKwQnsgzs.png"));
        } catch (Exception e) {
            System.err.println("Failed to load default book image: " + e.getMessage());
        }
    }
    
    public Image getImage() {
        return image;
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

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
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

    public List<Reservation> getReservation() {
        return reservation;
    }

    public void setReservation(List<Reservation> reservation) {
        this.reservation = reservation;
    }
    
    public void addReservation(Reservation reservation) {
        this.reservation.add(reservation);
    }

    // Override toString method
    @Override
    public String toString() {
        return "Book{" +
                "barcode='" + barcode + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", shelf='" + shelf + '\'' +
                ", availableCopies=" + availableCopies +
                ", loan=" + loan +
                ", reservation=" + reservation +
                '}';
    }
}
