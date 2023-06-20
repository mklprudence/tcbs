package com.mklprudence.tcbs.tcbscontroller.network;

public enum ActionType {
    Forward, Backward, Up, Down, TurnLeft, TurnRight,
    Dig, DigUp, DigDown,
    Place, PlaceUp, PlaceDown,
    Attack, AttackUp, AttackDown,
    Suck, SuckUp, SuckDown,
    Inspect, InspectUp, InspectDown,
    Terminate;

    public static ActionType fromOrdinal(int ord) {
        return ActionType.values()[ord];
    }
}
