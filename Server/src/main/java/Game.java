import java.util.ArrayList;

public class Game {
    private Deck deck;

    public Game() {
        deck = new Deck();
    }

    public PokerInfo processRound(PokerInfo gameInfo) {
        if (gameInfo.isPlaying()) {
            // Deal hands
            if (deck.size() <= 34) {
                deck.newDeck(); // Reshuffle deck if low on cards
            }
            ArrayList<Card> playerHand = new ArrayList<>();
            ArrayList<Card> dealerHand = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                playerHand.add(deck.remove(0));
                dealerHand.add(deck.remove(0));
            }

            // Set hands in gameInfo
            gameInfo.setPlayerHand(playerHand);
            gameInfo.setDealerHand(dealerHand);

            // Compare hands and update game result
            int result = ThreeCardLogic.compareHands(dealerHand, playerHand);
            gameInfo.setGameResult(result);

            // Update winnings
            if (result == 2) { // Player wins
                int anteWinnings = gameInfo.getAnteBet() * 2;
                int pairPlusWinnings = ThreeCardLogic.evalPPWinnings(playerHand, gameInfo.getPairPlusBet());
                gameInfo.setTotalWinnings(anteWinnings + pairPlusWinnings);
            } else { // Player loses or ties
                gameInfo.setTotalWinnings(0);
            }
        }
        return gameInfo;
    }
}
