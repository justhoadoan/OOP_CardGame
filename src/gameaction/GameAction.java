package gameaction;

public interface GameAction {
    String getActionName();
    boolean requiresParameter();
    String getParameterName();
    void setParameter(int value);
    int getParameter();
}