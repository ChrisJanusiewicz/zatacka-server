package login;

import data.ClientLoginInfo;
import data.MapMediator;
import data.UserRepository;
import net.IClient;
import security.Crypto;

import java.io.IOException;
import java.time.Duration;

public class LoginManager {

    private Crypto crypto;
    private UserRepository userRepository;
    private MapMediator<String, ClientLoginInfo> mediator;

    private int loginTokenLength;
    private Duration loginTokenDuration; //how long before login token expires

    public LoginManager(UserRepository userRepository,
                        MapMediator<String, ClientLoginInfo> mediator,
                        Crypto crypto,
                        int loginTokenLength, Duration loginTokenDuration) {

        this.userRepository = userRepository;
        this.mediator = mediator;
        this.crypto = crypto;
        this.loginTokenLength = loginTokenLength;
        this.loginTokenDuration = loginTokenDuration;
    }

    public void handleAuthRequest(String username, String password, IClient client) throws IOException {

        AuthService authService = new AuthService(
                userRepository, mediator, crypto,
                loginTokenLength, loginTokenDuration,
                username, password, client);

        Thread serviceThread = new Thread(authService);
        serviceThread.start();
    }

}
