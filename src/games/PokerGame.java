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
        Card card = deck.drawCard();
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

    }

    @Override
    public List<Playable> getPlayers() {
        return List.of();
    }

    @Override
    public Playable getCurrentPlayer() {
        return null;
    }

    @Override
    public String getPlayerHand(int playerId) {
        return "";
    }

    public String getPublicState() {return "Pot: " + pot + ", Current Bet: " + currentBet;}

    @Override
    public boolean isGameOver() {
        return false;
    }

    @Override
    public void playerRaise(Playable player) {

    }

    @Override
    public void playerFold(Playable player) {

    }

    @Override
    public void playerHit(Playable player) {

    }

    @Override
    public void playerStand(Playable player) {

    }

//    public String getWinner() {
//        return null;
//    }

//    public void playerRaise(Playable player) {
//
//    }

//    public void playerFold(Playable player) {
//
//    }

//    public void playerHit(Playable player) {
//
//    }

//    public void playerStand(Playable player) {
//
//    }

    public GameMode getGameMode() {return gameMode;}

    public void setGameMode(GameMode gameMode) {this.gameMode = gameMode;}

    @Override
    public void broadcastState() {

    }

    public GameType getGameType() { return GameType.POKER; }

//    void broadcastState() {
//
//    }

//    void distributePot(Playable winner) {
//
//    }

//    void placeBet(Playable player, int amount) {
//
//    }

    void resetBets() {this.pot = 0; this.currentBet = 0;}
}
