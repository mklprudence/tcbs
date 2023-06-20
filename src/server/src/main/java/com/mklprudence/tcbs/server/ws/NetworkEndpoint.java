package com.mklprudence.tcbs.server.ws;

import com.mklprudence.tcbs.server.ws.message.Message;
import com.mklprudence.tcbs.server.ws.message.MessageDecoder;
import com.mklprudence.tcbs.server.ws.message.MessageEncoder;
import com.mklprudence.tcbs.server.ws.message.MessageType;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ServerEndpoint(
        value = "/",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class
)
@Component
public class NetworkEndpoint {
    private static final Map<String, NetworkEndpoint> channelEndpoints = new ConcurrentHashMap<>(); // sessionID, NetworkEndpoint
    private static final Map<String, String> clients = new ConcurrentHashMap<>(); // clientID, sessionID

    private Session session;

    @OnOpen
    public void onOpen(Session session) throws IOException, EncodeException {
        this.session = session;
        channelEndpoints.put(session.getId(), this);
        sendMessage(Message.getReqInit());
        System.out.println("OPENED " + session.getId());
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws EncodeException, IOException {
        // Handle new messages
        System.out.printf("Session: %s, Message: %s%n", session.getId(), message.toString());

        // Branches that does not need INIT
        if (message.type == MessageType.INIT) {
            clients.put(message.from, session.getId());

            broadcast(clientID -> new Message()
                    .setType(MessageType.INFO)
                    .setFrom("server")
                    .setTo(clientID)
                    .addArg("request", "clients")
                    .addArg("namespace", "")
                    .addArg("list", String.join(" ", getConnectedClients(null))));
            return;
        }

        // INIT check
        if (!clients.containsValue(session.getId())) {
            sendMessage(Message.getReqInit());
            return;
        }

        // Check to
        if (!message.to.equals("server")) {
            if (message.to.equals("all")) {
                String fromSessionID = clients.get(message.from);
                channelEndpoints.entrySet().stream()
                        .filter(e -> !e.getKey().equals(fromSessionID))
                        .forEach(e -> {
                            try {
                                e.getValue().sendMessage(message);
                            } catch (EncodeException | IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                return;
            }

            if (clients.containsKey(message.to)) {
                channelEndpoints.get(clients.get(message.to)).sendMessage(message);
            } else {
                System.out.println("Referrer not present: " + message.to);
            }
            return;
        }

        // Branches that needs INIT
        if (message.type == MessageType.PING) {
            sendMessage(new Message()
                    .setType(MessageType.PONG)
                    .setFrom("server")
                    .setTo(getClientID())
            );
        } else if (message.type == MessageType.REQINFO) {
            if (!message.hasArg("request")) return;
            String request = message.getArg("request");
            System.out.printf("%s requested for {%s}%n", getClientID(), request);
            if (request.equals("clientID")) {
                sendMessage(new Message()
                        .setType(MessageType.INFO)
                        .setFrom("server")
                        .setTo(getClientID())
                        .addArg("request", "clientID")
                );
            } else if (request.equals("clients")) {
                String namespace = message.getArg("namespace");
                sendMessage(new Message()
                        .setType(MessageType.INFO)
                        .setFrom("server")
                        .setTo(getClientID())
                        .addArg("request", "clients")
                        .addArg("namespace", (namespace == null) ? "" : namespace)
                        .addArg("list", String.join(" ", getConnectedClients(namespace)))
                );
            }
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        // WebSocket connection closes
        String clientID = getClientID();
        String sessionID = session.getId();

        channelEndpoints.remove(sessionID);
        clients.remove(clientID);
        System.out.println("CLOSED " + sessionID);

        broadcast(id -> new Message()
                .setType(MessageType.INFO)
                .setFrom("server")
                .setTo(id)
                .addArg("request", "clients")
                .addArg("namespace", "")
                .addArg("list", String.join(" ", getConnectedClients(null))));
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        System.out.printf("Session: %s, Error: %s%n", session.getId(), throwable.toString());
    }

    public void sendMessage(Message msg) throws EncodeException, IOException {
        synchronized (session) {
            session.getBasicRemote().sendObject(msg);
        }
    }

    public static void broadcast(BroadcastMessageProvider provider) {
        clients.keySet().forEach(clientID -> {
            NetworkEndpoint endpoint = getEndpointFromClientID(clientID);
            if (endpoint == null) return;
            try {
                endpoint.sendMessage(provider.getMessage(clientID));
            } catch (IOException | EncodeException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Utils
    private String getClientID() {
        Optional<Map.Entry<String, String>> o = clients.entrySet().stream()
                .filter(e -> e.getValue().equals(session.getId()))
                .findFirst();
        if (o.isPresent()) return o.get().getKey();
        return session.getId();
    }

    private static NetworkEndpoint getEndpointFromSessionID(String sessionID) {
        return channelEndpoints.get(sessionID);
    }

    private static NetworkEndpoint getEndpointFromClientID(String clientID) {
        String sessionID = clients.get(clientID);
        if (sessionID == null) return null;
        return getEndpointFromSessionID(sessionID);
    }

    private static List<String> getConnectedClients(String namespace) {
        List<String> all = List.copyOf(clients.keySet());
        if (namespace == null) return all;
        if (namespace.equals("turtle") || namespace.equals("controller") || namespace.equals("spectator")) {
            return all.stream().filter(s -> s.split(":")[0].equals(namespace)).collect(Collectors.toList());
        }
        return all;
    }

    public interface BroadcastMessageProvider {
        Message getMessage(String clientID);
    }
}
