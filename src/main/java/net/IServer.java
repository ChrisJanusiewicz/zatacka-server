package net;

import net.events.INetListener;

public interface IServer {

    public void start(int port);

    public void stop();

    public void setListener(INetListener listener);


}
