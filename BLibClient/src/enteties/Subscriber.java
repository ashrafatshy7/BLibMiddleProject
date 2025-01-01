package enteties;




public class Subscriber {
	private String ID, name, phoneNumber, email;
	private int detailedSubscriptionHistory;
	
	public Subscriber(String ID, String name, String phoneNumber, String email, int detailedSubscriptionHistory) {
        this.ID = ID;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.detailedSubscriptionHistory = detailedSubscriptionHistory;
    }

    // Getters and Setters
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDetailedSubscriptionHistory() {
        return detailedSubscriptionHistory;
    }

    public void setDetailedSubscriptionHistory(int detailedSubscriptionHistory) {
        this.detailedSubscriptionHistory = detailedSubscriptionHistory;
    }

}
