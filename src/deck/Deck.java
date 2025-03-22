package deck;
import card.Card;
import card.CardSkin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards;
    private CardSkin skin;

    public Deck(CardSkin skin) {
        setSkin(skin);
        cards = new ArrayList<>();
        createDeck(skin);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("No cards left in the deck!");
        }
        return cards.removeFirst();
    }

    public int size() {
        return cards.size();
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setSkin(CardSkin skin) {
        this.skin = skin;
    }

    private void createDeck(CardSkin skin){
        String[] suits = {"Hearts", "Diamond", "Clubs", "Spades"};
        String[] ranks = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};
        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(new Card(suit, rank, skin));
            }
        }
        shuffle();
    }

    public void reset(){
        cards.clear();
        createDeck(skin);
    }
    
}