package com.mklprudence.tcbs.tcbscontroller.backend;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import com.mklprudence.tcbs.tcbscontroller.data.Turtle;
import com.mklprudence.tcbs.tcbscontroller.network.ActionType;
import jakarta.websocket.*;
import net.minecraft.commands.CommandSourceStack;
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
    public static MinecraftServer server = null;
    public static List<String> turtles = new ArrayList<>(), controllers = new ArrayList<>(), spectators = new ArrayList<>();

    public boolean connected = false;
    public Session session;

    public Backend() {
        connect();
    }

    public static void init(ServerLifecycleEvent event) {
        server = event.getServer();
        init();
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
            if (req.equals("clients")) {
                String clients = msg.args.get("list");
                if (clients == null) return;
                List<String> c = new ArrayList<>(), s = new ArrayList<>(), t = new ArrayList<>();
                Arrays.stream(clients.split(" ")).forEach(clientID -> {
                    String namespace = clientID.split(":")[0];
                    if (namespace.equals("controller")) {
                        c.add(clientID);
                    } else if (namespace.equals("spectator")) {
                        s.add(clientID);
                    } else if (namespace.equals("turtle")) {
                        t.add(clientID);
                    }
                });
                controllers = c;
                turtles = t;
                spectators = s;
            } else if (req.equals("lua")) {
                String lua = msg.args.get("res");
                logChat(String.format("<Lua> %s", lua));
            } else if (req.equals("inspect") || req.equals("inspectUp") || req.equals("inspectDown")) {
                String res = msg.args.get("res");
                JsonObject obj = TCBSController.GSON.fromJson(res, JsonObject.class);
                String pretty = TCBSController.GSON_PRETTY.toJson(obj);
                logChat(String.format("<%s>\n%s", StringUtils.capitalize(req), pretty));
            } else if (req.equals("fuelLevel")) {
                String res = msg.args.get("res");
                if (res.equals("unlimited")) {
                    Turtle.getTurtleFromClientID(msg.from).setInfiniteFuel();
                } else {
                    Turtle.getTurtleFromClientID(msg.from).setFuelLevel(Integer.parseInt(res));
                }
            } else if (req.equals("fuelLimit")) {
                String res = msg.args.get("res");
                if (res.equals("unlimited")) {
                    Turtle.getTurtleFromClientID(msg.from).setInfiniteFuel();
                } else {
                    Turtle.getTurtleFromClientID(msg.from).setFuelLimit(Integer.parseInt(res));
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

    public void logChat(String msg) {
        if (server == null) return;
        CommandSourceStack css = server.createCommandSourceStack();
        server.getPlayerList().broadcastChatMessage(PlayerChatMessage.system(msg), css, ChatType.bind(ChatType.CHAT, css));
    }



    public static class Message {
        public String type = "";
        public String from = "";
        public String to = "";
        public Map<String, String> args = new HashMap<>();

        private static Gson gson = new Gson();

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
            msg.args.put("request", "fuelLimit");
            return msg;
        }

        public static Message fromJson(String json) {
            return gson.fromJson(json, Message.class);
        }

        @Override
        public String toString() {
            return gson.toJson(this);
        }
    }
}
