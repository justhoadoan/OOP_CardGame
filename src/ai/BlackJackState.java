package ai;

import card.Card;
import playable.Player;

import java.util.List;

public class BlackJackState implements GameState{
    private Player player;
    private List<Card> playerHand;
    private int getCurrentBet;
    private int playerScore;
    private int dealerScore;
    private int betValue;


    public int getPlayerScore() {
        return playerScore;
    }

    public int getDealerScore() {
        return dealerScore;
    }

    public int getBetValue() {
        return betValue;
    }

    @Override
    public Player getplayer() {
        return player;
    }

    @Override
    public List<Card> getPlayerHand() {
        return playerHand;
    }

    @Override
    public int getPot() {
        return 0;
    }

    @Override
    public int getCurrentBet() {
        return getCurrentBet;
    }
}
