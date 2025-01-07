package application;

import ocsf.client.*;

import common.ChatIF;
import gui.bounderies.ClientFrameController;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import enteties.Book;
import enteties.Subscriber;

/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient {
	// Instance variables **********************************************

	private ClientFrameController clientFrameController;

	/**
	 * The interface type variable. It allows the implementation of the display
	 * method in the client.
	 */
	ChatIF clientUI;
	public static boolean awaitResponse = false;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the chat client.
	 *
	 * @param host     The server to connect to.
	 * @param port     The port number to connect on.
	 * @param clientUI The interface type variable.
	 */

	public ChatClient(String host, int port, ChatIF clientUI) throws IOException {
		super(host, port); // Call the superclass constructor
		this.clientUI = clientUI;
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

        if (operation.equals("getAllSubscribers")) {
            ArrayList<Map<String, Object>> rawRows = (ArrayList<Map<String, Object>>) data;
            // Convert each row (Map<String,Object>) to a Subscriber object
            ArrayList<Subscriber> subscribers = new ArrayList<>();
            for (Map<String, Object> row : rawRows) {
                Subscriber sub = new Subscriber(
                    row.get("subscriber_id").toString(),
                    row.get("subscriber_name").toString(),
                    row.get("subscriber_phone_number").toString(),
                    row.get("subscriber_email").toString(),
                    Integer.parseInt(row.get("detailed_subscription_history").toString())
                );
                subscribers.add(sub);
            }

            clientFrameController.getSubscribers(subscribers);

        } else if (operation.equals("register subscriber")) {
            boolean success = (boolean) response.get("success");
            Platform.runLater(() -> {
                if (success) {
                    showSuccessAlert("Subscriber registered successfully!");
                } else {
                    String error = (String) response.get("error");
                    showErrorAlert(error != null ? error : "Failed to register subscriber. Please try again.");
                }
            });
        }
    }

    private void showWarningAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String message) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setHeaderText("Success");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void showErrorAlert(String message) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * This method handles all data coming from the UI
     *
     * @param message The message from the UI.
     */
    public void handleMessageFromClientUI(String message) {
        try {
            openConnection();// in order to send more than one message
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
        } catch (IOException e) {
            e.printStackTrace();
            clientUI.display("Could not send message to server: Terminating client." + e);
            quit();
        }
    }

    /**
     * This method terminates the client.
     */
    public void quit() {
        try {
            ClientUI.chat.accept("QUIT");
            closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
// End of ChatClient class
