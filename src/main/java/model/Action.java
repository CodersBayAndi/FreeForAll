package model;

public class Action {

    String targetName;
    ActionType actionType;

    public Action(String action, ActionType actionType) {
        this.targetName = action;
        this.actionType = actionType;
    }

    public enum ActionType {
        USER,
        ATKBUFF,
        DEFBUFF,
        ATKDEBUFF,
        DEFDEBUFF;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }
}
