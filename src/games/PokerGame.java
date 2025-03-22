package games;

import card.Card;
import card.CardSkin;
import deck.Deck;
import gameaction.GameAction;
import gamemode.GameMode;
import playable.*;
import server.Client;
import server.NetworkManager;
import strategy.AIStrategy;
import strategy.InputHandler;
import strategy.PlayerStrategy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PokerGame implements Game {
    private Deck deck;
    private GameMode gameMode;
    private NetworkManager networkManager;
  private PlayerManager playerManager;
    private int pot;
    private int currentBet;
    private List<Playable> players;
    private Playable currentPlayer;
    private AIStrategy aiStrategy;
    private PlayerStrategy playerStrategy;
    private InputHandler inputHandler;
    private CardSkin skin;
    public PokerGame(GameMode gameMode, NetworkManager networkManager, CardSkin skin) {
        this.gameMode = gameMode;
        this.networkManager = networkManager;
        this.skin = skin;
        this.deck = new Deck(skin);
        this.playerManager = new PlayerManager(this);
        this.pot = 0;
        this.currentBet = 0;
        this.aiStrategy = new AIStrategy();
        this.playerStrategy = new PlayerStrategy();
        this.inputHandler = new InputHandler();
        if (gameMode != null) {
            gameMode.setGame(this);
            gameMode.setCardSkin(skin);
        }

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

        currentPlayer = playerManager.getPlayers().get(0);
        broadcastState();
        handlePlayerTurn();
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
    public List<Card> getPlayerHand(int playerId) {
        for (Playable player : players) {
            if (player.getId() == playerId) {
                return player.getHand();
            }
        }
        return Collections.emptyList();
    }


    public String getPublicState() {
        StringBuilder handsInfo = new StringBuilder();
        for (Playable player : playerManager.getPlayers()) {
            if (handsInfo.length() > 0) {
                handsInfo.append("; ");
            }
            handsInfo.append(player.getName()).append(": ");
            if (player == currentPlayer) {
                handsInfo.append(player.getHand().stream()
                        .map(card -> card.getRank() + " of " + card.getSuit())
                        .collect(Collectors.joining(", ")));
            } else {
                handsInfo.append("Hidden");
            }
        }

        return ", Pot: " + pot + ", Current Bet: " + currentBet +
                ", Hands: " + handsInfo.toString();
    }

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
        if (networkManager != null) {
            String publicState = getPublicState();
            networkManager.sendMessage("STATE:" + publicState);

            for (Playable player : playerManager.getPlayers()) {
                if (player instanceof Player) {
                    Player p = (Player) player;
                    Client client = p.getClient();
                    if (client != null) {
                        int clientId = p.getId();
                        List<Card> playerHand = getPlayerHand(clientId);
                        networkManager.sendMessageToClient
                                (clientId, "HAND:" +
                                        playerHand.stream()
                                            .map(card -> card.getRank() + " of " + card.getSuit())
                                .collect(Collectors.joining(", ")));
                    }
                }
            }
        }

        if (gameMode != null) {
            List<Card> playerHand = currentPlayer != null ? getPlayerHand(currentPlayer instanceof Player ? ((Player) currentPlayer).getId() : -1) : null;
            gameMode.updateDisplay(playerHand, getPublicState(), isGameOver() ? getWinner() : null);
        }
    }



    @Override
    public boolean isGameOver() {
        int activePlayers = 0;
        for(Playable player : players) {
            if(player.getStatus())
                activePlayers++;
        }
        return activePlayers == 1;
    }

    public GameType getGameType() {return GameType.POKER; }

    void distributePot(Player winner) {winner.addCurrentBalance(pot);}



    void placeBet(Playable player, int amount) {
        this.pot = amount;
        this.currentBet = Math.max(this.currentBet, amount);
    }

    void resetBets() {this.pot = 0; this.currentBet = 0;}
}
