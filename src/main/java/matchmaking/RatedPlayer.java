package matchmaking;

import data.User;
import net.IClient;
import net.message.Message;

public class RatedPlayer {

    private IClient client;
    private User user;

    public RatedPlayer(User user, IClient client) {
        this.user = user;
        this.client = client;

    }

    public IClient getClient() {
        return client;
    }

    public long getID() {
        return client.getID();
    }

    public boolean sendMessage(Message message) {
        return client.sendMessage(message);
    }

    public String getName() {
        return user.getUserName();
    }
}
