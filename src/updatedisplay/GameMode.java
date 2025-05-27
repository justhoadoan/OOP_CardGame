package updatedisplay;

import cards.card.Card;
import games.Game;
import java.util.List;

public abstract class GameMode {
    // Core game reference
    public void setGame(Game game) {

    }

    public void setCardSkin(String skin) {

    }

    public void updateDisplay(List<Card> playerHand, String publicState, String winner) {

    }

    String getGameState() {
        return null;
    }
}
