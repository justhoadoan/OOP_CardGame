package gamemode;

import card.Card;
import card.CardSkin;
import games.Game;
import server.NetworkManager;

import java.util.List;
import java.util.Map;

public interface GameMode {
    // Core game reference
    void setGame(Game game);
    void setCardSkin(CardSkin skin);
    void updateDisplay(List<Card> playerHand, String publicState, String winner);
}
