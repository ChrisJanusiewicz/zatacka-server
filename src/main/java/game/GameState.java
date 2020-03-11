package game;

public enum GameState {
    ACTIVE(1),  // round active
    PAUSED(2),  // inbetween rounds
    END(3),     // game finished

    UNKNOWN(255);

    private byte code;

    GameState(int code) {
        if (code > 255) {
            String errorMessage = String.format("GameState code is described by byte; max value 255. Actual: %d", code);
            throw new IllegalArgumentException(errorMessage);
        }
        this.code = (byte) code;
    }

    public static GameState fromByte(byte code) {
        for (GameState messageType : values()) {
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