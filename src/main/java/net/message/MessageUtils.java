package net.message;

import java.io.IOException;

public class MessageUtils {

    // Writes an int to a byte array (big endian)
    public static void writeIntToByteArray(byte[] dstArray, int pos, int i) {
        for (int j = 0; j < 4; j++) {
            dstArray[pos + j] = (byte) (i >> (8 * (3 - j)));
        }
    }

    public static Message buildAuthResponseMessage(byte[] token) throws IOException {
        MessageEncoder me = new MessageEncoder(MessageType.AUTH_RESPONSE);
        me.writeBytes(token);
        return me.getMessage();
    }


}
