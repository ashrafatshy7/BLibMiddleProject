package enteties;

import java.io.Serializable;
import java.sql.Timestamp;
/**
 * Represents an order placed by a subscriber for a book.
 * Implements Serializable for object serialization.
 */
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The subscriber ID who placed the order. */
    private String subscriberID;

    /** The barcode of the book being ordered. */
    private String barcode;
    
    private String title, orderDate;

    /** The date and time when the order was placed. */
    private Timestamp requestDate;

    /**
     * Constructs an Order instance with all required details.
     *
     * @param subscriberID The ID of the subscriber placing the order.
     * @param requestDate The date and time the order was placed.
     * @param barcode The barcode of the ordered book.
     */
    public Order(String subscriberID, Timestamp requestDate, String barcode) {
        this.subscriberID = subscriberID;
        this.requestDate = requestDate;
        this.barcode = barcode;
    }

    /**
     * Constructs an Order instance with subscriber ID and barcode.
     *
     * @param subscriberID The ID of the subscriber placing the order.
     * @param barcode The barcode of the ordered book.
     */
    public Order(String subscriberID, String barcode) {
        this.subscriberID = subscriberID;
        this.barcode = barcode;
    }
    
    
    public Order(String orderDate, String title, boolean flag) {
		this.title = title;
		this.orderDate = orderDate;
	}
    
    
    public String getTitle() {
		return title;
	}

	public void setTitle(String bookTitle) {
		this.title = bookTitle;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

    /**
     * Retrieves the date and time the order was placed.
     *
     * @return The timestamp of the order.
     */
    public Timestamp getRequestDate() {
        return requestDate;
    }

    /**
     * Sets the date and time the order was placed.
     *
     * @param dateAndTime The timestamp to set.
     */
    public void setRequestDate(Timestamp dateAndTime) {
        this.requestDate = dateAndTime;
    }

    /**
     * Retrieves the subscriber ID.
     *
     * @return The subscriber ID.
     */
    public String getSubscriberID() {
        return subscriberID;
    }

    /**
     * Sets the subscriber ID.
     *
     * @param subscriberID The subscriber ID to set.
     */
    public void setSubscriberID(String subscriberID) {
        this.subscriberID = subscriberID;
    }

    /**
     * Retrieves the barcode of the ordered book.
     *
     * @return The barcode of the book.
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * Sets the barcode of the ordered book.
     *
     * @param barcode The barcode to set.
     */
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
