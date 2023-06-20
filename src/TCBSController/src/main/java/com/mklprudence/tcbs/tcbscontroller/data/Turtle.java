package com.mklprudence.tcbs.tcbscontroller.data;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import com.mklprudence.tcbs.tcbscontroller.backend.Backend;
import net.minecraft.core.Direction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Turtle {
    public static List<Direction> DIRECTIONS = Arrays.asList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

    // Constructors
    private String clientID;
    private int fuelLevel = -1; // -1 == UNKNOWN; -2 == UNLIMITED
    private int fuelLimit = -1; // -1 == UNKNOWN; -2 == UNLIMITED; 0 == WLTF?????
    private int x = 0, y = 0, z = 0;
    private Direction facing = Direction.NORTH;

    private transient boolean isDirty = false;
    private transient boolean isMoved = false;
    private transient boolean isRotated = false;

    Turtle(String clientID) {
        this.clientID = clientID;
    }

    // Getters
    public String getID() {
        return getIDfromClientID(clientID);
    }

    public String getClientID() {
        return clientID;
    }

    public boolean isDirty() {
        return isDirty;
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
        return fuelLimit != -2;
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Direction getFacing() {
        return facing;
    }

    // Setters
    public Turtle setFuelLevel(int fuelLevel) {
        this.fuelLevel = fuelLevel;
        this.isDirty = true;
        return this;
    }

    public Turtle setFuelLimit(int fuelLimit) {
        this.fuelLimit = fuelLimit;
        this.isDirty = true;
        return this;
    }

    public Turtle setInfiniteFuel() {
        this.fuelLimit = -2;
        this.fuelLevel = -2;
        this.isDirty = true;
        return this;
    }

    public Turtle setX(int x) {
        if (this.x == x) return this;
        this.x = x;
        this.isDirty = true;
        this.isMoved = true;
        return this;
    }

    public Turtle setY(int y) {
        if (this.y == y) return this;
        this.y = y;
        this.isDirty = true;
        this.isMoved = true;
        return this;
    }

    public Turtle setZ(int z) {
        if (this.z == z) return this;
        this.z = z;
        this.isDirty = true;
        this.isMoved = true;
        return this;
    }

    public Turtle setD(int d) {
        if (d < 0 || d > 3) throw new RuntimeException("WTF");
        if (DIRECTIONS.indexOf(this.facing) == d) return this;
        this.facing = DIRECTIONS.get(d);
        this.isDirty = true;
        this.isRotated = true;
        return this;
    }

    // Cleaning
    public void clean() {
        this.isDirty = false;
        cleanMoved();
        cleanRotated();
    }

    private void cleanMoved() {
        if (isMoved == false) return;
        if (!TCBSController.isServerSide()) throw new RuntimeException("WHY ARE U HERE ON CLIENT SIDE");
        isMoved = false;
        TCBSController.LOGGER.debug(String.format("[DATA] MOVED TO %d, %d, %d", x, y, z));
    }

    private void cleanRotated() {
        if (isRotated == false) return;
        if (!TCBSController.isServerSide()) throw new RuntimeException("WHY ARE U HERE ON CLIENT SIDE");
        isRotated = false;
        TCBSController.LOGGER.debug(String.format("[DATA] ROTATED TO FACE %s", facing.getName()));
    }

    // Networking
    public Turtle reqInfo() {
        Backend.INSTANCE.sendReqTurtleInfo(this.clientID);
        return this;
    }

    public Turtle reqFuelLevel() {
        Backend.INSTANCE.sendReqFuelLevel(this.clientID);
        return this;
    }

    public Turtle reqFuelLimit() {
        Backend.INSTANCE.sendReqFuelLimit(this.clientID);
        return this;
    }

    // Statics
    public static String getIDfromClientID(String clientID) {
        String[] split = clientID.split(":");
        return split.length < 2 ? "" : split[1];
    }

    public static String getClientIDfromID(String ID) {
        return "turtle:" + ID;
    }
}
