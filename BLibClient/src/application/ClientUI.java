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
/**
 * ClientUI serves as the entry point for the library management application.
 * It initializes the client and manages the user session.
 */
public class ClientUI extends Application {
    /** The single instance of the ClientController handling communication. */
    public static ClientController chat;

    /** The currently logged-in user, can be a Subscriber or Librarian. */
    public static User user;

    /**
     * The main entry point for the application.
     *
     * @param args Command line arguments.
     * @throws Exception if an error occurs during application startup.
     */
    public static void main(String args[]) throws Exception {
        launch(args);
    }

    /**
     * Starts the JavaFX application.
     *
     * @param primaryStage The primary stage of the application.
     * @throws Exception if an error occurs during stage initialization.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Example user initialization
        // user = new Subscriber("815340001", "StudentUser4", "3344556677", "student4@example.com", "asd");
        user = new Librarian("123", "aaa", "234567", "aaa@gmail.com", "asd");
        
        IpFrameController aFrame = new IpFrameController();
        aFrame.start(primaryStage);
    }
}
