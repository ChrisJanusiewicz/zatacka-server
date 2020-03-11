package net.events;

import net.IClient;
import net.message.Message;

import java.io.IOException;

public interface IMessageHandler {
    void handleMessage(Message message, IClient client) throws IOException;
}

// TODO: ADD THIS LOGIC
/*
    private boolean processDataReceived(SocketChannel sc) throws IOException, InterruptedException {


        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketAddress address = sc.getRemoteAddress();
        buffer.clear();
        // TODO: should probably check if data was read
        sc.read(buffer);
        buffer.flip();  // flip from read to write

        // If no data, close the connection
        if (buffer.limit() == 0) {
            log("No data, closing connection %s", 1, address);
            return false;
        }

        //log("Processing %d bytes from %s", 1, buffer.limit(), address);

        NioClient client = clientMap.get(sc);
        ZatackaCodec cc = new ZatackaCodec(buffer);

        byte messageType;
        while ((messageType = cc.hasNext()) != 0) {
            switch (messageType) {
p
                case MessageType.AUTH_REQUEST : handleAuthRequest(cc.decodeAuthRequest(), client); break;
                case MessageType.PING_REQUEST : handlePingRequest(cc.decodePingRequest(), client); break;
                case MessageType.LOBBY_REQUEST : handleLobbyRequest(cc.decodeLobbyRequest(), client); break;

                case MessageType.LOBBY_JOIN_REQUEST : WaitingRoom.getInstance().handleLobbyJoinRequest(cc.decodeLobbyJoinRequest(), client); break;
                case MessageType.QUEUE_JOIN_REQUEST : WaitingRoom.getInstance().handleJoinQueueRequest(client); break;

                case MessageType.GAME_START_REQUEST: WaitingRoom.getInstance().handleGameStartRequest(client); break; // Lobby owner requests lobby to launch into game

                case MessageType.GAME_PLAYER_UPDATE : GameManager.getInstance().handlePlayerUpdate(cc.decodePlayerUpdate(), client); break; // client in game updates server on his positions
            }
        }

        return true;
    }

 */

/*


    // TODO: consider handling IOException in this function to stop errors from propagating further up (handle more data without erorrs)
    private void handleLobbyRequest(int[] actionTargetPair, NioClient client) throws IOException{
        int action = actionTargetPair[0];
        int targetID = actionTargetPair[1];

        ZatackaCodec cc = new ZatackaCodec(4096);
        if (client.isAuthenticated()) {

            switch (action) {
                case 1: // REQUEST LOBBY LIST : REPLY 17
                    cc.encodeLobbyList(WaitingRoom.getInstance().getJoinableLobbyList(client));
                    client.write(cc);
                    break;
                case 2: // REQUEST JOIN LOBBY : REPLY 18
                    cc.encodeLobbyInfo(WaitingRoom.getInstance().getLobbyByID(targetID));
                    client.write(cc);
                    break;
                case 3: // REQUEST CREATE LOBBY : REPLY 18
                    cc.encodeLobbyInfo(WaitingRoom.getInstance().handleLobbyCreateRequest(client));
                    client.write(cc);
                    break;
            }
        } else {

            cc.encodeAuthResponse(AuthResponse.UNAUTHENTICATED);
            client.write(cc);

        }
    }
    private void handlePingRequest(int pingID, NioClient client) throws IOException {
        log("Received Ping request", 1);
        ZatackaCodec cc = new ZatackaCodec(32);
        if (client.isAuthenticated()) {
            cc.encodePingResponse(pingID);
        } else {
            cc.encodeAuthResponse(AuthResponse.UNAUTHENTICATED);
        }
        client.write(cc);
    }
 */

/*

    //TODO: consider searching for username before removing expired tokens to allow checking for expired tokens
    private data.ClientLoginInfo getLoginInfoByUsername(String username) {

        // remove expired tokens
        loginQueue.removeIf(loginInfo -> loginInfo.expiry.isBefore(LocalDateTime.now()));

        data.ClientLoginInfo result = null;

        for (data.ClientLoginInfo cli : loginQueue) {
            if (cli.username.equals(username)) {
                // we found our user info

                // check token expiry, TODO: consider if this is necessary, maybe do it in handling of auth request
                if (cli.expiry.isBefore(LocalDateTime.now())) {
                    //expired. Shouldn't happen if expired tokens are removed before this loop.
                    log("login token expired for username %s", 3, username);
                    return null;
                } else {
                    result = cli;
                    break;
                }
            }
        }

        if (result != null) {
            loginQueue.remove(result);
        }
        return result;
    }

 */



