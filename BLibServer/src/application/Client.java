package application;

public class Client {
    private String ipAddress;
    private String hostName;
    private boolean isConnected;

    public Client(String ipAddress, String hostName, boolean isConnected) {
        this.ipAddress = ipAddress;
        this.hostName = hostName;
        this.isConnected = isConnected;
    }

    // Getters and Setters
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
    }

    public String getHostName(){
        return hostName;
    }

    public void setHostName(String hostName){
        this.hostName = hostName;
    }

    public boolean isConnected(){
        return isConnected;
    }

    public void setConnected(boolean isConnected){
        this.isConnected = isConnected;
    }

    @Override
    public String toString() {
        return "Client{" +
                "ipAddress='" + ipAddress + '\'' +
                ", hostName='" + hostName + '\'' +
                ", isConnected=" + isConnected +
                '}';
    }
}
