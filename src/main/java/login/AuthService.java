package login;

import data.ClientLoginInfo;
import data.MapMediator;
import data.User;
import data.UserRepository;
import net.IClient;
import net.message.Message;
import net.message.MessageUtils;
import security.Crypto;

import java.io.IOException;
import java.net.SocketAddress;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;

public class AuthService implements Runnable {

    String username;
    String password;
    IClient client;
    private UserRepository userRepository;
    private Crypto crypto;
    private int loginTokenLength;
    private Duration loginTokenDuration; //how long before login token expires
    private MapMediator<String, ClientLoginInfo> mediator;

    public AuthService(UserRepository userRepository, MapMediator<String, ClientLoginInfo> mediator, Crypto crypto,
                       int loginTokenLength, Duration loginTokenDuration,
                       String username, String password, IClient client) {

        this.userRepository = userRepository;
        this.mediator = mediator;
        this.crypto = crypto;

        this.loginTokenLength = loginTokenLength;
        this.loginTokenDuration = loginTokenDuration;

        this.username = username;
        this.password = password;
        this.client = client;
    }

    @Override
    public void run() {
        User user;
        try {
            System.out.println(String.format("User: [name: '%s'\t password: '%s']", username, password));
            user = userRepository.getUserByName(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (user == null) {
            System.out.println("could not find user with that name in the database");
            return;
        }

        byte[] userHash;
        byte[] serverHash = user.getPasswordHash();

        try {
            userHash = crypto.hashPassword(password, user.getSalt());
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return;
        }


        if (MessageDigest.isEqual(userHash, serverHash)) {
            byte[] token = generateAuthToken();

            Message reply = null;
            try {
                reply = MessageUtils.buildAuthResponseMessage(token);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            client.sendMessage(reply);

            SocketAddress address = null;
            try {
                address = client.getAddress();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            LocalDateTime expiry = LocalDateTime.now().plusSeconds(loginTokenDuration.getSeconds());

            ClientLoginInfo cli = new ClientLoginInfo(username, token, address, expiry);

            mediator.put(username, cli);

        } else {
            // TODO: construct reply informing of incorrect credentials
        }


    }

    public byte[] generateAuthToken() {
        SecureRandom random = new SecureRandom();
        byte[] token = new byte[loginTokenLength];
        random.nextBytes(token);
        return token;
    }


}
