package card;
import javax.swing.*;
import java.awt.Image;

public class Card {
    private String suit;
    private String rank;
    private String imagePath;
    private ImageIcon image;

    public Card(String suit, String rank, String imagePath) {
        this.suit = suit;
        this.rank = rank;
        this.imagePath = imagePath;
        this.image = loadAndResizeImage(imagePath, 100, 150); // Resize to 100x150 pixels
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public ImageIcon getImage() {
        return image;
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

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}