package games;

import card.Card;
import deck.Deck;
import player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class WarGameGUI extends JFrame {
    private Deck deck;
    private Player player1;
    private Player player2;

    private JLabel player1Label;
    private JLabel player2Label;
    private JLabel resultLabel;
    private JButton playButton;

    public WarGameGUI() {
        // Initialize the game
        initializeGame();

        // Set up the GUI
        setTitle("War Card Game");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create components
        player1Label = new JLabel("Player 1", SwingConstants.CENTER);
        player2Label = new JLabel("Player 2", SwingConstants.CENTER);
        resultLabel = new JLabel("Press 'Play Round' to start!", SwingConstants.CENTER);
        playButton = new JButton("Play Round");

        // Set font and alignment for better appearance
        player1Label.setFont(new Font("Arial", Font.BOLD, 14));
        player2Label.setFont(new Font("Arial", Font.BOLD, 14));
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Add components to the frame
        JPanel cardPanel = new JPanel(new GridLayout(2, 1, 10, 10)); // Add spacing between components
        cardPanel.add(player1Label);
        cardPanel.add(player2Label);

        add(cardPanel, BorderLayout.CENTER);
        add(resultLabel, BorderLayout.NORTH);
        add(playButton, BorderLayout.SOUTH);

        // Add action listener to the button
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playRound();
            }
        });
    }

    private void initializeGame() {
        deck = new Deck();
        deck.shuffle();

        player1 = new Player("Player 1");
        player2 = new Player("Player 2");

        // Deal cards to players
        while (deck.size() >= 2) {
            player1.addCard(deck.drawCard());
            player2.addCard(deck.drawCard());
        }
    }

    private void playRound() {
        if (player1.getHandSize() > 0 && player2.getHandSize() > 0) {
            // Players draw cards
            Card card1 = player1.playCard();
            Card card2 = player2.playCard();

            // Update GUI with the cards played
            player1Label.setIcon(card1.getImage());
            player2Label.setIcon(card2.getImage());

            // Compare cards and determine the winner
            int result = compareCards(card1, card2);
            if (result > 0) {
                resultLabel.setText("Player 1 wins the round!");
                player1.addCard(card1);
                player1.addCard(card2);
            } else if (result < 0) {
                resultLabel.setText("Player 2 wins the round!");
                player2.addCard(card1);
                player2.addCard(card2);
            } else {
                resultLabel.setText("It's a tie! Cards are returned to the deck.");
                deck.shuffle();
                deck.cards.add(card1);
                deck.cards.add(card2);
            }

            // Check if the game is over
            if (player1.getHandSize() == 0) {
                resultLabel.setText("Player 2 wins the game!");
                playButton.setEnabled(false);
            } else if (player2.getHandSize() == 0) {
                resultLabel.setText("Player 1 wins the game!");
                playButton.setEnabled(false);
            }
        } else {
            resultLabel.setText("Game over! No cards left.");
            playButton.setEnabled(false);
        }
    }

    private int compareCards(Card card1, Card card2) {
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        int rank1 = getRankIndex(card1.getRank(), ranks);
        int rank2 = getRankIndex(card2.getRank(), ranks);
        return Integer.compare(rank1, rank2);
    }

    private int getRankIndex(String rank, String[] ranks) {
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i].equals(rank)) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                WarGameGUI gui = new WarGameGUI();
                gui.setVisible(true);
            }
        });
    }
}