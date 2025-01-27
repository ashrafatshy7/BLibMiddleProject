package gui.bounderies;

import application.ClientController;
import application.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

/**
 * Controller for the IP Frame.
 */
public class IpFrameController {

	/** Button to initiate the connection. */
    @FXML
    private Button connectBtn;

    /** TextField to input the IP address. */
    @FXML
    private TextField ipText;

    /** TextField to input the port number. */
    @FXML
    private TextField portText;

    
    /**
     * Displays an error alert with the provided text.
     * @param errorText The error message to display.
     */
	private void showErrorAlert(String errorText) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText("Error");
		alert.setHeaderText(errorText);
		alert.showAndWait();
	}

	/**
     * Starts the IP Frame.
     * @param primaryStage The primary stage.
     * @throws Exception If an error occurs while loading the frame.
     */
	public void start(Stage primaryStage) throws Exception {

		Parent root = FXMLLoader.load(getClass().getResource("/gui/bounderies/IpFrame.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/bounderies/IpFrame.css").toExternalForm());
		primaryStage.setTitle("Connect");
		primaryStage.setScene(scene);
		primaryStage.show();

	}
	/**
     * Handles the connection action triggered by the connect button.
     * @param event The action event.
     * @throws Exception If an error occurs during the connection process.
     */
	public void connect(ActionEvent event) throws Exception {
		String ipAddress = "127.0.0.1";

		if (!isValidPort(portText.getText())) {
			showErrorAlert("port is invalid");
			return;
		}
		int port = Integer.parseInt(portText.getText());
		if (!isValidIPv4(ipAddress)) {
			showErrorAlert("IP address is invalid");
			ipText.clear();
			return;
		}
		ClientUI.chat = new ClientController(ipAddress, port);
		if (ClientUI.chat != null) {
			Stage primaryStage = new Stage();
			((Node) event.getSource()).getScene().getWindow().hide();
			HomeFrameController homePage = new HomeFrameController();
			homePage.start(primaryStage);
		} else
			showErrorAlert("client is null");
	}

	/**
     * Validates the given port number.
     * @param port The port number to validate.
     * @return True if the port number is valid, false otherwise.
     */
	private boolean isValidPort(String port) {
		if (port == null || port.isEmpty()) {
			return false;
		}

		try {
			int portNumber = Integer.parseInt(port);
			return portNumber >= 0 && portNumber <= 65535;
		} catch (NumberFormatException e) {
			// The port string is not a valid integer
			return false;
		}
	}

	/**
     * Validates the given IPv4 address.
     * @param ip The IP address to validate.
     * @return True if the IP address is valid, false otherwise.
     */
	private boolean isValidIPv4(String ip) {
		if (ip == null || ip.isEmpty()) {
			return false;
		}

		// Split into 4 parts by '.'
		String[] parts = ip.split("\\.");
		if (parts.length != 4) {
			return false;
		}

		// Check each of the 4 segments
		for (String part : parts) {
			// Ensure each part is digits-only
			if (!part.matches("\\d+")) {
				return false;
			}

			// Parse the segment as an integer and check the range
			int value = Integer.parseInt(part);
			if (value < 0 || value > 255) {
				return false;
			}
		}

		return true;
	}

}
