import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class JavaFXTemplate extends Application {


	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ClientIntroGUI.fxml"));
			Parent root = loader.load();


			Scene scene = new Scene(root, 600, 400);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Three Card Poker Client");
			primaryStage.show();
		} catch (Exception e) {
			System.err.println("Error loading the ClientIntroGUI: " + e.getMessage());
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		launch(args);
	}
}
