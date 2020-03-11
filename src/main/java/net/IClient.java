package net;

import net.events.INetListener;
import net.message.Message;
import util.Identifiable;

import java.io.IOException;
import java.net.SocketAddress;

public abstract class IClient extends Identifiable {

    public abstract void setListener(INetListener listener);

    public abstract boolean sendMessage(Message message);

    public abstract SocketAddress getAddress() throws IOException;

    public abstract boolean isConnected();

    public abstract void close();

    public abstract long getID();

    public abstract void assignID();

}
