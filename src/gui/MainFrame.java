package gui;

import card.Card;
import card.CardSkin;
import gamemode.GameMode;
import gamemode.GraphicMode;
import gamemode.NonGraphicMode;
import games.GameType;
import games.PokerGame;
import strategy.AIStrategy;


import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends javax.swing.JFrame {
    private CardLayout cardLayout;
    private JPanel viewPanel;
    private JPanel menuPanel;
    public MainFrame() {
        setTitle("Card Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        viewPanel = new JPanel(cardLayout);
        add(viewPanel, BorderLayout.CENTER);

        menuPanel = createMenuPanel();
        viewPanel.add(menuPanel, "Menu");
        cardLayout.show(viewPanel, "Menu");
        setVisible(true);
    }
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel gameLabel = new JLabel("Game Type:");
        JComboBox<String> gameCombo = new JComboBox<>(new String[]{"Poker", "Blackjack"});
        JLabel modeLabel = new JLabel("Mode Type:");
        JComboBox<String> modeCombo = new JComboBox<>(new String[]{"Graphic", "Non-Graphic"});
        JLabel roleLabel = new JLabel("Role Type:");
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Server", "Client", "Offline"});
        JLabel skinLabel = new JLabel("Skin Type:");
        JComboBox<String> skinCombo = new JComboBox<>(new String[]{"skin1", "skin2", "skin3"});
        JButton startButton = new JButton("Start Game");

        panel.add(gameLabel);
        panel.add(gameCombo);
        panel.add(modeLabel);
        panel.add(modeCombo);
        panel.add(roleLabel);
        panel.add(roleCombo);
        panel.add(skinLabel);
        panel.add(skinCombo);
        panel.add(new JLabel());
        panel.add(startButton);

        startButton.addActionListener(e -> {
            String gameType = (String) gameCombo.getSelectedItem();
            String modeType = (String) modeCombo.getSelectedItem();
            String roleType = (String) roleCombo.getSelectedItem();
            String skinType = (String) skinCombo.getSelectedItem();
            startGame(gameType, modeType, roleType, skinType);
        });

        return panel;
    }
    public void startGame(String gameType, String modeType, String roleType, String skinType) {
        GameType type = gameType.equals("Poker") ? GameType.POKER : GameType.BLACKJACK;
        GameMode gameMode;
        CardSkin skin = new CardSkin(skinType);

        // Khởi tạo GameMode
        if (modeType.equals("Graphic")) {
            //gameMode = new GraphicMode(type);
        } else {
          //  gameMode = new NonGraphicMode(type);
        }


    }

    public static void main(String[] args) {

    }
}
