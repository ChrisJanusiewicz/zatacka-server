package net;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


public class Codec {

    protected ByteBuffer bb;
    byte nextID;
    int nextLen; // length of the data of the next packet in bytes
    private int size;  // maximum size of buffer in bytes

    // encoding
    public Codec(int size) {
        bb = ByteBuffer.allocate(size);
        this.size = size;
        nextID = 0;
    }

    // decoding
    public Codec(ByteBuffer bb) {
        this.bb = bb;
        size = bb.limit();
        nextID = 0;
    }

    public boolean isFull() {
        // to be used when writing/encoding
        return bb.position() >= size;
    }

    public byte hasNext() {
        // attempt to read header here
        if (bb.position() + 6 >= bb.limit()) {
            // header takes up 7 bytes. If les than 7 bytes are available, there's no messages left
            return 0;
        }

        // read header
        nextLen = (bb.getShort() - 7);
        int messageID = bb.getInt();
        nextID = bb.get();

        return nextID;
    }


    /*
    public MessageType next() {
        // header structure
        //short length = readShort(position);
        //int packetid = readInt(position + 2);
        MessageType messageType = MessageType.fromByte(buffer[position + 6]);
        return messageType;
    }*/

    protected void putString(String value, int maxLength) {
        int position = bb.position();
        value = value == null ? "empty" : value;
        byte[] strBuffer = value.getBytes(StandardCharsets.UTF_8);
        if (strBuffer.length > maxLength) {
            System.err.println(String.format("Exceeded maximum string length for encoding: %s", value));
            bb.put(strBuffer, 0, Math.min(maxLength - 1, strBuffer.length));
        } else {
            bb.put(strBuffer, 0, Math.min(maxLength - 1, strBuffer.length));

        }
        bb.position(position + maxLength);
    }

    protected String readString(int length) {
        byte[] strBuffer = new byte[length];
        bb.get(strBuffer, 0, length);
        return new String(strBuffer, StandardCharsets.UTF_8).trim();
    }

    //reads the whole of the data of the next packet
    public byte[] readNext() {
        byte[] res = new byte[nextLen];
        bb.get(res, 0, nextLen);

        nextID = 0;
        nextLen = 0;
        return res;
    }

/*
    public enum MessageType {

        REQUEST_LOBBY,
        LOBBY_LIST,
        ERROR;
        public static MessageType fromByte(byte messageType) {
            switch (messageType) {
                case 16: return REQUEST_LOBBY;
                case 17: return LOBBY_LIST;
                default: return ERROR;
            }
        }
    }*/
}
