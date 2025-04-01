package gamemode;

import card.Card;
import card.CardSkin;
import games.PokerGame;
import input.InputHandler;
import gui.PokerView;

import javax.swing.*;
import java.util.List;

public class GraphicMode implements GameMode {
    private PokerGame game;
    private CardSkin skin;
    private PokerView view;

    public GraphicMode(CardSkin skin) {
        this.skin = skin;
        // Tạo main frame cho game
        JFrame frame = new JFrame("Poker Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        
        // Tạo view cho poker game
        view = new PokerView(null, null);
        view.setCardSkin(skin);
        
        frame.add(view);
        frame.setVisible(true);
    }

    @Override
    public void setGame(PokerGame game) {
        this.game = game;
        if (view != null) {
            view.setGame(game);
        }
    }

    @Override
    public void setCardSkin(CardSkin skin) {
        this.skin = skin;
        if (view != null) {
            view.setCardSkin(skin);
        }
    }

    @Override
    public void updateDisplay(List<Card> playerHand, String publicState, String winner) {
        if (view != null) {
            SwingUtilities.invokeLater(() -> {
                view.updateDisplay(playerHand, publicState, winner);
            });
        }
    }

    @Override
    public InputHandler getInputHandler() {
        return view.getInputHandler();
    }
}
