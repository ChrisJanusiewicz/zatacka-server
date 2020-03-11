package net.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Message {

    private MessageType messageType;
    private byte[] data;
    private int dataLength;

    public Message(MessageType messageType, byte[] data) {
        this.messageType = messageType;
        this.data = data;
        dataLength = data.length;
    }

    public static Message fromBuffer(ByteBuffer buffer) throws IOException {
        // header
        MessageType messageType = MessageType.fromByte(buffer.get());
        int dataLength = buffer.getInt();

        if (messageType == MessageType.UNKNOWN) {
            System.out.println("bad packet type");
            return null;
        } else {
            System.out.println(messageType);
        }

        // TODO: allow for project specific max message size instead of 2048
        if (dataLength > 2048 || dataLength > buffer.remaining()) {
            System.out.println(String.format("Message overflow. Length: %d", dataLength));
            return null;
        }

        // data
        byte[] data = new byte[dataLength];
        buffer.get(data, 0, dataLength);

        return new Message(messageType, data);
    }

    public static Message fromStream(final DataInputStream in) throws IOException {
        //header
        final MessageType messageType = MessageType.fromByte(in.readByte());
        final int dataLength = in.readInt();

        if (messageType == MessageType.UNKNOWN) {
            System.out.println("bad packet type");
            return null;
        } else {
            System.out.println(messageType);
        }
        if (dataLength > 1024) {
            System.out.println(String.format("Message overflow. Length: %d", dataLength));
            return null;
        }

        //data
        final byte[] data = new byte[dataLength];
        in.readFully(data);

        return new Message(messageType, data);
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public int getDataLength() {
        return dataLength;
    }

    public byte[] getData() {
        return data;
    }

    public void write(final DataOutputStream out) throws IOException {
        // Packet Type
        out.writeByte(messageType.getCode());

        // Data Length
        out.writeInt(dataLength);

        // Data
        out.write(data);
    }

    public byte[] getBytes() {
        byte[] output = new byte[5 + dataLength];

        output[0] = messageType.getCode();

        MessageUtils.writeIntToByteArray(output, 1, dataLength);

        System.arraycopy(data, 0, output, 5, dataLength);

        return output;
    }

    @Override
    public String toString() {
        return String.format("Message: [MessageType: %s, Length: %d]", messageType, dataLength);
    }
}