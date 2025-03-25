package card;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Card {
    private String suit;
    private String rank;
    private CardSkin skin;
    private ImageView imageView;

    public Card(String rank, String suit, CardSkin skin) {
        this.suit = suit;
        this.rank = rank;
        this.skin = skin;
        String imagePath = skin.getImagePath(rank, suit);
        this.imageView = loadAndResizeImage(imagePath, 100, 150); // Resize to 100x150 pixels
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

    private ImageView loadAndResizeImage(String imagePath, int width, int height) {
        // Load the original image
        Image originalImage = new Image(imagePath);

        // Create an ImageView and set the image
        ImageView imageView = new ImageView(originalImage);

        // Resize the image
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);

        return imageView;
    }

    public ImageView getImageView() {
        return imageView;
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