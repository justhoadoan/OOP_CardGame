package games;

import ai.PokerHandEvaluator;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
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

    private CardSkin skin;
    public PokerGame(GameMode gameMode, NetworkManager networkManager, CardSkin skin) {
        this.gameMode = gameMode;
        this.networkManager = networkManager;
        this.skin = skin;
        this.deck = new Deck(skin);
        this.playerManager = new PlayerManager(this);
        this.communityCards = new ArrayList<>();
        this.pot = 0;
        this.currentBet = 0;

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

        // pre-flop + bet
        for (int i = 0; i < 2; i++) {
            allPlayerDraw();
        }
        allPlayerBet();
        currentPlayer = playerManager.getPlayers().get(0);
        broadcastState();

        // flop + bet
        deck.drawCard(); // remove 1 card
        for(int i = 1; i <= 3; i++) {communityCards.add(deck.drawCard());}
        allPlayerBet();

        // turn + bet
        deck.drawCard();
        communityCards.add(deck.drawCard());
        allPlayerBet();

        // river + bet
        deck.drawCard();
        communityCards.add(deck.drawCard());
        allPlayerBet();

        // evaluate who is winner
        int maxRankIdx = 0;
        Playable winner = null;
        for (Playable player : players) {
            if (player instanceof Player) {
                HandRank handRank = PokerHandEvaluator.evaluateHand(player.getHand());
                int rankIndex = PokerHandEvaluator.HAND_RANK_ORDER.indexOf(handRank);
                if(maxRankIdx  == Math.max(maxRankIdx , rankIndex) ) {
                    maxRankIdx = rankIndex;
                    winner = player;
                }
            }
        }

        // get other loser's status off
        for (Playable player : players) {
            if (player instanceof Player) {
                if(player.getStatus() && (player.getId() != winner.getId())) {
                    player.setStatus(false);
                }
            }
        }

        String winnerName = winner.getName();
        int winnerId = winner.getId();
    }

    public void allPlayerDraw() {
        for (Playable player : players) {
            if (player instanceof Player) {
                ((Player) player).addCard(deck.drawCard());
            }
        }
    }

    public void allPlayerBet() {
        for (Playable player : players) {
            if (player instanceof Player) {
                String action = handlePlayerTurn();
                PokerActionProcessor processor = new PokerActionProcessor();
                Player p = (Player) player;
                Client client = p.getClient();
                processor.processAction(action, this, client);
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
    private boolean isActivePlayer(Playable player) {
        return player.getStatus();
    }

    @Override
    public void playerRaise(Playable player, int raiseAmount) {
        if (player instanceof Player) {
            Player p = (Player) player;
            if (p.getCurrentBalance() >= raiseAmount) {
                placeBet(player, raiseAmount);
                p.addCurrentBalance(-raiseAmount);
            }
        }
    }

    @Override
    public void playerFold(Playable player) {player.setStatus(false);}

    @Override
    public void playerHit(Playable player) {} // nothing would be done here

    @Override
    public void playerStand(Playable player) {} // nothing would be done here

    @Override
    public String handlePlayerTurn() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(currentPlayer.getName() + ", enter your action (raise, fold): ");
        String action = scanner.nextLine().trim().toLowerCase();

        while (!action.equals("raise") && !action.equals("fold")) {
            System.out.println("Invalid action. Please enter 'raise' or 'fold': ");
            action = scanner.nextLine().trim().toLowerCase();
        }

        return action;
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
