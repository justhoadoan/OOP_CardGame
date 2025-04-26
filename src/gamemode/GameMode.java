package gamemode;

import card.Card;
import card.CardSkin;
import games.PokerGame;
import input.InputHandler;

import java.util.List;

public interface GameMode {
    // Core game reference
    void setGame(PokerGame game);
    void setCardSkin(CardSkin skin);
    void updateDisplay(List<Card> playerHand, String publicState, String winner);

    String getGameState();

    InputHandler getInputHandler();


}
