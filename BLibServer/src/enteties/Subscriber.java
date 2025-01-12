package enteties;



public class Subscriber extends User {
    private int detailedSubscriptionHistory;

    // Constructor
    public Subscriber(String ID, String name, String phoneNumber, String email, int detailedSubscriptionHistory) {
        super(ID, name, phoneNumber, email);
        this.detailedSubscriptionHistory = detailedSubscriptionHistory;
    }

    // Getter and Setter for detailedSubscriptionHistory
    public int getDetailedSubscriptionHistory() {
        return detailedSubscriptionHistory;
    }

    public void setDetailedSubscriptionHistory(int detailedSubscriptionHistory) {
        this.detailedSubscriptionHistory = detailedSubscriptionHistory;
    }
}