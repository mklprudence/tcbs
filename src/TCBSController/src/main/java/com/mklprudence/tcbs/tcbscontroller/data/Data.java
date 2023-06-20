package com.mklprudence.tcbs.tcbscontroller.data;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import com.mklprudence.tcbs.tcbscontroller.network.Networking;
import com.mklprudence.tcbs.tcbscontroller.network.PacketSyncDataToClient;
import com.mklprudence.tcbs.tcbscontroller.network.PacketSyncDataToServer;
import com.sun.jna.ptr.ShortByReference;
import net.minecraft.server.MinecraftServer;

import java.util.*;
import java.util.stream.Collectors;

public class Data {
    public static Data SERVER = new Data(Side.SERVER), CLIENT = new Data(Side.CLIENT);
    public static List<DataUpdateListener> SERVER_LISTENER = new ArrayList<>(), CLIENT_LISTENER = new ArrayList<>();

    private Data(Side side) {
        this.side = side;
    }

    private Side side;
    private List<String> turtleClients = new ArrayList<>(), controllerClients = new ArrayList<>(), spectatorClients = new ArrayList<>();
    private Map<String, Turtle> turtleData = new HashMap<>();

    private transient boolean isDirty = false;

    private Data setSide(Side side) {
        this.side = side;
        return this;
    }

    public Data parseClientsList(String clients) {
        controllerClients.clear();
        spectatorClients.clear();
        turtleClients.clear();
        Arrays.stream(clients.split(" ")).forEach(clientID -> {
            String namespace = clientID.split(":")[0];
            if (namespace.equals("controller")) {
                controllerClients.add(clientID);
            } else if (namespace.equals("spectator")) {
                spectatorClients.add(clientID);
            } else if (namespace.equals("turtle")) {
                turtleClients.add(clientID);
                getTurtleFromClientID(clientID).reqInfo();
            }
        });
        isDirty = true;
        return this;
    }

    public String joinClientsList() {
        return Arrays.asList(
                turtleClients.stream().collect(Collectors.joining(" ")),
                controllerClients.stream().collect(Collectors.joining(" ")),
                spectatorClients.stream().collect(Collectors.joining(" "))
        ).stream().collect(Collectors.joining(" "));
    }

    public List<String> getTurtleClients() {
        return turtleClients;
    }

    public List<String> getControllerClients() {
        return controllerClients;
    }

    public List<String> getSpectatorClients() {
        return spectatorClients;
    }

    public Turtle getTurtleFromID(String id) {
        return getTurtleFromClientID(Turtle.getClientIDfromID(id));
    }

    public Turtle getTurtleFromClientID(String clientID) {
        if (turtleData.containsKey(clientID)) return turtleData.get(clientID);
        Turtle t = new Turtle(clientID);
        turtleData.put(clientID, t);
        isDirty = true;
        return t;
    }

    public void clean() {
        boolean needsCleaning = false;
        if (isDirty) needsCleaning = true;
        for (Turtle i : turtleData.values()) {
            if (i.isDirty()) {
                needsCleaning = true;
                break;
            }
        }
        if (!needsCleaning) return;
        TCBSController.LOGGER.debug("[DATA] needs cleaning");

        // Starts cleaning
        isDirty = false;
        turtleData.forEach((k,v) -> v.clean());
        runListener();
        sync();
    }

    public void sync() {
        if (this.side == Side.SERVER) {
            Networking.broadcastToPlayer(new PacketSyncDataToClient());
            TCBSController.LOGGER.debug("[DATA] sends PacketSyncDataToClient");
        } else {
            Networking.sendToServer(new PacketSyncDataToServer());
            TCBSController.LOGGER.debug("[DATA] sends PacketSyncDataToServer");
        }
    }

    public String toJson() {
        return TCBSController.GSON.toJson(this);
    }

    public static void fromJson(Side side, String json) {
        Data data = TCBSController.GSON.fromJson(json, Data.class).setSide(side);
        if (side == Side.SERVER) {
            SERVER = data;
        } else {
            CLIENT = data;
        }
        data.runListener();
    }

    public void runListener() {
        if (side == Side.SERVER) {
            SERVER_LISTENER.forEach((list) -> list.handle());
        } else {
            CLIENT_LISTENER.forEach((list) -> list.handle());
        }
    }

    public static void addListener(Side side, DataUpdateListener listener) {
        if (side == Side.SERVER) {
            SERVER_LISTENER.add(listener);
        } else {
            CLIENT_LISTENER.add(listener);
        }
    }

    public enum Side {SERVER, CLIENT}

    public interface DataUpdateListener {
        void handle();
    }
}
