package games;

import card.Card;
import card.CardSkin;
import deck.Deck;
import gamemode.GameMode;
import playable.Playable;
import playable.Player;

import java.util.*;

public class BlackjackGame implements Game {
    private Deck deck;
    private List<Playable> players;
    private Playable currentPlayer;
    private Map<Playable, Integer> playerBets;
    private int pot;
    private boolean dealerTurn;
    private Playable dealer;
    private Playable playerBeforeDealer;

    public BlackjackGame() {
        this.dealer = new Player("Dealer", 0); // Dealer is a special player
        this.players = new ArrayList<>(); // Initialize the players list
        this.playerBets = new HashMap<>(); // Initialize the playerBets map
        this.deck = new Deck(); // Initialize the deck
    }

    @Override
    public void start() {
        deck.createNewDeck();
        resetBets();
        dealerTurn = false;
        pot = 0;

        // Reset hands and place bets
        for (Playable player : players) {
            player.resetHand();
            if (player != dealer) {
                placeInitialBet(player, playerBets.getOrDefault(player, 0));
            }
        }
        dealer.resetHand();

        // First deal
        for (int i = 0; i < 2; i++) {
            for (Playable player : players) {
                if (player != dealer) {
                    player.addCard(deck.drawCard());
                }
            }
        }

        // Set the current player to the first player (excluding the dealer)
        for (Playable player : players) {
            if (player != dealer) {
                currentPlayer = player;
                break;
            }
        }

        // dealer draw 2 starting card
        ((Player) dealer).addCard(deck.drawCard());
        ((Player) dealer).addCard(deck.drawCard());

        
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
    public void nextPlayer() {
        currentPlayer = getNextPlayer();
        if (currentPlayer == null || currentPlayer == dealer) {
            currentPlayer = dealer;
            playDealerTurn();
        }
    }

    @Override
    public Playable getCurrentPlayer() {
        if (currentPlayer == null) {
            throw new IllegalStateException("Current player is not set.");
        }
        return currentPlayer;
    }

    @Override
    public Playable getPlayerBeforeDealer() {
        return playerBeforeDealer;
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
        return "Dealer's Hand: " + dealer.getHand() + "\nPot: " + pot;
    }

//    @Override
    public boolean isGameOver() {
        return (dealerTurn && calculateScore(dealer.getHand()) >= 17)
                || players.stream().allMatch(player -> player.getHand().size() >= 5)
                || players.stream().allMatch(player -> calculateScore(player.getHand()) > 21);
    }

//    @Override
    public String getWinner() {
        int dealerScore = calculateScore(dealer.getHand());
        StringBuilder winners = new StringBuilder();

        for (Playable player : players) {
            if (player.getName().equals("Dealer")) continue;
            int playerScore = calculateScore(player.getHand());
            boolean isBust = playerScore > 21;

            if (!isBust && (dealerScore > 21 || playerScore > dealerScore)) {
                distributePot(player);
                winners.append(player.getName()).append(" ");
            }
            if(!isBust && playerScore == dealerScore) {
                return "Draw!";
            }
        }
        return !winners.isEmpty() ? winners.toString() : "Dealer";
    }

    @Override
    public void playerHit(Playable player) {
        if (!dealerTurn && player.getHand().size() <= 5) {
            Card drawnCard = deck.drawCard();
            ((Player) player).addCard(drawnCard);
            System.out.println("Player " + player.getName() + " drew: " + drawnCard);
            System.out.println("Player's hand: " + player.getHand());
            System.out.println("Deck size: " + deck.getRemainingCards());
            if (calculateScore(player.getHand()) > 21) {
                System.out.println("Player " + player.getName() + " is bust!");
                currentPlayer = getNextPlayer(); // player bust
                if (currentPlayer == null || currentPlayer == dealer) {
                    playerBeforeDealer = player;
                    currentPlayer = dealer;
                    playDealerTurn();
                }
            }
        }
    }

    @Override
    public void playerStand(Playable player) {
        currentPlayer = getNextPlayer();
        if (currentPlayer == null || currentPlayer == dealer) {
            currentPlayer = dealer;
            playDealerTurn();
        }
    }

    private Playable getNextPlayer() {
        int currentIndex = players.indexOf(currentPlayer);
        if (currentIndex == -1) return players.get(0); // Start with the first player
        int nextIndex = currentIndex + 1;

        if (nextIndex >= players.size() - 1) {return null;}
        return players.get(nextIndex);
    }

    public int calculateScore(List<Card> hand) {
        int score = 0;
        int aceCount = 0;
        for (Card card : hand) {
            int value = card.getCardValueBlackJack();
            if (value == 1) { // Ace
                aceCount++;
                score += 11;
            } else if (value > 10) { // J, Q, K
                score += 10;
            } else {
                score += value;
            }
        }
        while (score > 21 && aceCount > 0) {
            score -= 10;
            aceCount--;
        }
        return score;
    }

    private void playDealerTurn() {
        if(areAllPlayersBust()) {
            System.out.println("All players are bust. Dealer wins!");
            return;
        }
        dealerTurn = true;
        while (calculateScore(dealer.getHand()) < 17) {
            dealer.addCard(deck.drawCard());
        }
    }

    private boolean areAllPlayersBust() {
        for (Playable player : players) {
            if (player != dealer && calculateScore(player.getHand()) <= 21) {
                return false; // At least one player is not bust
            }
        }
        return true; // All players are bust
    }

    public void placeInitialBet(Playable player, int amount) {
        if (player.getCurrentBalance() >= amount) {
            playerBets.put(player, amount);
            pot += amount;
            player.addCurrentBalance(-amount); // tru tien nguoi choi de bet
        }
    }

    public void distributePot(Playable winner) {
        int payout = playerBets.get(winner) * 2; // Trả 1:1
        winner.addCurrentBalance(payout);
        pot -= payout;
    }

    public void resetBets() {
        playerBets.clear();
        pot = 0;
    }

    @Override
    public void broadcastState() {

    }

    @Override
    public GameType getGameType() {
        return GameType.BLACKJACK;
    }

    @Override
    public GameMode getGameMode() {
        return null;
    }

    @Override
    public void setGameMode(GameMode gameMode) {

    }

    @Override
    public void playerRaise(Playable player, int raiseAmount) {
        throw new UnsupportedOperationException("Not supported in Blackjack");
    }

    @Override
    public void playerFold(Playable player) {
        throw new UnsupportedOperationException("Not supported in Blackjack");
    }

    public Playable getDealer() {
        return dealer;
    }

    public void showWinner() {
        String winner = getWinner();
        if (winner.isEmpty()) {
            System.out.println("No winners this round.");
        } else {
            System.out.println("Winner(s): " + winner);
        }
    }


}