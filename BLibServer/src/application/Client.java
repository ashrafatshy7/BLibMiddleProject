package application;

/**
 * Represents a client with IP address, host name, and connection status.
 */
public class Client {
    /** The IP address of the client. */
    private String ipAddress;
    
    /** The host name of the client. */
    private String hostName;
    
    /** Indicates whether the client is currently connected. */
    private boolean isConnected;
    
    /**
     * Constructs a Client instance with the specified IP address, host name, and connection status.
     *
     * @param ipAddress   The IP address of the client.
     * @param hostName    The host name of the client.
     * @param isConnected The connection status of the client.
     */
    public Client(String ipAddress, String hostName, boolean isConnected) {
        this.ipAddress = ipAddress;
        this.hostName = hostName;
        this.isConnected = isConnected;
    }

    /**
     * Gets the IP address of the client.
     *
     * @return The IP address.
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the IP address of the client.
     *
     * @param ipAddress The new IP address.
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Gets the host name of the client.
     *
     * @return The host name.
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Sets the host name of the client.
     *
     * @param hostName The new host name.
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * Checks if the client is connected.
     *
     * @return {@code true} if the client is connected, {@code false} otherwise.
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Sets the connection status of the client.
     *
     * @param isConnected The new connection status.
     */
    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    /**
     * Returns a string representation of the client.
     *
     * @return A string containing the client's IP address, host name, and connection status.
     */
    @Override
    public String toString() {
        return "Client{" +
                "ipAddress='" + ipAddress + '\'' +
                ", hostName='" + hostName + '\'' +
                ", isConnected=" + isConnected +
                '}';
    }
}
