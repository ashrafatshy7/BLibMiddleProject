package application;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

import gui.bounderies.ServerFrameController;

public class ServerUI extends Application {
	
	private static EchoServer server;
	
	public static void main( String args[] ) throws Exception
	   {   
		 launch(args);
	  } // end main
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// TODO Auto-generated method stub
		mysqlConnection.connectToDB();
		ServerFrameController aFrame = new ServerFrameController(); 
		 
		aFrame.start(primaryStage);
	}
	
	public static void runServer(String p)
	{
		
		int port = 0;

        try
        {
            port = Integer.parseInt(p); 

        }
        catch(Throwable t)
        {
            System.out.println("ERROR - Could not connect!");
        }
	    	
        	server = new EchoServer(port);
	        
	        try 
	        {
	        	server.listen(); //Start listening for connections
	        	
	        } 
	        catch (Exception ex) 
	        {
	          System.out.println("ERROR - Could not listen for clients!");
	        }
	}
	
	
	public static void stopServer() {
	    if (server != null) {
	        try {
	            server.close(); // Fully shuts down the server
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}

	
	public static EchoServer getServer(){
        return server;
    }
	
	@Override
    public void stop() throws Exception {
        // This method is called when the application is about to exit
        super.stop();
    }
	

}
