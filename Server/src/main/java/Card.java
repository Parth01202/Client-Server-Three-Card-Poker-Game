import java.io.Serializable;

public class Card implements Serializable {
    private static final long serialVersionUID = 1L;

    private char suit;
    private int value;

    public Card(char suit, int value) {
        this.suit = suit;
        this.value = value;
    }

    public char getSuit() {
        return suit;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        String valueStr;
        switch (value) {
            case 14: valueStr = "A"; break;
            case 13: valueStr = "K"; break;
            case 12: valueStr = "Q"; break;
            case 11: valueStr = "J"; break;
            default: valueStr = String.valueOf(value);
        }
        return String.valueOf(suit) + valueStr;
    }
}