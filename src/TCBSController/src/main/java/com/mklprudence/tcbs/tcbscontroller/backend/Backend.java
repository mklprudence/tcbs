package com.mklprudence.tcbs.tcbscontroller.backend;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import com.mklprudence.tcbs.tcbscontroller.data.Data;
import com.mklprudence.tcbs.tcbscontroller.data.Turtle;
import com.mklprudence.tcbs.tcbscontroller.network.ActionType;
import com.sun.jna.platform.win32.Rasapi32Util;
import jakarta.websocket.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.server.ServerLifecycleEvent;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;

@ClientEndpoint
public class Backend {
    public static Backend INSTANCE = null;

    public boolean connected = false;
    public Session session;

    public Backend() {
        connect();
    }

    public static void init() {
        if (INSTANCE != null && INSTANCE.connected) return;
        INSTANCE = new Backend();
        TCBSController.LOGGER.info("INIT BACKEND");
    }

    public void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, URI.create(BackendConfig.BACKEND_URL.get()));
            TCBSController.LOGGER.info("[WS] Constructor success");
        } catch (DeploymentException | IOException e) {
            this.connected = false;
            return;
        }
        this.connected = true;
    }

    @OnOpen
    public void onOpen(Session session) {
        TCBSController.LOGGER.info("[WS] Opening WS");
        this.session = session;
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        TCBSController.LOGGER.info(String.format("[WS] Closing WS for %s", reason.toString()));
        this.session = null;
        connected = false;
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        TCBSController.LOGGER.error(String.format("[WS] Session: %s, Error: %s", session.getId(), throwable.toString()));
    }

    @OnMessage
    public void onMessage(String message) {
        TCBSController.LOGGER.info(String.format("[WS] Received: %s", message));
        Message msg = Message.fromJson(message);

        if (msg.type.equals("REQINIT")) {
            sendMessage(Message.init());
        } else if (msg.type.equals("INFO")) {
            String req = msg.args.get("request");
            if (req == null) return;
            switch (req) {
                case "clients" -> {
                    String clients = msg.args.get("list");
                    if (clients == null) return;
                    Data.SERVER.parseClientsList(clients).clean();
                }
                case "lua" -> {
                    String lua = msg.args.get("res");
                    logChat(String.format("<Lua> %s", lua));
                }
                case "inspect", "inspectUp", "inspectDown" -> {
                    String res = msg.args.get("res");
                    JsonElement obj = TCBSController.GSON.fromJson(res, JsonElement.class);
                    String pretty = TCBSController.GSON_PRETTY.toJson(obj);
                    logChat(String.format("<%s>\n%s", StringUtils.capitalize(req), pretty));
                }
                case "fuelLevel" -> {
                    String res = msg.args.get("res");
                    if (res.equals("unlimited")) {
                        Data.SERVER.getTurtleFromClientID(msg.from).setInfiniteFuel();
                        Data.SERVER.clean();
                    } else {
                        Data.SERVER.getTurtleFromClientID(msg.from).setFuelLevel(Integer.parseInt(res));
                        Data.SERVER.clean();
                    }
                }
                case "fuelLimit" -> {
                    String res = msg.args.get("res");
                    if (res.equals("unlimited")) {
                        Data.SERVER.getTurtleFromClientID(msg.from).setInfiniteFuel();
                        Data.SERVER.clean();
                    } else {
                        Data.SERVER.getTurtleFromClientID(msg.from).setFuelLimit(Integer.parseInt(res));
                        Data.SERVER.clean();
                    }
                }
                case "turtleInfo" -> {
                    String res = msg.args.get("res");

                    Turtle turtle = Data.SERVER.getTurtleFromClientID(msg.to);
                    JsonObject info = TCBSController.GSON.fromJson(res, JsonObject.class);
                    if (info.has("fuelLimit")) {
                        String fuelLimit = info.get("fuelLimit").getAsString();
                        if (fuelLimit.equals("unlimited")) {
                            turtle.setInfiniteFuel();
                        } else {
                            turtle.setFuelLimit(Integer.parseInt(fuelLimit));
                        }
                    }
                    if (info.has("fuelLevel")) {
                        String fuelLevel = info.get("fuelLevel").getAsString();
                        if (fuelLevel.equals("unlimited")) {
                            turtle.setInfiniteFuel();
                        } else {
                            turtle.setFuelLevel(Integer.parseInt(fuelLevel));
                        }
                    }
                    if (info.has("x")) {
                        turtle.setX(info.get("x").getAsInt());
                    }
                    if (info.has("y")) {
                        turtle.setY(info.get("y").getAsInt());
                    }
                    if (info.has("z")) {
                        turtle.setZ(info.get("z").getAsInt());
                    }
                    if (info.has("d")) {
                        turtle.setD(info.get("d").getAsInt());
                    }

                    Data.SERVER.clean();
                }
            }
        }
    }

    @OnMessage
    public void onMessage(ByteBuffer bytes) {
        System.out.println(String.format("[WS] Bytes Received"));
    }

    public void sendMessage(String message) {
        this.session.getAsyncRemote().sendText(message);
    }

    public void sendMessage(Message message) {
        sendMessage(message.toString());
    }

    public void sendAction(String clientID, ActionType type) {
        sendMessage(Message.fromType(clientID, type));
    }

    public void sendLua(String clientID, String lua) {
        sendMessage(Message.fromLua(clientID, lua));
    }

    public void sendReqFuelLimit(String clientID) {
        sendMessage(Message.reqFuelLimit(clientID));
    }

    public void sendReqFuelLevel(String clientID) {
        sendMessage(Message.reqFuelLevel(clientID));
    }

    public void sendReqTurtleInfo(String clientID) {
        sendMessage(Message.reqTurtleInfo(clientID));
    }

    public void logChat(String msg) {
        if (TCBSController.MINECRAFT_SERVER == null) throw new RuntimeException("Dont access from CLIENT-SIDE");
        CommandSourceStack css = TCBSController.MINECRAFT_SERVER.createCommandSourceStack();
        TCBSController.MINECRAFT_SERVER.getPlayerList().broadcastChatMessage(PlayerChatMessage.system(msg), css, ChatType.bind(ChatType.CHAT, css));
    }



    public static class Message {
        public String type = "";
        public String from = "";
        public String to = "";
        public Map<String, String> args = new HashMap<>();

        public static Message fromType(String clientID, ActionType type) {
            Message msg = new Message();
            msg.type = "COMMAND";
            msg.from = "controller:forge";
            msg.to = clientID;
            msg.args.put("action", type.toString());
            return msg;
        }

        public static Message fromLua(String clientID, String lua) {
            Message msg = new Message();
            msg.type = "COMMAND";
            msg.from = "controller:forge";
            msg.to = clientID;
            msg.args.put("action", "Lua");
            msg.args.put("lua", lua);
            return msg;
        }

        public static Message init() {
            Message msg = new Message();
            msg.type = "INIT";
            msg.from = "controller:forge";
            msg.to = "server";
            return msg;
        }

        public static Message reqFuelLimit(String clientID) {
            Message msg = new Message();
            msg.type = "REQINFO";
            msg.from = "controller:forge";
            msg.to = clientID;
            msg.args.put("request", "fuelLimit");
            return msg;
        }

        public static Message reqFuelLevel(String clientID) {
            Message msg = new Message();
            msg.type = "REQINFO";
            msg.from = "controller:forge";
            msg.to = clientID;
            msg.args.put("request", "fuelLevel");
            return msg;
        }

        public static Message reqTurtleInfo(String clientID) {
            Message msg = new Message();
            msg.type = "REQINFO";
            msg.from = "controller:forge";
            msg.to = clientID;
            msg.args.put("request", "turtleInfo");
            return msg;
        }

        public static Message fromJson(String json) {
            return TCBSController.GSON.fromJson(json, Message.class);
        }

        @Override
        public String toString() {
            return TCBSController.GSON.toJson(this);
        }
    }

    private static class TurtleInfo {
        public String fuelLevel;
        public String fuelLimit;
    }
}
