package games;

import ai.*;
import ai.PokerHandEvaluator.HandRank;
import card.Card;
import card.CardSkin;
import deck.Deck;
import gamemode.GameMode;
import playable.*;

import input.PokerActionProcessor;

import java.util.*;
import java.util.stream.Collectors;

public class PokerGame implements Game {
    private Deck deck;
    private GameMode gameMode;
    private PlayerManager playerManager;
    private int pot;
    private int currentBet;
    private List<Playable> players;
    private Playable currentPlayer;
    private List<Card> communityCards;
    private AIStrategyFactory aiFactory;
    private CardSkin skin;
    public PokerGame(GameMode gameMode, CardSkin skin, CardSkin cardSkin) {
        this.gameMode = gameMode;
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
        allPlayerBet();
        for (Playable player : players) {
            if (player instanceof Player) {
                currentPlayer = player;
                break;
            }
        }
        broadcastState();
    }

    public void progressGame() {
        if (isGameOver()) {
            return;
        }
        // If no community cards, deal flop
        if (communityCards.isEmpty()) {
            dealCommunityCards(3);  // Deal flop
            allPlayerBet();
        }
        // Turn
        else if (communityCards.size() == 3) {
            dealCommunityCards(1);
            allPlayerBet();
        }
        // River
        else if (communityCards.size() == 4) {
            dealCommunityCards(1);
            allPlayerBet();
        }
        // Game end
        else {
            determineWinner();
            return;
        }
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
                distributePot(distributePot((AI) winner));
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
            processor.processAction(action, this, ai);
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
        int currentBalance = player.getCurrentBalance();
        int totalBetNeeded = raiseAmount - player.getCurrentBet(); // Calculate additional amount needed

        if (currentBalance >= totalBetNeeded && totalBetNeeded > 0) {
            // Update player's bet and balance
            player.addCurrentBalance(-totalBetNeeded);
            pot += totalBetNeeded;
            player.setCurrentBet(raiseAmount);
            currentBet = Math.max(currentBet, raiseAmount);

            // Update UI immediately
            broadcastState();
        }
    }

    @Override
    public void playerFold(Playable player) {player.setStatus(false);}

    @Override
    public void playerHit(Playable player) {} // nothing would be done here

    @Override
    public void playerStand(Playable player) {} // nothing would be done here

    @Override
    public void nextPlayer() {}

    @Override
    public Playable getPlayerBeforeDealer() {return null;}

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
        if (gameMode != null) {
            boolean isHumanPlayer = currentPlayer instanceof Player;
            List<Card> playerHand = null;

            if (isHumanPlayer) {
                playerHand = getPlayerHand(((Player) currentPlayer).getId());
            }

            // Create state string with current bet and pot information
            String state = getPublicState() +
                    "\nCurrent Bet: $" + currentBet +
                    "\nPot: $" + pot;

            gameMode.updateDisplay(playerHand, state, isGameOver() ? getWinner() : null);
        }
    }

    @Override
    public boolean isGameOver() {
        return countActivePlayers() == 1;
    }

    public GameType getGameType() {return GameType.POKER; }

    Playable distributePot(Playable winner) {winner.addCurrentBalance(pot);
        return winner;
    }

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


}