package gui.bounderies;

import javafx.scene.control.Label;
import java.lang.classfile.components.ClassPrinter.Node;
import java.util.ArrayList;
import java.util.List;

import application.ClientUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

    public void registerSubscriber(ActionEvent event) {
        // איפוס הודעות שגיאה
        clearErrorMessages();

        String readCard = readCardField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String username = usernameField.getText().trim();
        String phone = phoneField.getText().trim();

        // בדיקת תקינות
        boolean valid = validateInput(readCard, email, password, confirmPassword, username, phone);

        if (!valid) {
            return; // יש שגיאות, לא מבצעים את הרישום
        }

        // שליחת נתונים לשרת
        String command = String.format("register subscriber %s %s %s %s %s active 0", readCard, email, password, username, phone);

        ClientUI.chat.accept(command);
    }

    private boolean validateInput(String readCard, String email, String password, String confirmPassword, String username, String phone) {
        boolean valid = true;

        if (readCard.isEmpty() || !readCard.matches("\\d{9}")) {
            readCardError.setText("ReadCard must be exactly 9 digits.");
            valid = false;
        }
        if (email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            emailError.setText("Invalid email format.");
            valid = false;
        }
        if (password.isEmpty() || password.length() < 6 || !password.matches("[A-Za-z0-9]+")) {
            passwordError.setText("Password must be at least 6 characters and contain only English letters and numbers.");
            valid = false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordError.setText("Passwords do not match.");
            valid = false;
        }
        if (username.isEmpty() || !username.matches("[A-Za-z]+")) {
            usernameError.setText("Username must contain only English letters.");
            valid = false;
        }
        if (phone.isEmpty() || !phone.matches("05\\d{8}")) {
            phoneError.setText("Phone number must start with '05' and be exactly 10 digits.");
            valid = false;
        }

        return valid;
    }

    private void clearErrorMessages() {
        readCardError.setText("");
        emailError.setText("");
        passwordError.setText("");
        confirmPasswordError.setText("");
        usernameError.setText("");
        phoneError.setText("");
    }

    
    public void goBack(ActionEvent event) throws Exception {
        // סגירת החלון הנוכחי
        ((javafx.scene.Node) event.getSource()).getScene().getWindow().hide();

        // טעינת מסך ה-Main Menu
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bounderies/MainMenu.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        stage.setTitle("Main Menu");
        stage.setScene(scene);
        stage.show();
    }

   

 
}
