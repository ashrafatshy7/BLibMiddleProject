package application;


import gui.bounderies.IpFrameController;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientUI extends Application {
    public static ClientController chat; //only one instance

    public static void main( String args[] ) throws Exception
       { 
            launch(args);
       } // end main

    @Override
    public void start(Stage primaryStage) throws Exception {
        IpFrameController aFrame = new IpFrameController(); 

        aFrame.start(primaryStage);
    }


}