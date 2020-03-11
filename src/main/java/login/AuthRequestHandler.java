package login;

import net.IClient;
import net.events.IMessageHandler;
import net.message.Message;
import net.message.MessageDecoder;

import java.io.IOException;

public class AuthRequestHandler implements IMessageHandler {

    LoginManager loginManager;


    public AuthRequestHandler(LoginManager loginManager) {
        this.loginManager = loginManager;
    }

    @Override
    public void handleMessage(Message message, IClient client) throws IOException {

        // serial decoding
        MessageDecoder md = new MessageDecoder(message);

        String username = md.readString(32);
        String password = md.readString(32);

        loginManager.handleAuthRequest(username, password, client);
    }


}