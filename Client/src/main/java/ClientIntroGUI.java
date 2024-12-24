import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class ClientIntroGUI {

    @FXML
    private javafx.scene.control.TextField serverIPField;
    @FXML
    private javafx.scene.control.TextField portField;

    private Client clientConnection;

    @FXML
    public void connectToServer(ActionEvent event) {
        try {
            String serverIP = serverIPField.getText();
            int port = Integer.parseInt(portField.getText());
            clientConnection = new Client(serverIP, port);

            if (clientConnection.start()) {
                // Connection successful, transition to the welcome screen
                Stage stage = (Stage) serverIPField.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/WelcomeScreen.fxml"));
                Parent root = loader.load();

                WelcomeScreenController controller = loader.getController();
                controller.setClientConnection(clientConnection);

                stage.setScene(new Scene(root, 600, 400));
                stage.setTitle("Welcome to Three Card Poker!");
                stage.show();
            } else {
                // Show an error if the server is not running
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Connection Error");
                alert.setHeaderText("Could not connect to server");
                alert.setContentText("Please check the server and try again.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }
}
