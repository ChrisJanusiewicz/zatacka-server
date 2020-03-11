package net.events;

import net.IClient;
import net.message.Message;

import java.io.IOException;

public interface INetListener {

    public void onConnect(IClient client);

    public void onDisconnect(IClient client);

    public void onReceive(Message message, IClient client) throws IOException;

}
