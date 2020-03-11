package net;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


public class Decoder {

    ByteBuffer bb;

    public Decoder(ByteBuffer bb) {
        this.bb = bb;
    }

    public String readString(int length) {
        byte[] strBuffer = new byte[length];
        bb.get(strBuffer, 0, length);
        return new String(strBuffer, StandardCharsets.UTF_8).trim();
    }


    public MessageHeader getNext() {
        return MessageHeader.fromByteBuffer(bb);
    }


}