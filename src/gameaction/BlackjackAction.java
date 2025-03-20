package gameaction;

public class BlackjackAction implements GameAction {
    private String actionName;

    public BlackjackAction(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public String getActionName() {
        return actionName;
    }

    @Override
    public boolean requiresParameter() {
        return false; // Blackjack dont need parameter
    }

    @Override
    public String getParameterName() {
        return null;
    }

    @Override
    public void setParameter(int value) {
        // non
    }

    @Override
    public int getParameter() {
        return 0; // non
    }

    @Override
    public String toString() {
        return actionName;
    }
}
