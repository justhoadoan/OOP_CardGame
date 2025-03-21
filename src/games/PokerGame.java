package games;

import card.Card;
import card.CardSkin;
import deck.Deck;
import gamemode.GameMode;
import player.Playable;
import server.NetworkManager;
import player.Player;
import java.util.List;

public class PokerGame implements Game {
    private Deck deck;
    private GameMode gameMode;
    private NetworkManager networkManager;
//  private PlayerManager playerManager;
    private int pot;
    private int currentBet;
    private List<Playable> players;

    public PokerGame(GameMode gameMode, NetworkManager networkManager, CardSkin skin) {
        this.gameMode = gameMode;
        this.networkManager = networkManager;

    }

    public void start() {
        deck.reset();
        for (Playable player : players) {
            if (player instanceof Player) {
                ((Player) player).resetHand();
            }
        }

        for (int i = 0; i < 2; i++) {
            for (Playable player : players) {
                if (player instanceof Player) {
                    ((Player) player).addCard(deck.drawCard());
                }
            }
        }
    }

    @Override
    public void addPlayer(Playable player) {
        if (player != null) {
            players.add(player);
        }
    }

    @Override
    public List<Playable> getPlayers() {return players;}

    @Override
    public Playable getCurrentPlayer() {
        return null;
    }

    @Override
    public String getPlayerHand(int playerId) {
        for (Playable player : players) {
            if (player.getId() == playerId) {
                return player.getHand().toString();
            }
        }
        return null;
    }

    public String getPublicState() {return "Pot: " + pot + ", Current Bet: " + currentBet;}

    @Override
    public void playerRaise(Playable player) {

    }

    @Override
    public void playerFold(Playable player) {player.setStatus(false);}

    @Override
    public void playerHit(Playable player) {} // nothing would be done here

    @Override
    public void playerStand(Playable player) {} // nothing would be done here

    public String getWinner() {
        for(Playable player : players) {
            if(player.getStatus())
                return player.getName();
        }
        return null;
    }

    public GameMode getGameMode() {return gameMode;}

    public void setGameMode(GameMode gameMode) {this.gameMode = gameMode;}

    @Override
    public void broadcastState() {

    }

    public GameType getGameType() {return GameType.POKER; }

    void distributePot(Player winner) {winner.addCurrentBalance(pot);}

    void placeBet(Playable player, int amount) {
        this.pot = amount;
        this.currentBet = Math.max(this.currentBet, amount);
    }

    void resetBets() {this.pot = 0; this.currentBet = 0;}
}
