import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class WelcomeScreenController {
    private Client clientConnection;


    public void setClientConnection(Client clientConnection) {
        this.clientConnection = clientConnection;
    }


    @FXML
    public void continueToGame(ActionEvent event) {
        try {
            // Debugging: Check if the resource path is valid
            System.out.println(getClass().getResource("/FXML/ClientGameGUI.fxml"));


            // Load the Game Screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ClientGameGUI.fxml"));
            Parent root = loader.load();


            // Pass the client connection to the Game Screen
            ClientGameGUI controller = loader.getController();
            controller.setClientConnection(clientConnection);


            // Switch to the Game Screen
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.setTitle("Three Card Poker - Game");
            stage.show();
        } catch (Exception e) {
            System.err.println("Error transitioning to Game Screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
