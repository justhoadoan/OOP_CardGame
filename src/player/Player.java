package player;

import games.Game;
import gamemode.GameMode;
import card.Card;

import java.util.ArrayList;
import java.util.List;

public class Player implements Playable {
    private String name;
    private int id; //
    private List<Card> hand;
    private GameMode gameMode;

    public Player(String name, int id) {
        this.name = name;
        this.id = id;
        this.hand = new ArrayList<>();
    }

    @Override
    public void playTurn(Game game) {
        // Instead of calling `gameMode.executeTurn()`, call the appropriate methods from `GameMode`.
        gameMode.getPlayerInput(); // Receive input from the player.
        gameMode.displayGameState(); // Display the game state.
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Card> getHand() {
        return new ArrayList<>(hand); // Avoid exposing internal data.
    }

    @Override
    public void setHand(List<Card> hand) {
        this.hand = new ArrayList<>(hand);
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public void removeCard(Card card) {
        hand.remove(card);
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", hand=" + hand +
                '}';
    }
}
