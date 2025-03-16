package gui;

import card.Card;

import java.util.List;

public interface View {
    // GUI initialization
    void initialize();

    // Display updates
    void updateDisplay(String playerInfo, String publicState, List<Card> playerHand);

    // Interactive controls
    void setActionButtons(String button1Text, Runnable action1,
                          String button2Text, Runnable action2);

    // Result display
    void showResult(String result);
}
