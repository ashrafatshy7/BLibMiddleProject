package application;

import enteties.Subscriber;
import enteties.User;
import gui.bounderies.IpFrameController;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientUI extends Application {
	public static ClientController chat; // only one instance
	public static User user;

	public static void main(String args[]) throws Exception {
		launch(args);
	} // end main

	@Override
	public void start(Stage primaryStage) throws Exception {
		user = new Subscriber("123456789", "aaa", "234567", "aaa@gmail.com", 5);
		IpFrameController aFrame = new IpFrameController();
		aFrame.start(primaryStage);
	}

}