package gui;

import card.CardSkin;
import gamemode.GameMode;
import games.GameType;
import server.Client;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainFrame extends javax.swing.JFrame {
    private CardLayout cardLayout;
    private JPanel viewPanel;
    private JPanel menuPanel;

    private JTextField ipField;
    private JTextField portField;

    private JPanel serverPanel;
    private JTextField serverIPField;
    private JTextField serverPortField;
    public MainFrame() {
        setTitle("Card Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(34, 139, 34));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);

        // Title
        JLabel titleLabel = new JLabel("Card Game", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        // Create components with listeners
        JPanel gameTypePanel = createLabeledComboBox("Game Type:",
                new String[]{"Poker", "Blackjack"}, e -> {
                    JComboBox<?> cb = (JComboBox<?>) e.getSource();
                    System.out.println("Selected game: " + cb.getSelectedItem());
                });

        JPanel modeTypePanel = createLabeledComboBox("Mode Type:",
                new String[]{"Graphic", "Non-Graphic"}, e -> {
                    JComboBox<?> cb = (JComboBox<?>) e.getSource();
                    System.out.println("Selected mode: " + cb.getSelectedItem());
                });

        JPanel roleTypePanel = createLabeledComboBox("Role Type:",
                new String[]{"Server", "Client", "Offline"}, e -> {
                    JComboBox<?> cb = (JComboBox<?>) e.getSource();
                    String selectedRole = (String) cb.getSelectedItem();
                    boolean isClient = "Client".equals(selectedRole);

                    // Get the server IP and port panels
                    Component[] components = panel.getComponents();
                    for (Component comp : components) {
                        if (comp instanceof JPanel) {
                            String name = ((JLabel)((JPanel)comp).getComponent(0)).getText();
                            if (name.equals("Server IP:") || name.equals("Server Port:")) {
                                comp.setVisible(isClient);
                            }
                        }
                    }
                    pack();
                });

        JPanel skinTypePanel = createLabeledComboBox("Skin Type:",
                new String[]{"Animated", "Realistic", "Traditional"}, e -> {
                    JComboBox<?> cb = (JComboBox<?>) e.getSource();
                    System.out.println("Selected skin: " + cb.getSelectedItem());
                });

        // Layout components
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(gameTypePanel, gbc);

        gbc.gridx = 1;
        panel.add(modeTypePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(roleTypePanel, gbc);

        gbc.gridx = 1;
        panel.add(skinTypePanel, gbc);

        // Server panel with black text
        serverPanel = new JPanel();
        serverPanel.setOpaque(false);
        serverPanel.setLayout(new GridBagLayout());

        // Create server IP field with listener
        serverIPField = new JTextField("localhost", 15);
        serverIPField.setForeground(Color.BLACK);
        serverIPField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void insertUpdate(DocumentEvent e) { update(); }
            private void update() {
                System.out.println("Server IP changed to: " + serverIPField.getText());
            }
        });

        // Create server port field with listener
        serverPortField = new JTextField("12345", 15);
        serverPortField.setForeground(Color.BLACK);
        serverPortField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void insertUpdate(DocumentEvent e) { update(); }
            private void update() {
                System.out.println("Server Port changed to: " + serverPortField.getText());
            }
        });

        // Add server components
        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createLabeledPanel("Server IP:", serverIPField), gbc);

        gbc.gridy = 4;
        panel.add(createLabeledPanel("Server Port:", serverPortField), gbc);

        // Initially hide server panel
        serverIPField = new JTextField("localhost", 15);
        serverIPField.setForeground(Color.BLACK);
        serverIPField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void insertUpdate(DocumentEvent e) { update(); }
            private void update() {
                System.out.println("Server IP changed to: " + serverIPField.getText());
            }
        });

        serverPortField = new JTextField("12345", 15);
        serverPortField.setForeground(Color.BLACK);
        serverPortField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void insertUpdate(DocumentEvent e) { update(); }
            private void update() {
                System.out.println("Server Port changed to: " + serverPortField.getText());
            }
        });

        // Add server components directly to main panel
        JPanel ipPanel = createLabeledPanel("Server IP:", serverIPField);
        JPanel portPanel = createLabeledPanel("Server Port:", serverPortField);

        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(ipPanel, gbc);

        gbc.gridy = 4;
        panel.add(portPanel, gbc);

        // Initially hide server components
        ipPanel.setVisible(false);
        portPanel.setVisible(false);

        // Start Button with enhanced styling
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setPreferredSize(new Dimension(200, 45));
        startButton.setBackground(new Color(255, 165, 0));
        startButton.setForeground(Color.WHITE);
        startButton.addActionListener(e -> {
            try {
                JComboBox<?> gameCombo = (JComboBox<?>) ((JPanel) gameTypePanel).getComponent(1);
                JComboBox<?> modeCombo = (JComboBox<?>) ((JPanel) modeTypePanel).getComponent(1);
                JComboBox<?> roleCombo = (JComboBox<?>) ((JPanel) roleTypePanel).getComponent(1);
                JComboBox<?> skinCombo = (JComboBox<?>) ((JPanel) skinTypePanel).getComponent(1);

                startGame(
                        (String) gameCombo.getSelectedItem(),
                        (String) modeCombo.getSelectedItem(),
                        (String) roleCombo.getSelectedItem(),
                        (String) skinCombo.getSelectedItem()
                );
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error starting game: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(startButton, gbc);

        add(panel);
        pack();
    }

    private JPanel createLabeledComboBox(String labelText, String[] options, ActionListener listener) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        label.setPreferredSize(new Dimension(100, 30));

        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(200, 30));
        comboBox.addActionListener(listener);

        panel.add(label);
        panel.add(comboBox);

        return panel;
    }

    private JPanel createLabeledPanel(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        label.setPreferredSize(new Dimension(100, 30));

        textField.setPreferredSize(new Dimension(200, 30));
        textField.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(label);
        panel.add(textField);

        return panel;
    }

    public void startGame(String gameType, String modeType, String roleType, String skinType) throws IOException {
        GameType type = gameType.equals("Poker") ? GameType.POKER : GameType.BLACKJACK;
        CardSkin skin = new CardSkin(skinType);
        GameMode gameMode = null;

        // if (modeType.equals("Graphic")) {
        //    gameMode = new GraphicMode(type);
        //} else {
       //     gameMode = new NonGraphicMode(type);
       // }

        if (roleType.equals("Client")) {
            String ip = ipField.getText();
            int port = Integer.parseInt(portField.getText());
            Client client = new Client(ip, port, gameMode, type, skin);
            client.start();
            // Add view after client connection
            if (type == GameType.POKER) {
                PokerView view = new PokerView(null, client);
                view.setCardSkin(skin);
                addView("Game", view);
                showView("Game");
        }
        // ... handle other role types
    }


    }

    public void addView(String name, View view) {
        viewPanel.add((JPanel) view, name);
    }

    public void showView(String name) {
        cardLayout.show(viewPanel, name);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame menu = new MainFrame();
            menu.setVisible(true);
        });
    }
}
