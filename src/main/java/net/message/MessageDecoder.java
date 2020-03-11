package net.message;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class MessageDecoder {

    Message message;
    ByteBuffer buffer;

    public MessageDecoder(Message message) {
        this.message = message;
        buffer = ByteBuffer.wrap(message.getData());
    }

    public String readString(int length) {
        byte[] strBuffer = new byte[length];
        buffer.get(strBuffer, 0, length);
        return new String(strBuffer, StandardCharsets.UTF_8).trim();
    }

    public long readLong() {
        return buffer.getLong();
    }

    public int readInt() {
        return buffer.getInt();
    }

    public void readBytes(byte[] dst, int length) {
        buffer.get(dst, 0, length);
    }

    public byte readByte() {
        return buffer.get();
    }
}
