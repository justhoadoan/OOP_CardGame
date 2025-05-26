package deck;
import card.Card;
import card.CardSkin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards;
    private CardSkin skin;

    public Deck() {
        cards = new ArrayList<>();
        createNewDeck();
    }

    public Deck(CardSkin skin) {
        this.skin = skin;
        cards = new ArrayList<>();
        createNewDeck();
    }

    public void createNewDeck() {
        cards.clear();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};

        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(new Card(rank, suit, null)); // Không cần skin để tạo card
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }

    public int remainingCards() {
        return cards.size();
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setSkin(CardSkin skin) {
        this.skin = skin;
    }

    public int getRemainingCards() {
        return cards.size(); // Assuming 'cards' is the list of remaining cards in the deck
    }
}