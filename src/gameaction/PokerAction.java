package gameaction;

public class PokerAction implements GameAction {
    private String actionName;
    private boolean requiresParameter;
    private int parameter; // raiseAmount

    public PokerAction(String actionName, boolean requiresParameter) {
        this.actionName = actionName;
        this.requiresParameter = requiresParameter;
        this.parameter = 0;
    }

    @Override
    public String getActionName() {
        return actionName;
    }

    @Override
    public boolean requiresParameter() {
        return requiresParameter;
    }

    @Override
    public String getParameterName() {
        return requiresParameter ? "raiseAmount" : null;
    }

    @Override
    public void setParameter(int value) {
        this.parameter = value;
    }

    @Override
    public int getParameter() {
        return parameter;
    }

    @Override
    public String toString() {
        return actionName;
    }
}
