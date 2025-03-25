package input;

import games.GameType;

import java.util.Map;

public class ActionProcessorRegistry {
    Map<GameType, ActionProcessor> processors;
    public ActionProcessorRegistry(Map<GameType, ActionProcessor> processors) {
        processors.put(GameType.POKER, new PokerActionProcessor());
        processors.put(GameType.BLACKJACK, new BlackjackActionProcessor());

    }
    public ActionProcessor getProcessor(GameType gameType) {
        return processors.getOrDefault(gameType, (action, game, client) -> {
            String errorMessage = "Unsupported game type: " + gameType;
            if (client != null) {
                System.out.println(errorMessage);
            } else {
                System.err.println(errorMessage);
            }
        });
    }
    public void registerProcessor(GameType gameType, ActionProcessor processor) {
        processors.put(gameType, processor);
    }

}
