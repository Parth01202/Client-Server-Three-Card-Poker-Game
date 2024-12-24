import java.io.Serializable;
import java.util.ArrayList;

public class PokerInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int anteBet;
    private int pairPlusBet;
    private int totalWinnings;
    private int gameResult;
    private boolean playing;
    private boolean playAgain;
    private ArrayList<Card> playerHand;
    private ArrayList<Card> dealerHand;

    public PokerInfo() {
        this.anteBet = 0;
        this.pairPlusBet = 0;
        this.totalWinnings = 0;
        this.playing = true;
        this.playAgain = true;
        this.gameResult = 0;
        this.playerHand = new ArrayList<>();
        this.dealerHand = new ArrayList<>();
    }

    // Getters and Setters
    public int getAnteBet() { return anteBet; }
    public void setAnteBet(int anteBet) { this.anteBet = anteBet; }

    public int getPairPlusBet() { return pairPlusBet; }
    public void setPairPlusBet(int pairPlusBet) { this.pairPlusBet = pairPlusBet; }

    public int getTotalWinnings() { return totalWinnings; }
    public void setTotalWinnings(int totalWinnings) { this.totalWinnings = totalWinnings; }

    public boolean isPlaying() { return playing; }
    public void setPlaying(boolean playing) { this.playing = playing; }

    public boolean isPlayAgain() { return playAgain; }
    public void setPlayAgain(boolean playAgain) { this.playAgain = playAgain; }

    public ArrayList<Card> getPlayerHand() { return playerHand; }
    public void setPlayerHand(ArrayList<Card> playerHand) { this.playerHand = playerHand; }

    public ArrayList<Card> getDealerHand() { return dealerHand; }
    public void setDealerHand(ArrayList<Card> dealerHand) { this.dealerHand = dealerHand; }

    public int getGameResult() { return gameResult; }
    public void setGameResult(int gameResult) { this.gameResult = gameResult; }
}
