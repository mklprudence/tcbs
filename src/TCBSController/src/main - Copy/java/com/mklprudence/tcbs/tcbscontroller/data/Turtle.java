package com.mklprudence.tcbs.tcbscontroller.data;

import com.mklprudence.tcbs.tcbscontroller.backend.Backend;

import java.util.HashMap;
import java.util.Map;

public class Turtle {
    // Instances
    private static Map<String, Turtle> turtles = new HashMap<>();

    public static Turtle getTurtleFromID(String id) {
        return getTurtleFromClientID(getClientIDfromID(id));
    }

    public static Turtle getTurtleFromClientID(String clientID) {
        if (turtles.containsKey(clientID)) return turtles.get(clientID);
        Turtle t = new Turtle(clientID);
        turtles.put(clientID, t);
        return t;
    }

    // Constructors
    private String clientID;
    private int fuelLevel = -1; // -1 == UNKNOWN; -2 == UNLIMITED
    private int fuelLimit = -1; // -1 == UNKNOWN; -2 == UNLIMITED; 0 == WLTF?????

    private Turtle(String clientID) {
        this.clientID = clientID;
    }

    // Getters
    public String getID() {
        return getIDfromClientID(clientID);
    }

    public String getClientID() {
        return clientID;
    }

    public int getFuelLevel() {
        if (fuelLevel == -1) reqFuelLevel();
        return fuelLevel;
    }

    public int getFuelLimit() {
        if (fuelLimit == -1 || fuelLimit == 0) reqFuelLimit();
        return fuelLimit;
    }

    public boolean isConsumeFuel() {
        return fuelLimit == -2;
    }

    public float getFuelPercentage() {
        if (fuelLimit == -2 || fuelLevel == -2) return 1.0f;
        if (fuelLimit == -1 || fuelLevel == -1) return 0.0f;
        if (fuelLimit == 0) {
            reqFuelLimit();
            return 0.0f;
        }
        return (float) fuelLevel / (float) fuelLimit;
    }

    // Setters
    public void setFuelLevel(int fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public void setFuelLimit(int fuelLimit) {
        this.fuelLimit = fuelLimit;
    }

    public void setInfiniteFuel() {
        this.fuelLimit = -1;
        this.fuelLevel = -1;
    }

    // Networking
    public void reqFuelLevel() {
        Backend.INSTANCE.sendReqFuelLevel(this.clientID);
    }

    public void reqFuelLimit() {
        Backend.INSTANCE.sendReqFuelLimit(this.clientID);
    }

    // Statics
    public static String getIDfromClientID(String clientID) {
        return clientID.split(":")[1];
    }

    public static String getClientIDfromID(String ID) {
        return "turtle:" + ID;
    }
}
