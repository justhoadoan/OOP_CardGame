package server.networkinput;

import games.GameType;
import java.util.EnumMap;
import java.util.Map;

public final class ClientInputHandlerFactory {
    private static final ClientInputHandlerFactory INSTANCE = new ClientInputHandlerFactory();
    private final Map<GameType, ClientInputHandler> handlers;

    private ClientInputHandlerFactory() {
        handlers = new EnumMap<>(GameType.class);
    }

    public static ClientInputHandlerFactory getInstance() {
        return INSTANCE;
    }

    public ClientInputHandler getHandler(GameType gameType) {
        return handlers.computeIfAbsent(gameType, this::createHandler);
    }

    private ClientInputHandler createHandler(GameType gameType) {
        switch (gameType) {
            case POKER:
                return new PokerClientInputHandler();
            case BLACKJACK:
                return new BlackjackClientInputHandler();
            default:
                throw new IllegalArgumentException("Unsupported game type: " + gameType);
        }
    }
}