package matchmaking;

import net.IClient;
import net.events.IMessageHandler;
import net.message.Message;
import net.message.MessageDecoder;

import java.io.IOException;

public class LobbyRequestHandler implements IMessageHandler {

    MatchmakingManager matchmakingManager;

    public LobbyRequestHandler(MatchmakingManager matchmakingManager) {
        this.matchmakingManager = matchmakingManager;
    }


    @Override
    public void handleMessage(Message message, IClient client) throws IOException {

        // serial decoding
        MessageDecoder md = new MessageDecoder(message);

        byte actionCode = md.readByte();
        LobbyAction action = LobbyAction.fromByte(actionCode);
        Long id = md.readLong();


        switch (action) {
            case LIST:
                matchmakingManager.sendLobbyList(client);
                break;
            case JOIN:
                matchmakingManager.joinLobby(id, client);
                break;
            case CREATE:
                matchmakingManager.createLobby(client);
                break;
            case LAUNCH:
                matchmakingManager.launchLobby(client);
                break;
            default:
                System.out.println(String.format("Unsupported lobby action: %X\t%s", actionCode, action));
        }
    }


}
