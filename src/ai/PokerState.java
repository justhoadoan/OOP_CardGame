package ai;

import java.util.List;
import card.Card;
import playable.Player;

public class PokerState {
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

    public Player getPlayer(){
        return player;
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }

    public List<Card> getPlayerHand(){
        return player.getHand();
    }

    public int getPot(){
        return pot;
    }

    public int getCurrentBet(){
        return currentBet;
    }
}
