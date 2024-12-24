import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController {

    @FXML
    private TextField portField;

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    @FXML
    private ListView<String> logView;

    private ServerSocket serverSocket;           // Server socket for accepting clients
    private ExecutorService threadPool;          // Thread pool to handle multiple clients
    private boolean isRunning = false;           // Tracks server status

    // Start Server: Reads port number and starts the server
    public void startServer() {
        try {
            int port = Integer.parseInt(portField.getText());
            serverSocket = new ServerSocket(port);
            threadPool = Executors.newCachedThreadPool();
            isRunning = true;

            logMessage("Server started on port: " + port);

            threadPool.submit(() -> {
                while (isRunning) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        logMessage("Client connected: " + clientSocket.getInetAddress());

                        threadPool.submit(new ClientHandler(clientSocket));
                    } catch (IOException e) {
                        if (isRunning) logMessage("Error accepting client: " + e.getMessage());
                    }
                }
            });

            Platform.runLater(() -> {
                startButton.setDisable(true);
                stopButton.setDisable(false);
                portField.setDisable(true);
            });

        } catch (NumberFormatException e) {
            logMessage("Invalid port number. Please enter a valid integer.");
        } catch (IOException e) {
            logMessage("Failed to start server: " + e.getMessage());
        }
    }

    // Stop Server: Stops accepting clients and shuts down the server
    @FXML
    public void stopServer() {
        try {
            isRunning = false; // Stop the server loop
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // Close the server socket
            }
            if (threadPool != null) {
                threadPool.shutdownNow(); // Terminate all client handler threads
            }

            logMessage("Server stopped.");

            // Update UI components
            Platform.runLater(() -> {
                startButton.setDisable(false);
                stopButton.setDisable(true);
                portField.setDisable(false);
            });

        } catch (IOException e) {
            logMessage("Error stopping server: " + e.getMessage());
        }
    }

    // Log messages to the ListView (UI Thread-safe)
    private void logMessage(String message) {
        Platform.runLater(() -> logView.getItems().add(message));
    }
}
