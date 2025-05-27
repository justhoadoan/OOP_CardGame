package updatedisplay;

import card.Card;
import card.CardSkin;
import games.Game;


import java.util.List;

public interface GameMode {
    // Core game reference
    void setGame(Game game);
    void setCardSkin(CardSkin skin);
    void updateDisplay(List<Card> playerHand, String publicState, String winner);

    String getGameState();




}
