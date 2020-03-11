package matchmaking;

import net.IClient;
import net.events.IMessageHandler;
import net.message.Message;
import net.message.MessageDecoder;

import java.io.IOException;

public class LobbyAuthRequestHandler implements IMessageHandler {

    MatchmakingManager matchmakingManager;

    public LobbyAuthRequestHandler(MatchmakingManager matchmakingManager) {
        this.matchmakingManager = matchmakingManager;
    }


    @Override
    public void handleMessage(Message message, IClient client) throws IOException {

        // serial decoding
        MessageDecoder md = new MessageDecoder(message);

        String username = md.readString(32);

        // subtract username length from total data length to get token length
        int tokenLength = message.getDataLength() - 32;
        byte[] token = new byte[tokenLength];
        md.readBytes(token, tokenLength);

        matchmakingManager.handleServerJoinRequest(username, token, client);
    }
}
