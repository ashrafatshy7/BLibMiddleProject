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

import enteties.User;
import message.Message;
import message.MessageType;
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
	  
	    
	    try {
	        Message message = (Message) msg;
	        MessageType messageType = message.getMessageType();
	        Message messageFromServer = null;
	        System.out.println("Message arrived to server: " + messageType);
	    
	        switch (messageType) {
	        case login:
	        	User user = mysqlConnection.login(message.getMessageData());
	        	messageFromServer = new Message(MessageType.login, user);
                client.sendToClient(messageFromServer);
	        	break;
	        	
	        case Return:
	        	boolean isSuccess= mysqlConnection.Return(message.getMessageData());
	        	messageFromServer = new Message(MessageType.Return, isSuccess);
                client.sendToClient(messageFromServer);
	        	break;
	        	
	        }
	    
	    
	    
	    } catch (IOException e) {
	        System.err.println("IOException while handling message: " + e.getMessage());
	        e.printStackTrace();
	    } catch (Exception e) {
	        System.err.println("Unexpected exception while handling message: " + e.getMessage());
	        e.printStackTrace();
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
