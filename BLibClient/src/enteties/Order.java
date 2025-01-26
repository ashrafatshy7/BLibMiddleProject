package enteties;

import java.io.Serializable;
import java.sql.Timestamp;

public class Order implements Serializable {
	private static final long serialVersionUID = 1L;

	private String subscriberID, barcode, title, orderDate;
	private Timestamp requestDate;

	public Order(String subscriberID, Timestamp requestDate, String barcode) {
		this.subscriberID = subscriberID;
		this.requestDate = requestDate;
		this.barcode = barcode;
	}

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

	public Timestamp getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Timestamp dateAndTime) {
		this.requestDate = dateAndTime;
	}

	public String getSubscriberID() {
		return subscriberID;
	}

	public void setSubscriberID(String subscriberID) {
		this.subscriberID = subscriberID;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

}
