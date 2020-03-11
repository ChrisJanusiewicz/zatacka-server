package net.tcp;

import net.IClient;
import net.events.INetListener;
import net.message.Message;
import net.message.MessageType;

import java.io.*;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioClient extends IClient {

    private static long lastID;
    private long id;
    private INetListener listener;
    private SocketChannel sc;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public NioClient(SocketChannel sc) {
        this.sc = sc;
        id = -1;

        try {
            sc.socket().setTcpNoDelay(true);
            dataInputStream = new DataInputStream(new BufferedInputStream(sc.socket().getInputStream()));
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(sc.socket().getOutputStream()));


        } catch (IOException e) {
            e.printStackTrace();
            // TODO: close connection?
        }
    }

    @Override
    public SocketAddress getAddress() throws IOException {
        if (sc.isOpen()) {
            return sc.getRemoteAddress();
        } else {
            return null;
        }
    }

    public String getIP() throws IOException {
        if (sc.isOpen()) {
            return sc.getRemoteAddress().toString();
        } else {
            return "disconnected";
        }
    }

    public void receiveData(INetListener serverListener) throws IOException {

        int dataAvailable;
        while ((dataAvailable = dataInputStream.available()) > 5) {
            ByteBuffer bbHeader = ByteBuffer.allocate(5);
            sc.read(bbHeader);
            bbHeader.flip();

            byte messageCode = bbHeader.get();
            MessageType messageType = MessageType.fromByte(messageCode);
            int dataLength = bbHeader.getInt();

            System.out.println(String.format("Data available: %d\tMessageCode: %X\tType: %s\tLength: %d",
                    dataAvailable, messageCode, messageType, dataLength));

            if (messageType == MessageType.UNKNOWN) {
                System.out.println(String.format("closing socket, unsupported message received (%d)", messageCode));
                sc.close();
                return;
            }

            ByteBuffer data = ByteBuffer.allocate(dataLength);
            sc.read(data);

            Message message = new Message(messageType, data.array());

            serverListener.onReceive(message, this);

            if (listener != null) {
                try {
                    listener.onReceive(message, this);
                } catch (Exception e) {

                    System.err.println("Could not handle message\n" + e);
                }
            }
        }
    }


    @Override
    public void setListener(INetListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean sendMessage(Message message) {
        try {
            sc.write(ByteBuffer.wrap(message.getBytes()));

            System.out.println("sent message to client " + message.toString());

        } catch (IOException e) {
            System.err.println("Could not send message to client");
            return false;
        }
        return true;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void close() {
        // TODO : implement
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public synchronized void assignID() {
        id = ++lastID;
    }
}
