package game;

import net.IClient;
import net.events.IMessageHandler;
import net.message.Message;
import net.message.MessageDecoder;
import util.Vector2Int;

import java.io.IOException;

public class PlayerUpdateHandler implements IMessageHandler {

    private ZatackaGame game;

    public PlayerUpdateHandler(ZatackaGame game) {
        this.game = game;
    }

    @Override
    public void handleMessage(Message message, IClient client) throws IOException {

        System.out.println("handling client message on thread " + Thread.currentThread().getId());

        PlayerUpdate pu = decodePlayerUpdateMessage(message, client);

        game.handlePlayerUpdate(pu, client);
    }

    private PlayerUpdate decodePlayerUpdateMessage(Message message, IClient client) {

        // serial decoding
        MessageDecoder md = new MessageDecoder(message);

        byte updateCount = md.readByte();

        Vector2Int[] path = new Vector2Int[updateCount];
        for (byte i = 0; i < updateCount; i++) {
            path[i] = new Vector2Int(md.readInt(), md.readInt());
        }

        byte jumpCount = md.readByte();
        byte[] jumpindices = new byte[jumpCount];
        for (byte i = 0; i < jumpCount; i++) {
            jumpindices[i] = md.readByte();
        }

        byte playerID = game.getPlayerID(client);

        return new PlayerUpdate(playerID, path, jumpindices);
    }

}
