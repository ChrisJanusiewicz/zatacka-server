package net.tls;

import net.IServer;
import net.events.INetListener;
import net.message.Message;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NioSslServer extends NioSslPeer implements IServer {

    private InetSocketAddress address;
    private boolean active;
    private SSLContext context;
    private Selector selector;
    private INetListener listener;

    private Map<SocketChannel, NioSslClient> clientMap;

    public NioSslServer(String protocol, KeyManager[] keyManagers, TrustManager[] trustManagers, int port) throws Exception {

        clientMap = new HashMap<SocketChannel, NioSslClient>();

        //address = new InetSocketAddress(hostAddress, port);
        address = new InetSocketAddress(port);

        context = SSLContext.getInstance(protocol);
        context.init(keyManagers, trustManagers, new SecureRandom());

        SSLSession dummySession = context.createSSLEngine().getSession();
        myAppData = ByteBuffer.allocate(dummySession.getApplicationBufferSize());
        myNetData = ByteBuffer.allocate(dummySession.getPacketBufferSize());
        peerAppData = ByteBuffer.allocate(dummySession.getApplicationBufferSize());
        peerNetData = ByteBuffer.allocate(dummySession.getPacketBufferSize());
        dummySession.invalidate();

        selector = SelectorProvider.provider().openSelector();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(address);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        active = true;

    }

    private void mainLoop() {

        log("Ready to accept incoming connections on %s", 2, address);

        while (isActive()) {
            try {
                selector.select();
                Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = selectedKeys.next();
                    selectedKeys.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        accept(key);
                    } else if (key.isReadable()) {
                        read((SocketChannel) key.channel(), (SSLEngine) key.attachment());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start(int port) {
        ServerRunnable serverRunnable = new ServerRunnable();
        Thread mainThread = new Thread(serverRunnable);

        mainThread.start();
    }

    public void stop() {
        active = false;
        executor.shutdown();
        selector.wakeup();
    }

    @Override
    public void setListener(INetListener listener) {
        this.listener = listener;
    }

    private void accept(SelectionKey key) throws Exception {

        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        socketChannel.configureBlocking(false);

        log("New connection request: %s", 2, socketChannel.getRemoteAddress());

        SSLEngine engine = context.createSSLEngine();
        engine.setUseClientMode(false);
        engine.beginHandshake();

        if (doHandshake(socketChannel, engine)) {
            socketChannel.register(selector, SelectionKey.OP_READ, engine);

            NioSslClient client = new NioSslClient(this, socketChannel, engine);
            clientMap.put(socketChannel, client);
            listener.onConnect(client);

        } else {
            socketChannel.close();
            log("Connection closed due to handshake failure.", 2);
        }
    }

    @Override
    protected void read(SocketChannel socketChannel, SSLEngine engine) throws IOException {
        try {
            peerNetData.clear();
            int bytesRead = socketChannel.read(peerNetData);
            if (bytesRead > 0) {
                peerNetData.flip();
                while (peerNetData.hasRemaining()) {
                    peerAppData.clear();
                    SSLEngineResult result = engine.unwrap(peerNetData, peerAppData);
                    switch (result.getStatus()) {
                        case OK:
                            peerAppData.flip();
                            break;
                        case BUFFER_OVERFLOW:
                            peerAppData = enlargeApplicationBuffer(engine, peerAppData);
                            break;
                        case BUFFER_UNDERFLOW:
                            peerNetData = handleBufferUnderflow(engine, peerNetData);
                            break;
                        case CLOSED:
                            closeConnection(socketChannel, engine);
                            return;
                        default:
                            throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
                    }
                }


            } else if (bytesRead < 0) {
                System.out.println("Received end of stream. Attempting to close connection with client...");
                handleEndOfStream(socketChannel, engine);
            }

            if (bytesRead > 0) {
                handleDataReceived(peerAppData, socketChannel, engine);
            }

        } catch (SocketException e) {
            socketChannel.socket().close();

            //TODO: close socket and deregister
        }
    }

    private void handleDataReceived(ByteBuffer peerAppData, SocketChannel socketChannel, SSLEngine sslEngine) {

        NioSslClient client = clientMap.get(socketChannel);

        try {
            Message message = Message.fromBuffer(peerAppData);

            listener.onReceive(message, client);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void write(SocketChannel socketChannel, SSLEngine engine, byte[] message) throws IOException {

        myAppData.clear();
        myAppData.put(message);
        myAppData.flip();
        while (myAppData.hasRemaining()) {
            // The loop has a meaning for (outgoing) messages larger than 16KB.
            // Every wrap call will remove 16KB from the original message and send it to the remote peer.
            myNetData.clear();
            SSLEngineResult result = engine.wrap(myAppData, myNetData);
            switch (result.getStatus()) {
                case OK:
                    myNetData.flip();
                    while (myNetData.hasRemaining()) {
                        socketChannel.write(myNetData);
                    }
                    break;
                case BUFFER_OVERFLOW:
                    myNetData = enlargePacketBuffer(engine, myNetData);
                    break;
                case BUFFER_UNDERFLOW:
                    throw new SSLException("Buffer underflow occurred after a wrap");
                case CLOSED:
                    closeConnection(socketChannel, engine);
                    return;
                default:
                    throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
            }
        }
    }

    private boolean isActive() {
        return active;
    }

    private void log(String message, int messageSeverity) {
        //if (messageSeverity >= loggingSeverity) {
        System.out.println("[TLS Server]: " + message);
        // }
    }

    private void log(String message, int messageSeverity, Object... args) {
        log(String.format(message, args), messageSeverity);
    }

    private class ServerRunnable implements Runnable {
        public void run() {
            mainLoop();
        }
    }

}