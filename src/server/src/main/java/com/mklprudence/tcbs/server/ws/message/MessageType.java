package com.mklprudence.tcbs.server.ws.message;

public enum MessageType {
    INVALID, // Invalid message syntax
    INIT, // Client-specific
    REQINIT, COMMAND, // Server-specific
    PING, PONG, REQINFO, INFO // Both
}
