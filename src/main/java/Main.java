import config.*;
import data.ClientLoginInfo;
import data.DBManager;
import data.MapMediator;
import data.UserRepository;
import game.GameManager;
import login.AuthRequestHandler;
import login.LoginManager;
import matchmaking.LobbyAuthRequestHandler;
import matchmaking.LobbyRequestHandler;
import matchmaking.MatchmakingManager;
import net.events.DistributorListener;
import net.events.MessageDistributor;
import net.message.MessageType;
import net.tcp.NioServer;
import net.tls.NioSslServer;
import security.Crypto;


public class Main {

    public static void main(String[] args) {

        System.out.println("Initialising...");

        IGameProperties gameProperties = ProjectProperties.getInstance();

        MapMediator<String, ClientLoginInfo> loginMediator = new MapMediator<String, ClientLoginInfo>();


        GameManager gameManager = new GameManager(
                gameProperties.get_MAP_WIDTH(),
                gameProperties.get_MAP_HEIGHT(), 2);


        try {


            IDBProperties dbProperties = ProjectProperties.getInstance();
            ICryptoProperties cryptoProperties = ProjectProperties.getInstance();

            DBManager dbManager = new DBManager(dbProperties.get_DB_URL());
            UserRepository userRepository = new UserRepository(dbManager.getConn());

            MatchmakingManager matchmakingManager = new MatchmakingManager(gameManager, userRepository, loginMediator);

            NioServer gameServer = NioServer.getInstance();

            Crypto crypto = new Crypto(
                    cryptoProperties.get_KEYSTORE_LOCATION(),
                    cryptoProperties.get_PASSWORD_HASH_ITERATIONS(),
                    cryptoProperties.get_PASSWORD_KEY_LENGTH());

            NioSslServer loginServer = new NioSslServer(
                    "TLSv1.2",
                    crypto.kmf.getKeyManagers(),
                    crypto.tmf.getTrustManagers(),
                    1025);

            loginServer.start(1025);

            IServerProperties serverProperties = ProjectProperties.getInstance();

            gameServer.start(serverProperties.get_GAME_SERVER_PORT());


            LoginManager lc = new LoginManager(
                    userRepository, loginMediator, crypto,
                    serverProperties.get_LOGIN_TOKEN_LENGTH(),
                    serverProperties.get_LOGIN_TOKEN_DURATION());


            MessageDistributor tlsDistributor = new MessageDistributor();
            MessageDistributor tcpDistributor = new MessageDistributor();

            tlsDistributor.addHandler(MessageType.AUTH_REQUEST, new AuthRequestHandler(lc));
            tcpDistributor.addHandler(MessageType.JOIN_SERVER_REQUEST, new LobbyAuthRequestHandler(matchmakingManager));
            tcpDistributor.addHandler(MessageType.LOBBY_REQUEST, new LobbyRequestHandler(matchmakingManager));

            loginServer.setListener(new DistributorListener(tlsDistributor));
            gameServer.setListener(new DistributorListener(tcpDistributor));


        } catch (Exception e) {
            e.printStackTrace();

        }
        System.out.println("Initialisation complete");
    }
}
