package matchmaking;

import data.ClientLoginInfo;
import data.MapMediator;
import net.IClient;
import net.message.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.time.LocalDateTime;

public class ServerJoinService implements Runnable {

    private MatchmakingManager manager;
    private MapMediator<String, ClientLoginInfo> mediator;
    private String username;
    private byte[] token;
    private IClient client;

    public ServerJoinService(MatchmakingManager manager, MapMediator<String, ClientLoginInfo> mediator,
                             String username, byte[] token, IClient client) {
        this.manager = manager;
        this.mediator = mediator;
        this.username = username;
        this.token = token;
        this.client = client;
    }

    public void run() {

        ClientLoginInfo cli = mediator.query(username);

        if (cli == null) {
            System.out.println("No such user reported by login server...");
            return;
        }

        if (cli.expiry.isBefore(LocalDateTime.now())) {
            System.out.println("Token has expired...");
            mediator.remove(username);
            return;
        }

        InetAddress clientIP = null;
        try {
            clientIP = ((InetSocketAddress) client.getAddress()).getAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InetAddress savedIP = ((InetSocketAddress) cli.address).getAddress();

        if (!savedIP.equals(clientIP)) {
            System.out.println(String.format("IP MISMATCH: man in the middle attack for user: %s", username));
            return;
        }

        if (!MessageDigest.isEqual(token, cli.token)) {
            System.out.println(String.format("Token mismatch for user [%s]...", username));
        }


        System.out.println(String.format("Token matches. User [%s] successfully joined game server", username));
        mediator.remove(username);

        client.assignID();
        manager.joinPlayer(client, username);

        try {
            Message message = LobbyMessageUtils.buildAuthResponseMessage(AuthResponse.AUTHENTICATED);
            client.sendMessage(message);

        } catch (IOException e) {
            System.out.println("Error constructing auth response message");
        }

    }

    public enum AuthResponse {

        AUTHENTICATED(1),
        ALREADY_AUTHENTICATED(2),
        BAD_CREDENTIALS(3),
        UNAUTHENTICATED(4);

        private byte code;

        AuthResponse(int value) {
            if (value > 255) {
                String errorMessage = String.format("AuthResponse is described by byte; max value 255. Actual: %d", value);
                throw new IllegalArgumentException(errorMessage);
            }
            this.code = (byte) value;
        }

        public byte getCode() {
            return code;
        }

    }
}
