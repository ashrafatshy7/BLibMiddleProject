package application;


import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */

public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
	private List<Client> clients;
	
	
  /**
   * The default port to listen on.
   */
  //final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   * 
   */

  public EchoServer(int port) 
  {
    super(port);
    clients = Collections.synchronizedList(new ArrayList<>());
  }

  //Instance methods ************************************************
  
  public List<Client> getClients(){
      return clients;
  }
  
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  super.clientConnected(client);
      try {
          InetAddress inetAddress = client.getInetAddress();
          String ipAddress = inetAddress.getHostAddress();
          String hostName = inetAddress.getHostName();
          Client newClient = new Client(ipAddress, hostName, true);
          clients.add(newClient);
      } catch (Exception e) {
          e.printStackTrace();
      }
  }
  
  @Override
  protected void clientDisconnected(ConnectionToClient client) {
	 
	  super.clientDisconnected(client);
      try {
          InetAddress inetAddress = client.getInetAddress();
          if (inetAddress != null) {
              String ipAddress = inetAddress.getHostAddress();
              String hostName = inetAddress.getHostName();
              clients.removeIf(c -> c.getIpAddress().equals(ipAddress) && c.getHostName().equals(hostName));
              
              
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
  }


  
  
  
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   * @param 
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client) {
	    String message = (String) msg;
	    
	    if ("QUIT".equalsIgnoreCase(message.trim())) {
	        clientDisconnected(client);
	        try {
	            client.sendToClient("");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return; 
	    }
	    
	    ArrayList<String> allData = new ArrayList<>(Arrays.asList(message.split(" ")));
	    if (allData.get(0).equals("getAllValues")) {
	        String tableName = allData.get(1);

	        ArrayList<Map<String, Object>> tableData = mysqlConnection.getAllValues(tableName);
	        HashMap<String, Object> response = new HashMap<>();
	        response.put("operation", "getAllSubscribers");
	        response.put("data", tableData);

	        try {
	            client.sendToClient(response);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    else if(allData.get(0).equals("update")) {
	    	ArrayList<String> updatedData = new ArrayList<String>();
	    	for(int i=1; i<allData.size(); i++) {
	    		if(allData.get(1).equals("subscribers") && allData.get(i).equals("phoneNumber")) 
	    			updatedData.add("subscriber_phone_number");
	    		else if(allData.get(1).equals("subscribers") && allData.get(i).equals("email"))
	    			updatedData.add("subscriber_email");
	    		else updatedData.add(allData.get(i));
	    	}
            mysqlConnection.updateValues(updatedData);
            ArrayList<Map<String, Object>> tableData = mysqlConnection.getAllValues(updatedData.get(0));
	        
	        try {
	        	HashMap<String, Object> response = new HashMap<>();
		        response.put("operation", "getAllSubscribers");
		        response.put("data", tableData);
	            client.sendToClient(response);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
  


  
  
  /**
   * This method retrieves the actual IP address of the machine.
   * It skips loopback addresses (e.g., 127.0.0.1) and focuses on
   * site-local IPv4 addresses (e.g., 192.168.x.x or 10.x.x.x).
   * 
   * @return The actual IP address of the machine as a String, or 
   *         "Unable to determine IP address" if no valid IP address is found.
   */
 
  
   
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println ("Server listening for connections on port: " + getPort());


  }
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()  {
    System.out.println ("Server has stopped listening for connections.");
  }  
}
//End of EchoServer class
