import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private String serverIP;
    private int port;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private final Object sendLock = new Object();
    private final Object receiveLock = new Object();
    private static final int TIMEOUT = 30000; // 30 seconds timeout

    public Client(String serverIP, int port) {
        this.serverIP = serverIP;
        this.port = port;
    }

    public boolean start() {
        try {
            socket = new Socket(serverIP, port);
            socket.setSoTimeout(TIMEOUT); // Set socket timeout
            socket.setKeepAlive(true);

            // Create streams in correct order
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush(); // Flush the header immediately
            input = new ObjectInputStream(socket.getInputStream());

            System.out.println("Connection established!");
            return true;
        } catch (Exception e) {
            System.err.println("Error starting client: " + e.getMessage());
            return false;
        }
    }

    public void sendPokerInfo(PokerInfo gameInfo) {
        synchronized(sendLock) {
            try {
                if (socket == null || socket.isClosed()) {
                    throw new IOException("Socket is not connected");
                }
                System.out.println("Sending PokerInfo to server...");
                output.writeObject(gameInfo);
                output.flush();
                output.reset(); // Prevent object caching issues
                System.out.println("PokerInfo sent successfully.");
            } catch (Exception e) {
                System.err.println("Error sending PokerInfo to the server: " + e.getMessage());
                attemptReconnect();
            }
        }
    }

    public PokerInfo receivePokerInfo() {
        synchronized(receiveLock) {
            try {
                if (socket == null || socket.isClosed()) {
                    throw new IOException("Socket is not connected");
                }

                // Add explicit timeout for read operation
                socket.setSoTimeout(TIMEOUT);
                PokerInfo response = (PokerInfo) input.readObject();

                if (response == null) {
                    throw new IOException("Received null response from server");
                }

                return response;
            } catch (Exception e) {
                System.err.println("Error receiving PokerInfo from the server: " + e.getMessage());
                attemptReconnect();
                return null;
            }
        }
    }

    private void attemptReconnect() {
        try {
            close();
            if (start()) {
                System.out.println("Successfully reconnected to server");
            } else {
                System.err.println("Failed to reconnect to server");
            }
        } catch (Exception e) {
            System.err.println("Error during reconnection: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception e) {
            System.err.println("Error closing client resources: " + e.getMessage());
        } finally {
            output = null;
            input = null;
            socket = null;
        }
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }
}