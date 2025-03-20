package playeable;

import games.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {
private List<Playable> players;
private Game game;
private Map<Integer, Playable> playerMap;

    public PlayerManager (){
        this.game = game;
        this.players = new ArrayList<>();
        this.playerMap = new HashMap<>();
    }
    public void addPlayer(Playable player) {
        players.add(player);
        playerMap.put(player.getId(), player);
    }

    public void removePlayer(Playable player) {
        players.remove(player);
        playerMap.remove(player.getId());
    }

    public Playable getPlayerById(int id) {
        return playerMap.get(id);
    }

    public Playable getCurrentPlayer(){
        return players.isEmpty() ? null : players.getFirst();
    }

    public List<Playable> getPlayers(){
        return new ArrayList<>(players);
    }

    public void reset(){
        players.clear();
        playerMap.clear();
    }
}


