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

    public int getValue() {
        switch (rank) {
            case "Ace":
                return 11;
            case "King":
            case "Queen":
            case "Jack":
            case "10":
                return 10;
            default:
                return Integer.parseInt(rank);
        }
    }
    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}