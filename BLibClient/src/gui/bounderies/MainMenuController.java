package gui.bounderies;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainMenuController {

    public void openRegisterSubscriber(ActionEvent event) throws Exception {
        ((Node) event.getSource()).getScene().getWindow().hide();
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/gui/bounderies/RegisterSubscriber.fxml"));
        stage.setTitle("Register New Subscriber");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void openSubscribersTable(ActionEvent event) throws Exception {
        ((Node) event.getSource()).getScene().getWindow().hide();
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/gui/bounderies/ClientFrame.fxml"));
        stage.setTitle("Subscribers Table");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
