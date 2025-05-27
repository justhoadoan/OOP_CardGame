package cards.card;

public class Card {
    private String rank;
    private String suit;
    private String skin;

    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public int getCardValueBlackJack() {
        String s = this.rank;
        switch (s) {
            case "2":
                return 2;
            case "3":
                return 3;
            case "4":
                return 4;
            case "5":
                return 5;
            case "6":
                return 6;
            case "7":
                return 7;
            case "8":
                return 8;
            case "9":
                return 9;
            case "10":
                return 10;
            case "Jack":
                return 11;
            case "Queen":
                return 12;
            case "King":
                return 13;
            case "Ace":
                return 1;
        }
        return 0;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getImagePath(String rank, String suit) {
        return "/cards/" + skin + "/" + rank + suit + ".png";
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}