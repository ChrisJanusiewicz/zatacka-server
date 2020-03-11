package matchmaking;

public enum LobbyAction {
    LIST(1),
    JOIN(2),
    CREATE(3),
    LAUNCH(4),
    UNKNOWN(255);

    private byte code;

    LobbyAction(int code) {
        if (code > 255) {
            String errorMessage = String.format("LobbyAction code is described by byte; max value 255. Actual: %d", code);
            throw new IllegalArgumentException(errorMessage);
        }
        this.code = (byte) code;
    }

    public static LobbyAction fromByte(byte code) {
        for (LobbyAction messageType : values()) {
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
