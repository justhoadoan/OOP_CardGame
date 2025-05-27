package ai;

import games.GameType;
import java.util.HashMap;
import java.util.Map;

public class AIStrategyFactory {
    private Map<GameType, Map<String, AIStrategy>> strategies;

    public AIStrategyFactory(){
        strategies = new HashMap<>();
        strategies.put(GameType.POKER, new HashMap<>());
        strategies.put(GameType.BLACKJACK, new HashMap<>());
    }

    public AIStrategy getAIStrategy(String strategy){
        if(strategy.equals("Monte Carlo")){
            return new MonteCarloAIStrategy();
        }
        else if(strategy.equals("Rule Based")){
            return new RuleBasedAIStrategy();
        }
        else{
            return null;
        }
    }
}
