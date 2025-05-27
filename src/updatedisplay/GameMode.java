package updatedisplay;

import cards.card.Card;
import games.Game;
import java.util.List;

public interface GameMode {
    // Core game reference
    void setGame(Game game);
    void setCardSkin(String skin);
    void updateDisplay(List<Card> playerHand, String publicState, String winner);
    String getGameState();
}
