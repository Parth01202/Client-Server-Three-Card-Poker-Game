import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ClientGameGUI {
    @FXML
    private Label playerCardsLabel;

    @FXML
    private Label dealerCardsLabel;

    @FXML
    private Label gameStatusLabel;

    @FXML
    private Label totalWinningsLabel;

    @FXML
    private TextField anteBetField;

    @FXML
    private TextField pairPlusBetField;

    @FXML
    private Button playHandButton;

    @FXML
    private Button foldButton;

    @FXML
    private Button placeBetsButton;

    private Client clientConnection;
    private volatile boolean isGameInProgress = false;

    public void setClientConnection(Client clientConnection) {
        this.clientConnection = clientConnection;
    }

    @FXML
    public void initialize() {
        totalWinningsLabel.setText("$0.00");
        gameStatusLabel.setText("Place your bets to begin");

        // Disable play and fold buttons initially
        if (playHandButton != null) {
            playHandButton.setDisable(true);
        }
        if (foldButton != null) {
            foldButton.setDisable(true);
        }

        // Add input validation for bet fields
        if (anteBetField != null) {
            anteBetField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    anteBetField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
        }

        if (pairPlusBetField != null) {
            pairPlusBetField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    pairPlusBetField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
        }
    }

    @FXML
    public void placeBets() {
        if (clientConnection == null) {
            showError("Connection Error", "Not connected to server", "Please ensure you are connected to the server.");
            return;
        }

        try {
            if (anteBetField.getText().isEmpty()) {
                showError("Invalid Bet", "Ante bet is required", "Please enter an ante bet to play.");
                return;
            }

            int anteBet = Integer.parseInt(anteBetField.getText());
            int pairPlusBet = pairPlusBetField.getText().isEmpty() ? 0 : Integer.parseInt(pairPlusBetField.getText());

            // Validate bet amounts
            if (anteBet < 5 || anteBet > 25) {
                showError("Invalid Bet", "Invalid ante bet", "Ante bet must be between $5 and $25.");
                return;
            }

            if (pairPlusBet > 25) {
                showError("Invalid Bet", "Invalid pair plus bet", "Pair plus bet cannot exceed $25.");
                return;
            }

            PokerInfo gameInfo = new PokerInfo();
            gameInfo.setAnteBet(anteBet);
            gameInfo.setPairPlusBet(pairPlusBet);
            gameInfo.setPlaying(true);

            // Update UI state
            setGameControlsState(true);

            // Send bet information to server
            clientConnection.sendPokerInfo(gameInfo);

            // Handle server response
            PokerInfo response = clientConnection.receivePokerInfo();
            if (response != null) {
                updateGameState(response);
            } else {
                showError("Server Error", "No response from server", "Failed to receive response from server.");
                setGameControlsState(false);
            }

        } catch (NumberFormatException e) {
            showError("Invalid Input", "Invalid bet amount", "Please enter valid numbers for bets.");
        } catch (Exception e) {
            showError("Error", "Game Error", "An error occurred: " + e.getMessage());
            setGameControlsState(false);
        }
    }

    @FXML
    public void playHand() {
        if (clientConnection == null) {
            showError("Connection Error", "Not connected to server", "Please ensure you are connected to the server.");
            return;
        }

        try {
            PokerInfo gameInfo = new PokerInfo();
            gameInfo.setPlaying(true);
            gameInfo.setAnteBet(Integer.parseInt(anteBetField.getText()));

            if (!pairPlusBetField.getText().isEmpty()) {
                gameInfo.setPairPlusBet(Integer.parseInt(pairPlusBetField.getText()));
            }

            clientConnection.sendPokerInfo(gameInfo);
            gameStatusLabel.setText("Playing hand...");

            PokerInfo response = clientConnection.receivePokerInfo();
            if (response != null) {
                updateGameState(response);
            } else {
                showError("Server Error", "No response from server", "Failed to receive response from server.");
            }

        } catch (Exception e) {
            showError("Error", "Game Error", "An error occurred: " + e.getMessage());
        }
    }

    @FXML
    public void fold() {
        if (clientConnection == null) {
            showError("Connection Error", "Not connected to server", "Please ensure you are connected to the server.");
            return;
        }

        try {
            PokerInfo gameInfo = new PokerInfo();
            gameInfo.setPlaying(false);
            clientConnection.sendPokerInfo(gameInfo);

            setGameControlsState(false);
            gameStatusLabel.setText("You folded.");
            clearCards();

        } catch (Exception e) {
            showError("Error", "Game Error", "An error occurred while folding: " + e.getMessage());
        }
    }

    private void updateGameState(PokerInfo gameInfo) {
        Platform.runLater(() -> {
            // Update cards display
            if (gameInfo.getPlayerHand() != null) {
                playerCardsLabel.setText(gameInfo.getPlayerHand().toString());
            }
            if (gameInfo.getDealerHand() != null) {
                dealerCardsLabel.setText(gameInfo.getDealerHand().toString());
            }

            // Update winnings display
            totalWinningsLabel.setText(String.format("$%.2f", (double)gameInfo.getTotalWinnings()));

            // Update game result message
            String resultMessage;
            switch(gameInfo.getGameResult()) {
                case 0:
                    resultMessage = "It's a tie!";
                    break;
                case 1:
                    resultMessage = "Dealer wins!";
                    break;
                case 2:
                    resultMessage = "You win!";
                    break;
                default:
                    resultMessage = "Waiting for result...";
                    break;
            }
            gameStatusLabel.setText(resultMessage);

            // Reset game controls if game is over
            if (!gameInfo.isPlayAgain()) {
                setGameControlsState(false);
            }
        });
    }

    private void setGameControlsState(boolean gameInProgress) {
        Platform.runLater(() -> {
            anteBetField.setDisable(gameInProgress);
            pairPlusBetField.setDisable(gameInProgress);
            placeBetsButton.setDisable(gameInProgress);
            playHandButton.setDisable(!gameInProgress);
            foldButton.setDisable(!gameInProgress);
        });
    }

    private void clearCards() {
        Platform.runLater(() -> {
            playerCardsLabel.setText("-");
            dealerCardsLabel.setText("-");
        });
    }

    private void showError(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}