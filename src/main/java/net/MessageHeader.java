package net;

import java.nio.ByteBuffer;

public class MessageHeader {

    private short messageLength;
    private byte messageType;


    private MessageHeader(ByteBuffer bb) {
        messageLength = bb.getShort();
        messageType = bb.get();
    }

    public static MessageHeader fromByteBuffer(ByteBuffer bb) {
        if (bb.limit() < 3) {
            return null;
        }
        return new MessageHeader(bb);
    }

    public short getMessageLength() {
        return messageLength;
    }

    public byte getMessageType() {
        return messageType;
    }

}

