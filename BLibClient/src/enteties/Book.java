package enteties;

import java.util.List;


public class Book {
    private String ID;
    private String title;
    private String author;
    private String subject;
    private String description;
    private String locationOnTheShelf;
    private boolean isAvailable;
    private int availableCopies;
    private List<Loan> loan;
    private List<Reservation> reservation;

    // Constructor
    public Book(String ID, String title, String author, String subject, String description,
                String locationOnTheShelf, boolean isAvailable, int availableCopies,
                List<Loan> loan, List<Reservation> reservation) {
        this.ID = ID;
        this.title = title;
        this.author = author;
        this.subject = subject;
        this.description = description;
        this.locationOnTheShelf = locationOnTheShelf;
        this.isAvailable = isAvailable;
        this.availableCopies = availableCopies;
        this.loan = loan;
        this.reservation = reservation;
    }

    // Getters and Setters
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationOnTheShelf() {
        return locationOnTheShelf;
    }

    public void setLocationOnTheShelf(String locationOnTheShelf) {
        this.locationOnTheShelf = locationOnTheShelf;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
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

    public List<Reservation> getReservation() {
        return reservation;
    }

    public void setReservation(List<Reservation> reservation) {
        this.reservation = reservation;
    }

    // Override toString method
    @Override
    public String toString() {
        return "Book{" +
                "ID='" + ID + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", locationOnTheShelf='" + locationOnTheShelf + '\'' +
                ", isAvailable=" + isAvailable +
                ", availableCopies=" + availableCopies +
                ", loan=" + loan +
                ", reservation=" + reservation +
                '}';
    }
}

