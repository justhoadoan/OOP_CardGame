package card;
import java.awt.Image;
import javax.swing.*;

public class Card {
    private String suit;
    private String rank;
    private CardSkin skin;
    private ImageIcon image;

    public Card(String rank, String suit, CardSkin skin) {
        this.suit = suit;
        this.rank = rank;
        setSkin(skin);
        String imagePath = skin.getImagePath(rank, suit);
        this.image = loadAndResizeImage(imagePath, 100, 150); // Resize to 100x150 pixels
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

    public void setSkin(CardSkin skin) {
        this.skin = skin;
    }

    private ImageIcon loadAndResizeImage(String imagePath, int width, int height) {
        // Load the original image
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image originalImage = originalIcon.getImage();

        // Resize the image
        Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        // Return the resized image as an ImageIcon
        return new ImageIcon(resizedImage);
    }

    public ImageIcon getImage() {
        return image;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}