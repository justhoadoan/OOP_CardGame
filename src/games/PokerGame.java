package games;

import card.CardSkin;
import deck.Deck;
import gameaction.GameAction;
import gamemode.GameMode;
import playable.*;
import server.NetworkManager;
import strategy.AIStrategy;
import strategy.InputHandler;
import strategy.PlayerStrategy;

import java.util.List;

public class PokerGame implements Game {
    private Deck deck;
    private GameMode gameMode;
    private NetworkManager networkManager;
//  private PlayerManager playerManager;
    private int pot;
    private int currentBet;
    private List<Playable> players;
    private Playable currentPlayer;
    private AIStrategy aiStrategy;
    private PlayerStrategy playerStrategy;
    private InputHandler inputHandler;
    // create a table to save card which is dealt on table

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

        // pre-flop
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
        if (player != null && !players.contains(player)) {
            players.add(player);
        }
    }

    @Override
    public List<Playable> getPlayers() {return players;}

    @Override
    public Playable getCurrentPlayer() {return currentPlayer;}

    @Override
    public String getPlayerHand(int playerId) {
        for (Playable player : players) {
            if (player.getId() == playerId) {
                return player.getHand().toString();
            }
        }
        return "Not found";
    }

    public String getPublicState() {return "Pot: " + pot + ", Current Bet: " + currentBet;}

    @Override
    public void playerRaise(Playable player, int raiseAmount) {


    }

    @Override
    public void playerFold(Playable player) {player.setStatus(false);}

    @Override
    public void playerHit(Playable player) {} // nothing would be done here

    @Override
    public void playerStand(Playable player) {} // nothing would be done here

    @Override
    public void handlePlayerTurn() {
        if (currentPlayer == null) return;

        // Game state
        System.out.println("\n=== " + currentPlayer.getName() + "'s Turn ===");
        System.out.println("Game State: " + getPublicState());

        // Get the available actions for the current player
        List<GameAction> availableActions = playerStrategy.getAvailableActions(this);

        if (currentPlayer instanceof Player) {
            Player player = (Player) currentPlayer;
            // get input from player
            GameAction selectedAction = inputHandler.getPlayerAction(player, this, availableActions);

            // process player's action
            if (selectedAction != null) {
                // just print out the action for now
                String actionName = selectedAction.getActionName();
                if (actionName.equals("Raise")) {
                    int raiseAmount = selectedAction.getParameter();
                    playerRaise(currentPlayer, raiseAmount);
                } else if (actionName.equals("Fold")) {
                    playerFold(currentPlayer);
                } else if (actionName.equals("Hit")) {
                    playerHit(currentPlayer);
                } else if (actionName.equals("Stand")) {
                    playerStand(currentPlayer);
                }
            }
        } else if (currentPlayer instanceof AI) {
            // use AI strategy to decide action
            GameAction aiAction = aiStrategy.decidePokerAction(this, availableActions);
            String actionName = aiAction.getActionName();
            System.out.println(currentPlayer.getName() + " (AI) chooses " + actionName);
            if (actionName.equals("Raise")) {
                int raiseAmount = aiAction.getParameter();
                playerRaise(currentPlayer, raiseAmount);
            } else if (actionName.equals("Fold")) {
                playerFold(currentPlayer);
            } else if (actionName.equals("Hit")) {
                playerHit(currentPlayer);
            } else if (actionName.equals("Stand")) {
                playerStand(currentPlayer);
            }
        }
    }

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
        this.pot += amount;
        this.currentBet = Math.max(this.currentBet, amount);
    }

    void resetBets() {this.pot = 0; this.currentBet = 0;}
}
