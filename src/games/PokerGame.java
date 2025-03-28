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
        this.deck = new Deck(skin);
        this.players = new ArrayList<>();
        this.playerManager = new PlayerManager(this);
        this.communityCards = new ArrayList<>();
        this.pot = 0;
        this.currentBet = 0;
        this.aiFactory = new AIStrategyFactory();

        if (gameMode != null) {
            gameMode.setGame(this);
            gameMode.setCardSkin(skin);
        }

    }

    public void start() {
        deck.reset();
        pot = 0;
        currentBet = 0;
        communityCards.clear();
        
        if (players.isEmpty()) {
            System.out.println("Cannot start game: No players available");
            return;
        }
        
        // Reset all players' hands and status
        for (Playable player : players) {
            player.resetHand();
            player.setStatus(true);
        }
        
        currentPlayer = players.get(0);

        // pre-flop + bet
        for (int i = 0; i < 2; i++) {
            allPlayerDraw();
        }
        
        // Chỉ hiển thị game state cho người chơi thật sau khi chia bài ban đầu
        if (currentPlayer instanceof Player) {
            broadcastState();
        }
        
        // flop + bet
        deck.drawCard(); // remove 1 card
        for(int i = 1; i <= 3; i++) {
            communityCards.add(deck.drawCard());
        }
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
            if (player.getStatus()) {  // Check all active players, not just Player instances
                HandRank handRank = PokerHandEvaluator.evaluateHand(player.getHand());
                int rankIndex = PokerHandEvaluator.HAND_RANK_ORDER.indexOf(handRank);
                if (rankIndex >= maxRankIdx) {  // Use >= instead of comparing max
                    maxRankIdx = rankIndex;
                    winner = player;
                }
            }
        }

        if (winner != null) {
            // Set winner as the only active player
            for (Playable player : players) {
                player.setStatus(player.getId() == winner.getId());
            }
            
            System.out.println("Winner: " + winner.getName() + " with hand rank: " + 
                              PokerHandEvaluator.HAND_RANK_ORDER.get(maxRankIdx));
                              
            // Distribute pot to winner
            if (winner instanceof Player) {
                distributePot((Player)winner);
            } else if (winner instanceof AI) {
                // Handle AI winner
                ((AI)winner).addCurrentBalance(pot);
            }
        }
        
        // Chỉ broadcast state ở cuối game để thông báo người thắng
        broadcastState();

        // Check after each betting round if we have active players
        if (countActivePlayers() == 0) {
            System.out.println("Game over: No active players remaining");
            return;
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
        // Check if there are any active players
        List<Playable> activePlayers = players.stream()
            .filter(Playable::getStatus)
            .collect(Collectors.toList());
        
        if (activePlayers.isEmpty()) {
            System.out.println("No active players to bet");
            return;
        }
        
        for (Playable player : activePlayers) {
            if (player.getStatus()) {
                currentPlayer = player;
                
                // Chỉ broadcast state trước và sau lượt của người chơi thật
                boolean isHumanPlayer = player instanceof Player;
                
                // Hiển thị state trước khi người chơi thật hành động
                if (isHumanPlayer) {
                    broadcastState();
                }

                try {
                    if (player instanceof AI) {
                        AI ai = (AI) player;
                        // Make sure AI has a hand to evaluate
                        if (ai.getHand() == null || ai.getHand().isEmpty()) {
                            System.out.println("AI " + ai.getName() + " has no cards to evaluate");
                            continue;
                        }
                        
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
                    } else {
                        PokerActionProcessor processor = new PokerActionProcessor();
                        String action = handlePlayerTurn();
                        processor.processAction(action, this, null);
                    }
                } catch (Exception e) {
                    System.out.println("Error during player bet: " + e.getMessage());
                    e.printStackTrace();
                }

                // Chỉ hiển thị state sau khi người chơi thật hành động
                if (isHumanPlayer) {
                    broadcastState();
                }
            }
        }
        
        // Không cần broadcast state ở cuối vòng cược nữa
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
        
        // Chỉ cập nhật hiển thị khi:
        // 1. Lượt của người chơi thật (không phải AI)
        // 2. Đã kết thúc game (để hiển thị người thắng)
        if (gameMode != null) {
            // Chỉ hiển thị bài của người chơi thật (là Player, không phải AI)
            boolean isHumanPlayer = currentPlayer instanceof Player;
            List<Card> playerHand = null;
            
            // Chỉ lấy bài nếu là người chơi thật
            if (isHumanPlayer) {
                playerHand = getPlayerHand(((Player) currentPlayer).getId());
            }
            
            // Chỉ gọi updateDisplay trong 2 trường hợp:
            // 1. Khi là lượt của người chơi thật (playerHand != null)
            // 2. Khi game đã kết thúc (để hiển thị người thắng)
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

    void resetBets() {this.pot = 0; this.currentBet = 0;}

    private int countActivePlayers() {
        int count = 0;
        for (Playable player : players) {
            if (player.getStatus()) {
                count++;
            }
        }
        return count;
    }

    // Add a method to check if deck is empty

}
