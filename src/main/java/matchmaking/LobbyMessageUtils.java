package matchmaking;

import net.message.Message;
import net.message.MessageEncoder;
import net.message.MessageType;

import java.io.IOException;
import java.util.Collection;

public class LobbyMessageUtils {


    public static Message buildLobbyListMessage(Collection<Lobby> lobbies) throws IOException {
        MessageEncoder me = new MessageEncoder(MessageType.LOBBY_LIST_RESPONSE);

        // TODO: maximum is 255 lobbies

        // 1 byte
        me.writeByte((byte) lobbies.size());

        // 41 bytes
        for (Lobby l : lobbies) {
            me.writeLong(l.getID());
            me.writeString(l.getName(), 32);
            me.writeByte((byte) l.getPlayers().size());
        }

        return me.getMessage();
    }

    public static Message buildLobbyDataMessage(long id, Collection<RatedPlayer> players) throws IOException {
        MessageEncoder me = new MessageEncoder(MessageType.LOBBY_DATA_RESPONSE);

        // 40 bytes
        me.writeLong(id);
        me.writeString("Lobby Name", 32);

        // 1 byte
        me.writeByte((byte) players.size());

        // n x 40 bytes
        for (RatedPlayer rp : players) {
            me.writeLong(rp.getID());
            me.writeString(rp.getName(), 32);
        }

        return me.getMessage();
    }

    public static Message buildAuthResponseMessage(ServerJoinService.AuthResponse responseCode) throws IOException {
        MessageEncoder me = new MessageEncoder(MessageType.AUTH_RESPONSE);

        me.writeByte(responseCode.getCode());

        return me.getMessage();
    }
}
