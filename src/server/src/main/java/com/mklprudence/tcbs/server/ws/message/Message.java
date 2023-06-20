package com.mklprudence.tcbs.server.ws.message;

import jakarta.websocket.EncodeException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Message {
    public MessageType type = MessageType.INVALID;
    public String from = "";
    public String to = "";
    public Map<String, String> args = new HashMap<>();

    // Getters & Setters
    public Message setType(MessageType type) {
        this.type = type;
        return this;
    }

    public Message setFrom(String from) {
        this.from = from;
        return this;
    }

    public Message setTo(String to) {
        this.to = to;
        return this;
    }

    public boolean hasArg(String key) {
        return args.containsKey(key);
    }

    public String getArg(String key) {
        return args.get(key);
    }

    public Message addArg(String key, String val) {
        this.args.put(key, val);
        return this;
    }



    // Default messages
    public static Message getReqInit() {
        return new Message()
                .setType(MessageType.REQINIT)
                .setFrom("server")
                .setTo("UNKNOWN");
    }



    // enumeration
    @Override
    public String toString() {
        try {
            return new MessageEncoder().encode(this);
        } catch (EncodeException e) {
            throw new RuntimeException(e);
        }
    }

    public String enumerate(boolean print) {
        String out = "";
        out += String.format("Type: %s\n", type);
        out += String.format("From: %s\n", from);
        out += String.format("  To: %s\n", to);
        out += "Args: \n";
        out += args.entrySet().stream().map((e) -> String.format("    - %s : %s", e.getKey(), e.getValue())).collect(Collectors.joining());
        if (print) System.out.println(out);
        return out;
    }
}
