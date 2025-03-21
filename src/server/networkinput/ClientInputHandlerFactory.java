package server.networkinput;

import games.GameType;

public class ClientInputHandlerFactory {

    public static server.networkinput.ClientInputHandlerFactory ClientInputHandlerFactory;

    public static ClientInputHandler getHandler(GameType gameType) {
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


