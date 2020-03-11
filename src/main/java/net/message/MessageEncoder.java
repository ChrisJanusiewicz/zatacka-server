package net.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MessageEncoder {

    private MessageType messageType;
    private ByteArrayOutputStream byteArrayOutputStream;
    private DataOutputStream dataOutputStream;

    public MessageEncoder(MessageType messageType) {
        this.messageType = messageType;
        byteArrayOutputStream = new ByteArrayOutputStream();
        dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    }

    public void writeByte(byte b) throws IOException {
        dataOutputStream.write(b);
    }

    public void writeString(String str, int length) throws IOException {
        // TODO
        dataOutputStream.write(str.getBytes());
    }

    public void writeBytes(byte[] buffer) throws IOException {
        dataOutputStream.write(buffer, 0, buffer.length);
    }

    public void writeBytes(byte[] buffer, int offset, int length) throws IOException {
        dataOutputStream.write(buffer, offset, length);
    }

    public void writeLong(long v) throws IOException {
        dataOutputStream.writeLong(v);
    }

    public Message getMessage() {
        try {
            dataOutputStream.close();
        } catch (final IOException e) {
            System.out.println(String.format("Unable to build packet: %s:%s", e.getClass(), e.getMessage()));
        }
        return new Message(messageType, byteArrayOutputStream.toByteArray());
    }
}
