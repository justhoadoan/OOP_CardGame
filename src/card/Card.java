package card;

import javax.swing.*;
import java.awt.*;

public class Card {
    private String rank;
    private String suit;
    private CardSkin skin;
    private ImageIcon cardImage;
    private String imagePath;

    public Card(String rank, String suit, CardSkin skin) {
        this.rank = rank;
        this.suit = suit;
        this.skin = skin;

        if (skin != null) {
            try {
                imagePath = skin.getImagePath(rank, suit);
                cardImage = new ImageIcon(imagePath);
            } catch (Exception e) {
                System.err.println("Error loading card image: " + e.getMessage());
                cardImage = null;
            }
        }
    }
    public String getImagePath() {

        return this.imagePath;
    }


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

    public CardSkin getSkin() {
        return skin;
    }

    public ImageIcon getCardImage() {

        return cardImage;
    }

    public void setSkin(CardSkin skin) {
        this.skin = skin;
        if (skin != null) {
            try {
                String imagePath = skin.getImagePath(rank, suit);
                cardImage = new ImageIcon(imagePath);
            } catch (Exception e) {
                System.err.println("Error loading card image: " + e.getMessage());
                cardImage = null;
            }
        }
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}