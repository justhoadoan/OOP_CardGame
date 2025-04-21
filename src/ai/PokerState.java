package ai;

import java.util.List;
import card.Card;
import playable.Player;

public class PokerState implements GameState {
    private Player player;
    private int pot;
    private int currentBet;
    private List<Card> communityCards;

    public PokerState(Player player, int pot, int currentBet, List<Card> CommunityCards){
        this.player = player;
        this.pot = pot;
        this.currentBet = currentBet;
        this.communityCards = CommunityCards;
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }


    @Override
    public Player getplayer() {
        return player;
    }

    @Override
    public List<Card> getPlayerHand(){
        return player.getHand();
    }
    @Override
    public int getPot(){
        return pot;
    }
    @Override
    public int getCurrentBet(){
        return currentBet;
    }
}
