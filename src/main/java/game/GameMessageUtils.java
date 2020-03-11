package game;

import net.message.Message;
import net.message.MessageEncoder;
import net.message.MessageType;

import java.io.IOException;

public class GameMessageUtils {
    public static Message buildGameStateMessage(GameState gameState) throws IOException {
        MessageEncoder me = new MessageEncoder(MessageType.GAME_STATE);

        // 1 byte
        me.writeByte(gameState.getCode());

        return me.getMessage();
    }

    public static Message buildPlayerUpdateMessage() {
        // TODO

        return null;
    }
}
