package application;


import enteties.Librarian;
import enteties.Subscriber;
import enteties.User;
import gui.bounderies.HomeFrameController;
import gui.bounderies.IpFrameController;
import gui.bounderies.LoanFrameController;
import gui.bounderies.SeeAllFrameController;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientUI extends Application {
    public static ClientController chat; //only one instance
    public static User user;

    public static void main( String args[] ) throws Exception
       { 
            launch(args);
       } // end main

    @Override
    public void start(Stage primaryStage) throws Exception {
//    	user = null;/
 //   	user = new Subscriber("499728433", "aaa", "234567", "aaa@gmail.com");
    	user = new Librarian("123", "aaa", "234567", "aaa@gmail.com");
    	
    	IpFrameController aFrame = new IpFrameController();
    	

        aFrame.start(primaryStage);
    }


}