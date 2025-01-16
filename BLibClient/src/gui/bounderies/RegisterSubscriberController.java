package gui.bounderies;

import java.util.ArrayList;

import application.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import message.Message;
import message.MessageType;
import enteties.Subscriber;

public class RegisterSubscriberController {

	@FXML
	private TextField readCardField;

	@FXML
	private TextField emailField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private PasswordField confirmPasswordField;

	@FXML
	private TextField usernameField;

	@FXML
	private TextField phoneField;

	@FXML
	private Label readCardError;

	@FXML
	private Label emailError;

	@FXML
	private Label passwordError;

	@FXML
	private Label confirmPasswordError;

	@FXML
	private Label usernameError;

	@FXML
	private Label phoneError;

	@FXML
	public void initialize() {
		// Add change listeners to all fields
		addFieldChangeListener(readCardField, readCardError);
		addFieldChangeListener(emailField, emailError);
		addFieldChangeListener(passwordField, passwordError);
		addFieldChangeListener(confirmPasswordField, confirmPasswordError);
		addFieldChangeListener(usernameField, usernameError);
		addFieldChangeListener(phoneField, phoneError);
	}

	private void addFieldChangeListener(TextField textField, Label errorLabel) {
		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			textField.getStyleClass().remove("invalid-border");
			errorLabel.setVisible(false);
		});
	}

	@FXML
	public void start(Stage primaryStage) throws Exception {
		Stage stage = new Stage();
		Parent root = FXMLLoader.load(getClass().getResource("/gui/bounderies/RegisterSubscriber.fxml"));
		stage.setTitle("Register New Subscriber");
		stage.setScene(new Scene(root));
		stage.show();
	}

	public void registerSubscriber(ActionEvent event) {
		String readCard = readCardField.getText().trim();
		String email = emailField.getText().trim();
		String password = passwordField.getText().trim();
		String confirmPassword = confirmPasswordField.getText().trim();
		String username = usernameField.getText().trim();
		String phone = phoneField.getText().trim();

		// Validate inputs
		if (!validateInput(readCard, email, password, confirmPassword, username, phone)) {
			return; // Stop if there are errors
		}

		// Send data to server
		ArrayList<Object> subscriber = new ArrayList<Object>();
		Subscriber sub = new Subscriber(readCard, username, phone, email);
		subscriber.add(sub);
		subscriber.add(password);
		Message sendToserver = new Message(MessageType.registerSubscriber, subscriber);
		ClientUI.chat.accept(sendToserver);
	}

	private boolean validateInput(String readCard, String email, String password, String confirmPassword,
			String username, String phone) {
		boolean isValid = true;

		// Validate ReadCard
		if (readCard.isEmpty() || !readCard.matches("\\d{9}")) {
			readCardError.setText("ReadCard must be exactly 9 digits.");
			readCardError.setVisible(true);
			applyErrorStyle(readCardField, true);
			isValid = false;
		} else {
			applyErrorStyle(readCardField, false);
		}

		// Validate Email
		if (email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
			emailError.setText("Invalid email format.");
			emailError.setVisible(true);
			applyErrorStyle(emailField, true);
			isValid = false;
		} else {
			applyErrorStyle(emailField, false);
		}

		// Validate Password
		if (password.isEmpty() || password.length() < 6 || !password.matches("[A-Za-z0-9]+")) {
			passwordError
					.setText("Password must be at least 6 characters and contain only English letters and numbers.");
			passwordError.setVisible(true);
			applyErrorStyle(passwordField, true);
			isValid = false;
		} else {
			applyErrorStyle(passwordField, false);
		}

		// Validate Confirm Password
		if (!password.equals(confirmPassword)) {
			confirmPasswordError.setText("Passwords do not match.");
			confirmPasswordError.setVisible(true);
			applyErrorStyle(confirmPasswordField, true);
			isValid = false;
		} else {
			applyErrorStyle(confirmPasswordField, false);
		}

		// Validate Username
		if (username.isEmpty() || !username.matches("[A-Za-z]+")) {
			usernameError.setText("Username must contain only English letters.");
			usernameError.setVisible(true);
			applyErrorStyle(usernameField, true);
			isValid = false;
		} else {
			applyErrorStyle(usernameField, false);
		}

		// Validate Phone Number
		if (phone.isEmpty() || !phone.matches("05\\d{8}")) {
			phoneError.setText("Phone number must start with '05' and be exactly 10 digits.");
			phoneError.setVisible(true);
			applyErrorStyle(phoneField, true);
			isValid = false;
		} else {
			applyErrorStyle(phoneField, false);
		}

		return isValid;
	}

	private void applyErrorStyle(TextField field, boolean hasError) {
		field.getStyleClass().remove("invalid-border");
		if (hasError) {
			if (!field.getStyleClass().contains("invalid-border")) {
				field.getStyleClass().add("invalid-border");
			}
		}
	}

	public void goBack(ActionEvent event) {
		Stage primaryStage = new Stage();
		((Node) event.getSource()).getScene().getWindow().hide();
		HomeFrameController homeFrameController = new HomeFrameController();
		try {
			homeFrameController.start(primaryStage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}