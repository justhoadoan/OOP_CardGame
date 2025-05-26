package games;

import ai.*;
import ai.PokerHandEvaluator.HandRank;
import card.Card;
import card.CardSkin;
import deck.Deck;
import gamemode.GameMode;
import playable.*;

import processor.PokerActionProcessor;

import java.util.*;
import java.util.stream.Collectors;

public class PokerGame implements Game {
   // private final List<GameEventListener> listeners = new ArrayList<>();
    private Deck deck;
    private GameMode gameMode;
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
        this.deck = new Deck();
        this.players = new ArrayList<>();
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
        for (Playable player : players) {
            if (player instanceof Player) {
                currentPlayer = player;
                break;
            }
        }
        broadcastState();
    }
    public void progressGame() {
        // Check for game over first
        if (isGameOver()) {
            determineWinner();
            return;
        }

        // Check if only one player remains active
        if (countActivePlayers() == 1) {
            determineWinner();
            return;
        }

        // Check if all active players have made their bets
        if (checkAllPlayersMatchBet()) {
            // Reset betting status for next round
            for (Playable player : players) {
                player.setHasBet(false);
                player.setCurrentBet(0);
            }
            currentBet = 0;

            // Deal next round of community cards
            if (communityCards.isEmpty()) {
                dealCommunityCards(3);  // Flop
            } else if (communityCards.size() == 3) {
                dealCommunityCards(1);  // Turn
            } else if (communityCards.size() == 4) {
                dealCommunityCards(1);  // River
            } else {
                determineWinner();
                return;
            }

            // Set current player to first active player
            for (Playable player : players) {
                if (player.getStatus()) {
                    currentPlayer = player;
                    break;
                }
            }
        } else {
            // Move to next active player
            moveToNextActivePlayer();
        }

        broadcastState();
    }

    private void moveToNextActivePlayer() {
        int currentIndex = players.indexOf(currentPlayer);
        int nextIndex = (currentIndex + 1) % players.size();

        while (nextIndex != currentIndex) {
            Playable nextPlayer = players.get(nextIndex);
            if (nextPlayer.getStatus() && !nextPlayer.getHasBet()) {
                currentPlayer = nextPlayer;
                if (currentPlayer instanceof AI) {
                    handleAIAction((AI) currentPlayer);
                }
                break;
            }
            nextIndex = (nextIndex + 1) % players.size();
        }
    }

    private boolean checkAllPlayersMatchBet() {
        // Get the number of active players who have bet
        long activePlayersBetCount = players.stream()
                .filter(p -> p.getStatus() && p.getHasBet())
                .count();

        // Get total number of active players
        long totalActivePlayers = players.stream()
                .filter(Playable::getStatus)
                .count();

        // All active players have bet if counts match
        return activePlayersBetCount == totalActivePlayers;
    }
    private void initializeGame() {
        deck.createNewDeck();
        pot = 0;
        currentBet = 0;
        communityCards.clear();

        // Reset all players
        for (Playable player : players) {
            player.resetHand();
            player.setStatus(true);
            player.setHasBet(false); // Reset betting status
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
        try {
            // If only one player is active, they automatically win
            if (countActivePlayers() == 1) {
                Playable winner = players.stream()
                        .filter(Playable::getStatus)
                        .findFirst()
                        .orElse(null);
                if (winner != null) {
                    // Distribute pot to winner
                    winner.addCurrentBalance(pot);
                    pot = 0;

                    // Set all other players' status to false
                    for (Playable player : players) {
                        if (player != winner) {
                            player.setStatus(false);
                        }
                    }
                    // Keep winner's status as true
                    broadcastState();
                    return;
                }
            }
            // If game reached showdown, evaluate hands
            Playable winner = null;
            HandRank bestRank = null;

            for (Playable player : players) {
                if (player.getStatus()) {
                    List<Card> combinedCards = new ArrayList<>(player.getHand());
                    combinedCards.addAll(communityCards);
                    HandRank currentRank = PokerHandEvaluator.evaluateHand(combinedCards);

                    if (bestRank == null || currentRank.compareTo(bestRank) > 0) {
                        bestRank = currentRank;
                        winner = player;
                    }
                }
            }
            if (winner == null) {
                System.out.println("No winner found.");
                return;
            }
            int winnerNum = 1;
            List<Playable> winners = new ArrayList<>();;
            for (Playable player : players) {
                if (player.getStatus()) {
                    List<Card> combinedCards = new ArrayList<>(player.getHand());
                    combinedCards.addAll(communityCards);
                    HandRank currentRank = PokerHandEvaluator.evaluateHand(combinedCards);

                    if(currentRank == bestRank && winner.getId() != player.getId()) {

                        System.out.println("Tie detected between " + winner.getName() + " and " + player.getName());
                        winnerNum += 1;
                        winners.add(player);
                    }
                }
            }
            // Set all other players' status to false
            for (Playable player : players) {
                if (player != winner) {
                    player.setStatus(false);
                }
            }

            // Distribute pot to winner
            pot = pot / winnerNum;
            if (!winners.isEmpty()) {
                for (Playable tiedPlayer : winners) {
                    tiedPlayer.addCurrentBalance(pot);
                }
            }
            pot = 0;

            // Keep winner's status as true
            broadcastState();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setCurrentBetGame(int currentBet) {this.currentBet = currentBet;}

    public int getCurrentBetGame() {return currentBet;}



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
    public Playable getCurrentPlayer() {return currentPlayer;}

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
        if (player instanceof AI) {
            raiseAmount += currentBet;
        }

        int totalBetNeeded = raiseAmount + player.getCurrentBet();

        if (currentBalance >= raiseAmount && raiseAmount > 0) {
            // Update player's bet and balance
            player.addCurrentBalance(-raiseAmount);
            pot += raiseAmount;
            player.setCurrentBet(totalBetNeeded);
            currentBet = Math.max(currentBet, totalBetNeeded);

            // Check for all-in
            if (player.getCurrentBalance() == 0) {
                handleAllIn();
            }

            // Update UI immediately
            broadcastState();
        }
    }

    private void handleAllIn() {
        // Deal remaining community cards if any
        while (communityCards.size() < 5) {
            dealCommunityCards(1);
        }

        // Force immediate showdown
        determineWinner();
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
        return countActivePlayers() == 1 ||
                (communityCards.size() == 5 &&
                        players.stream().filter(Playable::getStatus).allMatch(Playable::getHasBet));
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