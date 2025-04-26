package games;

import ai.*;
import ai.PokerHandEvaluator.HandRank;
import card.Card;
import card.CardSkin;
import deck.Deck;
import gameaction.GameAction;
import gamemode.GameMode;
import playable.*;
import server.Client;
import server.NetworkManager;
import input.PokerActionProcessor;

import java.util.*;
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
    private List<Card> communityCards;
    private AIStrategyFactory aiFactory;
    private CardSkin skin;
    public PokerGame(GameMode gameMode, NetworkManager networkManager, CardSkin skin) {
        this.gameMode = gameMode;
        this.networkManager = networkManager;
        this.skin = skin;
        this.deck = new Deck(null);
        this.players = new ArrayList<>();
        this.playerManager = new PlayerManager(this);
        this.communityCards = new ArrayList<>();
        this.pot = 0;
        this.currentBet = 0;
        this.aiFactory = new AIStrategyFactory();

        if (gameMode != null) {
            gameMode.setGame(this);
            if (skin != null) {
                gameMode.setCardSkin(skin);
            }
        }

    }



    public void start() {
        initializeGame();
        dealInitialCards();
    }
    public void progressGame() {
        if (isGameOver()) {
            determineWinner();
            return;
        }

        // Deal community cards
        if (communityCards.isEmpty()) {
            // Flop

            dealCommunityCards(3);
            allPlayerBet();
        } else if (communityCards.size() == 3) {
            // Turn
            dealCommunityCards(1);
            allPlayerBet();
        } else if (communityCards.size() == 4) {
            // River
            dealCommunityCards(1);
            allPlayerBet();
        } else {
            determineWinner();
            return;
        }

        // After dealing cards, let players bet

        broadcastState();
    }
    private void initializeGame() {
        deck.reset();
        pot = 0;
        currentBet = 0;
        communityCards.clear();

        // Reset all players
        for (Playable player : players) {
            player.resetHand();
            player.setStatus(true);
        }

        currentPlayer = players.get(0);
        broadcastState(); // Initial state
    }
    private void dealInitialCards() {
        // Deal 2 cards to each player
        for (int i = 0; i < 2; i++) {
            for (Playable player : players) {
                if (player.getStatus()) {
                    Card card = deck.drawCard();
                    player.addCard(card);
                }
            }
        }
        broadcastState(); // Show dealt cards
    }


    private void dealCommunityCards(int count) {
        try {
            deck.drawCard(); // Burn card
            for (int i = 0; i < count; i++) {
                Card card = deck.drawCard();
                if (card != null) {
                    communityCards.add(card);
                }
            }
            broadcastState(); // Show community cards
        } catch (Exception e) {
            System.err.println("Error dealing community cards: " + e.getMessage());
        }
    }


    private void determineWinner() {
        Playable winner = null;
        HandRank bestRank = null;

        for (Playable player : players) {
            if (player.getStatus()) {
                HandRank currentRank = PokerHandEvaluator.evaluateHand(player.getHand());
                if (bestRank == null || currentRank.compareTo(bestRank) > 0) {
                    bestRank = currentRank;
                    winner = player;
                }
            }
        }

        if (winner != null) {
            if (winner instanceof Player) {
                distributePot((Player) winner);
            } else if (winner instanceof AI) {
                ((AI) winner).addCurrentBalance(pot);
            }
        }
    }


    public void allPlayerDraw() {
        // Check if deck has enough cards
        for (Playable player : players) {
            // Handle both Player and AI instances
            if (player.getStatus()) {
                try {
                    Card card = deck.drawCard();
                    if (card == null) {
                        System.out.println("No more cards to draw");
                        return;
                    }
                    
                    if (player instanceof Player) {
                        ((Player) player).addCard(card);
                    } else if (player instanceof AI) {
                        ((AI) player).addCard(card);
                    }
                } catch (Exception e) {
                    System.out.println("Error drawing card for player " + player.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void allPlayerBet() {
        List<Playable> activePlayers = players.stream()
                .filter(Playable::getStatus)
                .collect(Collectors.toList());

        if (activePlayers.isEmpty()) return;

        boolean bettingComplete = false;
        while (!bettingComplete) {
            bettingComplete = true;

            for (Playable player : activePlayers) {
                if (!player.getStatus()) continue;

                currentPlayer = player;

                if (player instanceof AI) {
                    handleAIAction((AI) player);
                }

                if (isGameOver()) return;

                if (player.getStatus() && player.getCurrentBet() < currentBet) {
                    bettingComplete = false;
                }
            }
        }

        for (Playable player : players) {
            player.setCurrentBet(0);
        }
        currentBet = 0;
    }

    private void handleAIAction(AI ai) {
        PokerState state = new PokerState(
                new Player(ai.getName(), ai.getId()),
                pot,
                currentBet,
                communityCards
        );

        AIStrategy strategy = aiFactory.getAIStrategy(ai.getStrategyType());
        if (strategy != null) {
            String action = strategy.getAction(state);
            PokerActionProcessor processor = new PokerActionProcessor();
            processor.processAction(action, this, null);
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
        return currentPlayer;
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
        StringBuilder state = new StringBuilder();

        // Community Cards
        state.append("Community Cards: ");
        if (communityCards.isEmpty()) {
            state.append("None");
        } else {
            state.append(communityCards.stream()
                    .map(card -> card.getRank() + " of " + card.getSuit())
                    .collect(Collectors.joining(", ")));
        }

        // Game Info
        state.append("\nPot: $").append(pot);
        state.append("\nCurrent Bet: $").append(currentBet);

        // Player Info
        state.append("\nPlayers:");
        for (Playable player : players) {
            state.append("\n- ").append(player.getName());
            if (player instanceof Player) {
                state.append(" ($").append(((Player)player).getCurrentBalance()).append(")");
            }
            state.append(player.getStatus() ? " [Active]" : " [Folded]");
        }

        return state.toString();
    }
    private boolean isActivePlayer(Playable player) {
        return player.getStatus();
    }

    @Override
    public void playerRaise(Playable player, int raiseAmount) {
        int currentBalance = 0;
        
        if (player instanceof Player) {
            Player p = (Player) player;
            currentBalance = p.getCurrentBalance();
        } else if (player instanceof AI) {
            AI ai = (AI) player;
            currentBalance = ai.getCurrentBalance();
        }
        
        if (currentBalance >= raiseAmount) {
            placeBet(player, raiseAmount);
            
            if (player instanceof Player) {
                ((Player) player).addCurrentBalance(-raiseAmount);
            } else if (player instanceof AI) {
                ((AI) player).addCurrentBalance(-raiseAmount);
            }
        }
        broadcastState();
    }

    @Override
    public void playerFold(Playable player) {player.setStatus(false);}

    @Override
    public void playerHit(Playable player) {} // nothing would be done here

    @Override
    public void playerStand(Playable player) {} // nothing would be done here

    @Override
    public String handlePlayerTurn() {

            // Delegate input handling to PokerActionProcessor
            PokerActionProcessor processor = new PokerActionProcessor();
            return processor.getPlayerAction();

    }
    public void addAIPlayer(String name, String strategyType) {
        AI ai = new AI(players.size() + 1, name);
        ai.setStrategyType(strategyType);
        addPlayer(ai);
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
            boolean isHumanPlayer = currentPlayer instanceof Player;
            List<Card> playerHand = null;

            if (isHumanPlayer) {
                playerHand = getPlayerHand(((Player) currentPlayer).getId());
            }
            if (isHumanPlayer || isGameOver()) {
                gameMode.updateDisplay(playerHand, getPublicState(), isGameOver() ? getWinner() : null);
            }
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
        this.pot += amount;
        this.currentBet = Math.max(this.currentBet, amount);
    }

    void resetBets() {
        this.pot = 0; this.currentBet = 0;
    }

    private int countActivePlayers() {
        int count = 0;
        for (Playable player : players) {
            if (player.getStatus()) {
                count++;
            }
        }
        return count;
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }

    public String getPot() {
        return String.valueOf(pot);
    }

    // Add a method to check if deck is empty

}
