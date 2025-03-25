package gui;

import card.Card;
import card.CardSkin;
import games.Game;
import games.GameType;
import server.Client;


import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class PokerView extends JPanel implements View{
    private JPanel mainPanel;
    private JPanel allHandsPanel;
    private JLabel publicInfoLabel;
    private JPanel actionPanel;
    private Game game;
    private Client client;
    private CardSkin cardSkin;
    private Consumer<String> actionListener;
    private Runnable raiseAction;
    private Runnable foldAction;

    public PokerView(Game game, Client client) {
        this.game = game;
        this.client = client;
        initialize();
    }
    public void setCardSkin(CardSkin cardSkin) {
        this.cardSkin = cardSkin;
    }
    @Override
    public void initialize() {
        setLayout(new BorderLayout());
        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        allHandsPanel = new JPanel();
        allHandsPanel.setLayout(new BoxLayout(allHandsPanel, BoxLayout.Y_AXIS));
        allHandsPanel.setBorder(BorderFactory.createTitledBorder("All Hands"));
        mainPanel.add(allHandsPanel, BorderLayout.NORTH);

        publicInfoLabel = new JLabel("Game State: ");
        publicInfoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        mainPanel.add(publicInfoLabel, BorderLayout.EAST);

        actionPanel = new JPanel(new FlowLayout());

        JButton raiseButton = new JButton("Raise");
        JButton foldButton = new JButton("Fold");

        actionPanel.add(raiseButton);
        actionPanel.add(foldButton);

        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        raiseButton.addActionListener(e -> {
            if (raiseAction != null) {
                raiseAction.run();
            } else {
                String raiseAmountStr = JOptionPane.showInputDialog(this, "Enter raise amount:", "Raise", JOptionPane.QUESTION_MESSAGE);
                try {
                    int raiseAmount = Integer.parseInt(raiseAmountStr);
                    if (client != null) {
                        // Use client's sendMessage directly instead of handler
                        client.sendMessage("Raise:" + client.getClientId() + ":" + raiseAmount);
                    } else if (game != null) {
                        game.playerRaise(game.getCurrentPlayer(), raiseAmount);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid raise amount", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        foldButton.addActionListener(e -> {
            if (foldAction != null) {
                foldAction.run();
            } else {
                if (client != null) {
                    client.sendMessage("Fold:" + client.getClientId());
                } else if (game != null) {
                    game.playerFold(game.getCurrentPlayer());
                }
            }
        });

    }

    @Override
    public void updateDisplay(String playerInfo, String publicState, List<Card> playerHand) {
        publicInfoLabel.setText("Game State: " + publicState);
        allHandsPanel.removeAll();


        String[] players = playerInfo.split("\n");
        for (String player : players) {
            JPanel playerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            playerPanel.add(new JLabel(player));


            if (playerHand != null && player.contains("Client-" + client.getClientId())) {
                for (Card card : playerHand) {
                    ImageIcon cardImage = new ImageIcon(cardSkin.getImagePath(card.getRank(), card.getSuit()));

                    Image image = cardImage.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);
                    JLabel cardLabel = new JLabel(new ImageIcon(image));
                    playerPanel.add(cardLabel);
                }
            }

            else if (player.contains("Client-")) {

                int numCards = countCardsInPlayerInfo(player);
                for (int i = 0; i < numCards; i++) {
                    ImageIcon cardBackImage = new ImageIcon(cardSkin.getImagePath("BACK", ""));
                    Image image = cardBackImage.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);
                    JLabel cardLabel = new JLabel(new ImageIcon(image));
                    playerPanel.add(cardLabel);
                }
            }

            allHandsPanel.add(playerPanel);
        }

        revalidate();
        repaint();
    }

    private int countCardsInPlayerInfo(String playerInfo) {

        int startIndex = playerInfo.indexOf("[");
        int endIndex = playerInfo.indexOf("]");
        if (startIndex == -1 || endIndex == -1) return 0;

        String cards = playerInfo.substring(startIndex + 1, endIndex);
        if (cards.trim().isEmpty()) return 0;
        return cards.split(",").length;
    }

    @Override
    public void setActionButtons(String button1Text, Runnable action1, String button2Text, Runnable action2) {
        raiseAction = action1;
        foldAction = action2;
    }

    @Override
    public void showResult(String result) {
        JOptionPane.showMessageDialog(this, result, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        actionPanel.setVisible(false);
    }
}
