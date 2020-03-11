package data;

import java.net.SocketAddress;
import java.time.LocalDateTime;

public class ClientLoginInfo {
    public String username;
    public byte[] token;
    public SocketAddress address;
    public LocalDateTime expiry;

    public ClientLoginInfo(String username, byte[] token, SocketAddress address, LocalDateTime expiry) {
        this.username = username;
        this.token = token;
        this.address = address;
        this.expiry = expiry;
    }
}