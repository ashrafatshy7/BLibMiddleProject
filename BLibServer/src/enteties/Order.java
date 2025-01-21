package enteties;

import java.io.Serializable;
import java.sql.Timestamp;

public class Order implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String subscriberID, barcode;
	private Timestamp requestDate;
	
	
	public Order(String subscriberID, Timestamp requestDate, String barcode) {
		this.subscriberID = subscriberID;
		this.requestDate = requestDate;
		this.barcode = barcode;
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
