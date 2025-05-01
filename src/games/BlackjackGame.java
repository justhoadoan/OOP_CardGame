package games;

import card.Card;
import card.CardSkin;
import deck.Deck;
import gamemode.GameMode;
import playable.Playable;
import playable.Player;
import server.NetworkManager;
import java.util.*;

public class BlackjackGame implements Game {
    private Deck deck;
    private GameMode gameMode;
    private NetworkManager networkManager;
    private List<Playable> players;
    private Playable currentPlayer;
    private Map<Playable, Integer> playerBets;
    private int pot;
    private boolean dealerTurn;
    private Playable dealer;

    public BlackjackGame(GameMode gameMode, NetworkManager networkManager, CardSkin skin) {
        this.gameMode = gameMode;
        this.networkManager = networkManager;
        this.dealer = new Player("Dealer", 0); // Dealer is a special player
        this.players = new ArrayList<>(); // Initialize the players list
        this.playerBets = new HashMap<>(); // Initialize the playerBets map
        this.deck = new Deck(); // Initialize the deck
    }

    @Override
    public void start() {
        deck.reset();
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
                player.addCard(deck.drawCard());
            }
        }

        // Set the current player to the first player (excluding the dealer)
        for (Playable player : players) {
            if (player != dealer) {
                currentPlayer = player;
                break;
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
    public Playable getCurrentPlayer() {
        if (currentPlayer == null) {
            throw new IllegalStateException("Current player is not set.");
        }
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
        return "Dealer's Hand: " + dealer.getHand() + "\nPot: " + pot;
    }

//    @Override
    public boolean isGameOver() {
        return dealerTurn && calculateScore(dealer.getHand()) >= 17;
    }

//    @Override
    public String getWinner() {
        int dealerScore = calculateScore(dealer.getHand());
        StringBuilder winners = new StringBuilder();

        for (Playable player : players) {
            if (player == dealer) continue;
            int playerScore = calculateScore(player.getHand());
            boolean isBust = playerScore > 21;

            if (!isBust && (dealerScore > 21 || playerScore > dealerScore)) {
                distributePot(player);
                winners.append(player.getName()).append(", ");
            }
        }
        return winners.length() > 0 ? winners.toString() : "Dealer wins!";
    }

    @Override
    public void playerHit(Playable player) {
        if (!dealerTurn && player.getHand().size() < 5) {
            Card drawnCard = deck.drawCard();
            ((Player) player).addCard(drawnCard);
            System.out.println("Player " + player.getName() + " drew: " + drawnCard);
            System.out.println("Player's hand: " + player.getHand());
            System.out.println("Deck size: " + deck.getRemainingCards());
            if (calculateScore(player.getHand()) > 21) {
                currentPlayer = getNextPlayer(); // player bust
            }
        }
    }

    @Override
    public void playerStand(Playable player) {
        currentPlayer = getNextPlayer();
        if (currentPlayer == null) {
            playDealerTurn();
        }
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
        dealerTurn = true;
        while (calculateScore(dealer.getHand()) < 17) {
            dealer.addCard(deck.drawCard());
        }
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

    private Playable getNextPlayer() {
        int currentIndex = players.indexOf(currentPlayer);
        if (currentIndex == -1) return players.get(0);
        int nextIndex = (currentIndex + 1) % players.size();
        return nextIndex == 0 ? null : players.get(nextIndex); // Kết thúc khi đến dealer
    }

    @Override
    public void broadcastState() {

    }

    @Override
    public GameType getGameType() {
        return GameType.BLACKJACK;
    }

    @Override
    public String handlePlayerTurn() {return null;}

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