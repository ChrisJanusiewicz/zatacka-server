package net.tls;

import net.IClient;
import net.events.INetListener;
import net.message.Message;

import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class NioSslClient extends IClient {

    private static long lastID;
    private NioSslServer server;
    private SocketChannel socketChannel;
    private SSLEngine engine;
    private long id;


    public NioSslClient(NioSslServer server, SocketChannel socketChannel, SSLEngine engine) {
        this.server = server;
        this.socketChannel = socketChannel;
        this.engine = engine;
    }

    @Override
    public void setListener(INetListener listener) {
        //TODO
    }

    @Override
    public boolean sendMessage(Message message) {
        try {
            server.write(socketChannel, engine, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public SocketAddress getAddress() throws IOException {
        return socketChannel.getRemoteAddress();
    }

    @Override
    public boolean isConnected() {
        return socketChannel.isConnected();
    }

    @Override
    public void close() {
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public synchronized void assignID() {
        this.id = ++lastID;
    }
}
