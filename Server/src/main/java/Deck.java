import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> cards;

    public Deck() {
        newDeck();
    }

    // Initializes a new shuffled deck of 52 cards
    public void newDeck() {
        cards = new ArrayList<>();
        char[] suits = {'C', 'D', 'H', 'S'};
        for (char suit : suits) {
            for (int value = 2; value <= 14; value++) {
                cards.add(new Card(suit, value));
            }
        }
        Collections.shuffle(cards);
    }

    // Returns the size of the deck
    public int size() {
        return cards.size();
    }

    // Removes a card from the top of the deck
    public Card remove(int index) {
        return cards.remove(index);
    }
}
