package gui;

import card.Card;
import card.CardSkin;
import gamemode.GameMode;
import gamemode.GraphicMode;
import gamemode.NonGraphicMode;
import games.GameType;
import games.PokerGame;
import strategy.AIStrategy;

import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class MainFrame extends javax.swing.JFrame {
    private CardLayout cardLayout;
    private JPanel viewPanel;
    private JPanel menuPanel;
    public MainFrame() {
        setTitle("Card Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        viewPanel = new JPanel(cardLayout);
        add(viewPanel, BorderLayout.CENTER);

        menuPanel = createMenuPanel();
        viewPanel.add(menuPanel, "Menu");
        cardLayout.show(viewPanel, "Menu");
        setVisible(true);
    }
    private JPanel createMenuPanel() {
        // Panel chính với hình nền
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Thêm hình nền (có thể thay bằng hình ảnh của bạn)
                g.setColor(new Color(34, 139, 34)); // Màu xanh lá (màu bàn poker)
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Tiêu đề
        JLabel titleLabel = new JLabel("Card Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(20, 0, 40, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Panel chứa các lựa chọn
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setOpaque(false); // Trong suốt để thấy hình nền
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Các thành phần giao diện
        JLabel gameLabel = new JLabel("Game Type:");
        gameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gameLabel.setForeground(Color.WHITE);
        JComboBox<String> gameCombo = new JComboBox<>(new String[]{"Poker", "Blackjack"});
        styleComboBox(gameCombo);

        JLabel modeLabel = new JLabel("Mode Type:");
        modeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        modeLabel.setForeground(Color.WHITE);
        JComboBox<String> modeCombo = new JComboBox<>(new String[]{"Graphic", "Non-Graphic"});
        styleComboBox(modeCombo);

        JLabel roleLabel = new JLabel("Role Type:");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        roleLabel.setForeground(Color.WHITE);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Server", "Client", "Offline"});
        styleComboBox(roleCombo);

        JLabel skinLabel = new JLabel("Skin Type:");
        skinLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        skinLabel.setForeground(Color.WHITE);
        JComboBox<String> skinCombo = new JComboBox<>(new String[]{"skin1", "skin2", "skin3"});
        styleComboBox(skinCombo);

        JButton startButton = new JButton("Start Game");
        styleButton(startButton);

        // Sắp xếp các thành phần
        gbc.gridx = 0;
        gbc.gridy = 0;
        optionsPanel.add(gameLabel, gbc);
        gbc.gridx = 1;
        optionsPanel.add(gameCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        optionsPanel.add(modeLabel, gbc);
        gbc.gridx = 1;
        optionsPanel.add(modeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        optionsPanel.add(roleLabel, gbc);
        gbc.gridx = 1;
        optionsPanel.add(roleCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        optionsPanel.add(skinLabel, gbc);
        gbc.gridx = 1;
        optionsPanel.add(skinCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        optionsPanel.add(startButton, gbc);

        panel.add(optionsPanel, BorderLayout.CENTER);

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

        CardSkin skin = new CardSkin(skinType);

        // Khởi tạo GameMode
        if (modeType.equals("Graphic")) {
            //gameMode = new GraphicMode(type);
        } else {
          //  gameMode = new NonGraphicMode(type);
        }



    }
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(Color.BLACK);
        comboBox.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        comboBox.setPreferredSize(new Dimension(200, 30));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(255, 165, 0)); // Màu cam
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);

        // Hiệu ứng hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(255, 140, 0)); // Màu cam đậm hơn
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(255, 165, 0)); // Màu cam ban đầu
            }
        });
    }
    public void addView(String name, View view) {
        viewPanel.add((JPanel) view, name);
    }

    public void showView(String name) {
        cardLayout.show(viewPanel, name);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
