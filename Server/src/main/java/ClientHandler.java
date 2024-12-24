import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Game game;
    private volatile boolean isRunning;
    private static final int TIMEOUT = 30000; // 30 seconds timeout
    private final Object sendLock = new Object();
    private final Object receiveLock = new Object();

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.game = new Game();
        this.isRunning = true;
        configureSocket();
    }

    private void configureSocket() {
        try {
            clientSocket.setTcpNoDelay(true);
            clientSocket.setKeepAlive(true);
            clientSocket.setSoTimeout(TIMEOUT);
        } catch (IOException e) {
            System.err.println("Error configuring socket: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // Initialize streams in correct order and flush header
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(clientSocket.getInputStream());

            System.out.println("Client handler initialized for: " + clientSocket.getInetAddress());

            while (isRunning && !clientSocket.isClosed()) {
                try {
                    // Receive game info from client
                    PokerInfo gameInfo = receiveGameInfo();
                    if (gameInfo == null) {
                        System.out.println("Received null gameInfo, client may have disconnected");
                        break;
                    }

                    System.out.println("Received game info from client: " +
                            "Ante Bet: " + gameInfo.getAnteBet() +
                            ", Pair Plus Bet: " + gameInfo.getPairPlusBet());

                    // Process the game round
                    PokerInfo response = processGameRound(gameInfo);

                    // Send response back to client
                    if (!sendGameInfo(response)) {
                        System.out.println("Failed to send response to client");
                        break;
                    }

                } catch (SocketTimeoutException e) {
                    System.out.println("Client connection timed out");
                    break;
                } catch (IOException e) {
                    System.err.println("IO Error in client handler loop: " + e.getMessage());
                    break;
                } catch (Exception e) {
                    System.err.println("Unexpected error in client handler: " + e.getMessage());
                    e.printStackTrace();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error initializing streams: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private PokerInfo receiveGameInfo() throws IOException, ClassNotFoundException {
        synchronized (receiveLock) {
            try {
                return (PokerInfo) input.readObject();
            } catch (ClassNotFoundException e) {
                System.err.println("Error deserializing PokerInfo: " + e.getMessage());
                throw e;
            }
        }
    }

    private boolean sendGameInfo(PokerInfo gameInfo) {
        synchronized (sendLock) {
            try {
                if (gameInfo == null) {
                    gameInfo = createErrorResponse();
                }
                output.writeObject(gameInfo);
                output.flush();
                output.reset(); // Prevent object caching issues
                return true;
            } catch (IOException e) {
                System.err.println("Error sending game info: " + e.getMessage());
                return false;
            }
        }
    }

    private PokerInfo processGameRound(PokerInfo gameInfo) {
        try {
            if (!gameInfo.isPlaying()) {
                // Handle fold case
                PokerInfo response = new PokerInfo();
                response.setPlayAgain(true);
                return response;
            }

            // Process the round using game logic
            return game.processRound(gameInfo);

        } catch (Exception e) {
            System.err.println("Error processing game round: " + e.getMessage());
            return createErrorResponse();
        }
    }

    private PokerInfo createErrorResponse() {
        PokerInfo errorResponse = new PokerInfo();
        errorResponse.setPlayAgain(false);
        errorResponse.setPlaying(false);
        return errorResponse;
    }

    private void cleanup() {
        System.out.println("Cleaning up client handler resources...");
        isRunning = false;

        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing output stream: " + e.getMessage());
        }

        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing input stream: " + e.getMessage());
        }

        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }

        System.out.println("Client handler cleanup completed");
    }

    public void stop() {
        isRunning = false;
        cleanup();
    }
}