package game;

import net.IClient;
import net.message.Message;
import util.Vector2Int;

public class Player {

    public Vector2Int lastPos;
    public boolean isAlive;
    public byte score;
    private IClient client;
    private byte playerID;

    public Player(IClient client, byte playerID) {
        this.client = client;
        this.playerID = playerID;
        isAlive = true;
        score = 0;
    }

    public IClient getClient() {
        return client;
    }

    public void kill() {
        isAlive = false;
    }

    public byte getID() {
        return playerID;
    }

    public void sendMessage(Message message) {
        client.sendMessage(message);
    }

}