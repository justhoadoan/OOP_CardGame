package test;

import card.Card;
import card.CardSkin;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.Objects;


public class TestImagePath {
    public  static void main(String[] args) {
        String skinName = "basic";
        String rank = "Ace";
        String suit = "Hearts";
        Card card = new Card(rank, suit, new CardSkin(skinName));
        // Create a CardSkin object
        CardSkin cardSkin = new CardSkin(skinName);

        // Get the image path for the card
        String imagePath = cardSkin.getImagePath(rank, suit);

        // Print the image path
        System.out.println("Image Path: " + imagePath);
        // Load the image
        System.out.println("Loading image from: " + card.getImagePath());
        Class<?> clazz = TestImagePath.class;
        InputStream input=clazz.getResourceAsStream(imagePath);
        if (input == null) {
            System.out.println("Image not found: " + imagePath);
        } else {
            Image image = new Image(input);
            System.out.println("Image loaded successfully.");
        }

    }
}
