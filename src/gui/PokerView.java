package gui;

import card.Card;
import card.CardSkin;
import games.Game;
import games.GameType;
import input.InputHandler;
import input.PokerActionProcessor;
import server.Client;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PokerView extends JPanel {
    private JPanel mainPanel;
    private JPanel allHandsPanel;
    private JLabel publicInfoLabel;
    private JPanel actionPanel;
    private Game game;
    private Client client;
    private CardSkin cardSkin;
    private InputHandler inputHandler;

    public PokerView(Game game, Client client) {
        this.game = game;
        this.client = client;
      //  this.inputHandler = new PokerActionProcessor();
        initialize();
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setCardSkin(CardSkin cardSkin) {
        this.cardSkin = cardSkin;
    }

    private void initialize() {
        setLayout(new BorderLayout());
        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        allHandsPanel = new JPanel();
        allHandsPanel.setLayout(new BoxLayout(allHandsPanel, BoxLayout.Y_AXIS));
        allHandsPanel.setBorder(BorderFactory.createTitledBorder("All Hands"));
        mainPanel.add(allHandsPanel, BorderLayout.NORTH);

        publicInfoLabel = new JLabel("Game State: ");
        publicInfoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        mainPanel.add(publicInfoLabel, BorderLayout.CENTER);

        setupActionButtons();
    }

    private void setupActionButtons() {
        actionPanel = new JPanel(new FlowLayout());
        JButton raiseButton = new JButton("Raise");
        JButton foldButton = new JButton("Fold");

        raiseButton.addActionListener(e -> handleRaise());
        foldButton.addActionListener(e -> handleFold());

        actionPanel.add(raiseButton);
        actionPanel.add(foldButton);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
    }

    private void handleRaise() {
        String amountStr = JOptionPane.showInputDialog(this, "Enter raise amount:", "Raise", JOptionPane.QUESTION_MESSAGE);
        try {
            int amount = Integer.parseInt(amountStr);
            if (client != null) {
                client.sendMessage("ACTION:Raise:" + client.getClientId() + ":" + amount);
            } else if (game != null) {
                game.playerRaise(game.getCurrentPlayer(), amount);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleFold() {
        if (client != null) {
            client.sendMessage("ACTION:Fold:" + client.getClientId());
        } else if (game != null) {
            game.playerFold(game.getCurrentPlayer());
        }
    }

    public void updateDisplay(List<Card> playerHand, String publicState, String winner) {
        if (winner != null) {
            showResult("Winner: " + winner);
            return;
        }

        allHandsPanel.removeAll();
        
        // Display player's hand if available
        if (playerHand != null && !playerHand.isEmpty()) {
            JPanel handPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            handPanel.add(new JLabel("Your Hand: "));
            
            for (Card card : playerHand) {
                if (cardSkin != null) {
                    ImageIcon cardImage = card.getCardImage();
                    if (cardImage != null) {
                        Image image = cardImage.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);
                        handPanel.add(new JLabel(new ImageIcon(image)));
                    }
                }
            }
            allHandsPanel.add(handPanel);
        }

        // Update public state
        if (publicState != null) {
            publicInfoLabel.setText(publicState);
        }

        revalidate();
        repaint();
    }

    public void showResult(String result) {
        JOptionPane.showMessageDialog(this, result, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        actionPanel.setVisible(false);
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }
}
