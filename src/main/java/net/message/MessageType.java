package net.message;

public enum MessageType {

    AUTH_REQUEST(8),
    AUTH_RESPONSE(9),
    JOIN_SERVER_REQUEST(10),
    JOIN_SERVER_RESPONSE(11),

    LOBBY_REQUEST(16),
    LOBBY_LIST_RESPONSE(17),
    LOBBY_DATA_RESPONSE(18),


    GAME_STATE(32),
    GAME_PLAYER_UPDATE(33), // Sent by client and broadcast by server
    GAME_PLAYER_STATE(34),

    UNKNOWN(255);

    private byte code;

    MessageType(int code) {
        if (code > 255) {
            String errorMessage = String.format("Message code is described by byte; max value 255. Actual: %d", code);
            throw new IllegalArgumentException(errorMessage);
        }
        this.code = (byte) code;
    }

    public static MessageType fromByte(byte code) {
        for (MessageType messageType : values()) {
            if (messageType.code == code) {
                return messageType;
            }
        }
        return UNKNOWN;
    }

    public byte getCode() {
        return code;
    }
}

