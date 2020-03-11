package net.tcp;

import net.IServer;
import net.events.INetListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class NioServer implements IServer {

    private static NioServer instance;
    private InetSocketAddress serverAddress;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private INetListener listener;
    private Map<SocketChannel, NioClient> clientMap;
    private boolean interrupted = false;

    public NioServer() {
        clientMap = new HashMap<SocketChannel, NioClient>();

    }

    public static NioServer getInstance() {
        if (instance == null) {
            instance = new NioServer();
        }
        return instance;
    }

    @Override
    public void start(int port) {

        serverAddress = new InetSocketAddress(port);

        try {

            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();

            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(serverAddress);

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            // Selector is now ready to intercept a client socket connection and relay to the server socket channel

            //log("Ready to accept incoming connections on %s", 2, serverSocketChannel.getLocalAddress());

            ServerRunnable serverRunnable = new ServerRunnable();
            Thread mainThread = new Thread(serverRunnable);
            mainThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        interrupted = true;
    }

    @Override
    public void setListener(INetListener listener) {
        this.listener = listener;
    }

    private void mainLoop() throws IOException, InterruptedException {
        SelectionKey key = null;
        System.out.println(String.format("[TCP Server]: Ready to accept incoming connections on %s", serverAddress));

        while (!interrupted) {

            // TODO: not sure if this if shouldn't be changed
            // select is blocking and it returns 0 only if we didn't handle all the data
            // from the previous select, so we shouldn't continue but
            if (selector.select() == 0) {
                // Number of channels that have I/O activity: 0
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    processConnectionRequest();
                }
                if (key.isReadable()) {
                    SocketChannel sc = (SocketChannel) key.channel();

                    try {

                        boolean ok = processDataReceived(sc);

                        // If the connection is dead, then remove it from the selector and close it
                        if (!ok) {
                            key.cancel();
                            sc.close();
                        }

                    } catch (IOException ie) {

                        // On exception, remove this channel from the selector
                        key.cancel();

                        try {
                            sc.close();
                        } catch (IOException ie2) {
                            ie2.printStackTrace();
                        }

                        System.out.println("Closed " + sc);
                    }
                }
            }
        }
    }

    private boolean processDataReceived(SocketChannel sc) throws IOException {

        NioClient client = clientMap.get(sc);

        if (client != null) {
            client.receiveData(listener);

        }

        return true;
    }

    private void processConnectionRequest() {
        try {
            SocketChannel sc = serverSocketChannel.accept();
            sc.configureBlocking(false);
            NioClient client = new NioClient(sc);
            clientMap.put(sc, client);

            listener.onConnect(client);

            sc.register(selector, SelectionKey.OP_READ, client);
            //log("Connection Accepted: "  + sc.getRemoteAddress(), 2);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ServerRunnable implements Runnable {
        public void run() {
            try {
                mainLoop();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
