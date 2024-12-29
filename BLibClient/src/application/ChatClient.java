package application;


import ocsf.client.*;

import common.ChatIF;
import gui.bounderies.ClientFrameController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import enteties.Subscriber;
import enteties.Status;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
	
	
	private ClientFrameController clientFrameController;
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  public static boolean awaitResponse = false;

  //Constructors ****************************************************
  
  
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
	 
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
  }

  //Instance methods ************************************************
  
  
  public void setClientFrameController(ClientFrameController clientFrameController) {
	    this.clientFrameController = clientFrameController;
	}
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  @SuppressWarnings("unchecked")
  public void handleMessageFromServer(Object msg) {
	  awaitResponse = false;
      HashMap<String, Object> response = (HashMap<String, Object>) msg;
      
      String operation = (String) response.get("operation");
      
      Object data = response.get("data");
      
      
      if(operation.equals("getAllSubscribers")) {
    	 
    	  ArrayList<Map<String, Object>> rawRows = (ArrayList<Map<String, Object>>) data;
          // Convert each row (Map<String,Object>) to a Subscriber object
          ArrayList<Subscriber> subscribers = new ArrayList<>();
          for (Map<String, Object> row : rawRows) {
  
              Subscriber sub = new Subscriber((String)row.get("subscriber_id"),(String)row.get("subscriber_name"),(String)row.get("subscriber_phone_number"),(String)row.get("subscriber_email"),(int)row.get("detailed_subscription_history"));
              subscribers.add(sub);
          }     
          
          clientFrameController.getSubscribers(subscribers);

		

      }

  
      
      
  }



  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  
  public void handleMessageFromClientUI(String message)  
  {
    try
    {
    	openConnection();//in order to send more than one message
       	awaitResponse = true;
    	sendToServer(message);
		// wait for response
		while (awaitResponse) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    }
    catch(IOException e)
    {
    	e.printStackTrace();
      clientUI.display("Could not send message to server: Terminating client."+ e);
      quit();
    }
  }

  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
    	ClientUI.chat.accept("QUIT");
      closeConnection();
    }
    catch(IOException e) {
    	e.printStackTrace();
    }catch(Exception e) {
    	e.printStackTrace();
    }
  }
}
//End of ChatClient class
